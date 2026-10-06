-- Ward & Bed Manager module.
--
--   wards               one row per ward/pavilion
--   beds                every physical bed; status is the live bed board
--   admissions          a patient's inpatient stay; bed_id is the bed they are in NOW
--   bed_transfers       history of bed moves within an admission
--   admission_requests  the "awaiting a bed" queue (ER, PACU, OPD ...)
--
-- Statuses are stored as VARCHAR with CHECK constraints (same approach as the
-- patients table) so they map straight onto Java enums with EnumType.STRING.

CREATE TABLE wards (
    id            UUID PRIMARY KEY,
    code          VARCHAR(8)   NOT NULL UNIQUE,
    name          VARCHAR(160) NOT NULL,
    short_name    VARCHAR(60)  NOT NULL,
    location      VARCHAR(160),
    charge_nurse  VARCHAR(160),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL
);

CREATE TABLE beds (
    id                 UUID PRIMARY KEY,
    ward_id            UUID         NOT NULL REFERENCES wards (id),
    code               VARCHAR(16)  NOT NULL UNIQUE,
    room               VARCHAR(160),
    status             VARCHAR(16)  NOT NULL
                       CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'RESERVED', 'CLEANING', 'MAINTENANCE')),
    isolation          BOOLEAN      NOT NULL DEFAULT FALSE,
    status_note        VARCHAR(255),
    status_changed_at  TIMESTAMP    NOT NULL,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL
);

CREATE INDEX idx_beds_ward ON beds (ward_id);
CREATE INDEX idx_beds_status ON beds (status);

CREATE TABLE admissions (
    id               UUID PRIMARY KEY,
    patient_id       UUID         NOT NULL REFERENCES patients (id),
    bed_id           UUID         NOT NULL REFERENCES beds (id),
    status           VARCHAR(20)  NOT NULL
                     CHECK (status IN ('ACTIVE', 'DISCHARGE_READY', 'DISCHARGED')),
    diagnosis        VARCHAR(255) NOT NULL,
    attending        VARCHAR(160),
    notes            VARCHAR(500),
    admitted_at      TIMESTAMP    NOT NULL,
    admitted_by      VARCHAR(160),
    discharged_at    TIMESTAMP,
    discharged_by    VARCHAR(160),
    discharge_notes  VARCHAR(500),
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    CHECK ((status = 'DISCHARGED') = (discharged_at IS NOT NULL))
);

-- The database itself guarantees no double-booking: at most one current
-- (not discharged) admission per bed, and per patient.
CREATE UNIQUE INDEX ux_admissions_current_bed ON admissions (bed_id) WHERE status <> 'DISCHARGED';
CREATE UNIQUE INDEX ux_admissions_current_patient ON admissions (patient_id) WHERE status <> 'DISCHARGED';
CREATE INDEX idx_admissions_status ON admissions (status);

CREATE TABLE bed_transfers (
    id              UUID PRIMARY KEY,
    admission_id    UUID         NOT NULL REFERENCES admissions (id),
    from_bed_id     UUID         NOT NULL REFERENCES beds (id),
    to_bed_id       UUID         NOT NULL REFERENCES beds (id),
    reason          VARCHAR(255),
    transferred_by  VARCHAR(160),
    transferred_at  TIMESTAMP    NOT NULL,
    CHECK (from_bed_id <> to_bed_id)
);

CREATE INDEX idx_bed_transfers_admission ON bed_transfers (admission_id);

CREATE TABLE admission_requests (
    id                 UUID PRIMARY KEY,
    patient_id         UUID         NOT NULL REFERENCES patients (id),
    source             VARCHAR(120) NOT NULL,
    urgency            VARCHAR(12)  NOT NULL CHECK (urgency IN ('CRITICAL', 'URGENT', 'ROUTINE')),
    diagnosis          VARCHAR(255) NOT NULL,
    notes              VARCHAR(500),
    preferred_ward_id  UUID REFERENCES wards (id),
    suggested_bed_id   UUID REFERENCES beds (id),
    status             VARCHAR(12)  NOT NULL CHECK (status IN ('PENDING', 'ADMITTED', 'CANCELLED')),
    requested_at       TIMESTAMP    NOT NULL,
    requested_by       VARCHAR(160),
    admission_id       UUID REFERENCES admissions (id),
    resolved_at        TIMESTAMP,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP    NOT NULL
);

-- One open request per patient at a time.
CREATE UNIQUE INDEX ux_admission_requests_pending_patient ON admission_requests (patient_id) WHERE status = 'PENDING';
CREATE INDEX idx_admission_requests_status ON admission_requests (status);
