CREATE TABLE patients (
    id               UUID PRIMARY KEY,
    mrn              VARCHAR(32)  NOT NULL UNIQUE,
    name             VARCHAR(160) NOT NULL,
    age              INT          NOT NULL,
    sex              VARCHAR(16)  NOT NULL,
    phone            VARCHAR(32),
    address          VARCHAR(255),
    dob              DATE,
    next_of_kin_name  VARCHAR(160),
    next_of_kin_phone VARCHAR(32),
    nhis_number      VARCHAR(32),
    nhis_status      VARCHAR(24)  NOT NULL,
    blood_group      VARCHAR(8),
    allergies        VARCHAR(500),
    category         VARCHAR(24)  NOT NULL,
    last_visit       DATE,
    registered_by    VARCHAR(160),
    registered_on    DATE,
    status           VARCHAR(16)  NOT NULL,
    duplicate_flag   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL
);

CREATE INDEX idx_patients_name ON patients (name);
CREATE INDEX idx_patients_status ON patients (status);
