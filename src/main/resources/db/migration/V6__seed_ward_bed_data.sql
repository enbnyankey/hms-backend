-- Seed data mirroring hms/lib/ward-data.ts so the Ward & Bed Manager screen
-- shows the same wards, beds, patients and admissions queue as the mock.
-- Every ward is given its full bed count (280 in total); beds the mock
-- doesn't describe individually are seeded as AVAILABLE.

INSERT INTO wards (id, code, name, short_name, location, charge_nurse, active, created_at, updated_at) VALUES
    ('533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E', 'Ward E – Intensive Care Unit (ICU)', 'ICU', 'Floor 2, West Wing', 'Sister Grace Mensah, RN', TRUE, NOW(), NOW()),
    ('04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A', 'Ward A – Male Medical Ward', 'Male Med', 'Floor 1, Block C', 'Brother Samuel Tetteh, RN', TRUE, NOW(), NOW()),
    ('454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B', 'Ward B – Female Surgical Ward', 'Fem Surg', 'Floor 1, Block D', 'Matron Faustina Armah, RN', TRUE, NOW(), NOW()),
    ('3333523c-f50e-5c7a-be6c-0f5a1b6a0ed7', 'C', 'Ward C – Pediatric Complex', 'Peds', NULL, 'Sr. Rita Adobea', TRUE, NOW(), NOW()),
    ('c2c4e5d7-9e78-5e01-a4ed-0e90565fc1ea', 'D', 'Ward D – Maternity / Labor Wing', 'Maternity', NULL, 'Principal Midwife Nsa Borkor', TRUE, NOW(), NOW());

-- Inpatients and queued patients from the ward board who aren't in V2's registry seed.
-- (Nana Kwaku Adjei is reused from V2.)
INSERT INTO patients (id, mrn, name, age, sex, nhis_number, nhis_status, allergies, category, last_visit, registered_by, registered_on, status, duplicate_flag, created_at, updated_at) VALUES
    ('5bfce7f2-9cbc-5ad5-9c3a-67ecdfe835d4', 'GH-2024-8219', 'Kofi Asante', 62, 'Male', '88219004', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('d40ef0ab-ccc5-5294-bd2f-888124e56d53', 'GH-2024-0142', 'Akua Darko', 45, 'Female', '90142117', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('87d73355-9763-5385-adf3-4424f2238613', 'GH-2024-4328', 'Kwabena Opoku', 56, 'Male', '74328810', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('5bfd84ae-7a9d-5aaa-bbcc-8564155837b6', 'GH-2024-5021', 'Joseph Nunoo', 60, 'Male', '65021933', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('96ac2d45-36b2-5061-90fe-882e0816b2a3', 'GH-2024-3391', 'Francis Baah', 59, 'Male', '83391207', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('4811e1bf-4efd-5d65-875f-52f80b04195f', 'GH-2024-9186', 'Bernard Appiah', 64, 'Male', NULL, 'Not_Enrolled', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('350baa0a-d09c-5c17-8b27-40f16dd1bb23', 'GH-2024-4918', 'Ebenezer Sowah', 59, 'Male', '64918355', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('46344e31-b17b-52c2-b162-6e2229d7b90d', 'GH-2024-2201', 'Yaa Amah', 38, 'Female', '22014471', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('9e4706fe-0e5a-5c40-a65d-2d66328929af', 'GH-2024-2202', 'Esi Osei', 44, 'Female', '22029908', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('d3d12b3a-c8c3-51c7-ad50-7135b0643809', 'GH-2024-2205', 'Mavis Cudjoe', 52, 'Female', NULL, 'Not_Enrolled', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('2b7a45a4-c46d-5e7f-a4ef-7b5d1ec9a0ba', 'GH-2024-2208', 'Dorcas Kyei', 29, 'Female', '22087316', 'Verified', 'None recorded', 'Inpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('4da80c3c-c7cf-5103-8447-0fc50178b74f', 'GH-2024-9281', 'Emmanuel Quaye', 68, 'Male', '29019281', 'Verified', 'None recorded', 'Walk_in', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('e040ca4f-ce49-53a4-b87a-fd4ec2c2b263', 'GH-2024-7342', 'Patricia Mensah', 34, 'Female', NULL, 'Not_Enrolled', 'None recorded', 'Referral', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW()),
    ('cb804f88-b9d5-5d77-87e9-b0b9d74a8bbf', 'GH-2024-2941', 'Yaw Ofori', 51, 'Male', '88102941', 'Verified', 'None recorded', 'Outpatient', CURRENT_DATE, 'Seed data', CURRENT_DATE, 'Active', FALSE, NOW(), NOW());

UPDATE patients SET category = 'Inpatient' WHERE id = '1d1918fb-ca52-5fcc-a3f4-e99661ccb976';

-- Beds described individually on the mock ward board.
INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at) VALUES
    ('f7154f88-c1b4-509b-8b7b-2950a913e746', '533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E-01', NULL, 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '28 hours', NOW(), NOW()),
    ('859c7089-85ad-5a20-888b-88a7655d10e2', '533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E-02', NULL, 'OCCUPIED', TRUE, 'Sepsis protocol — blood culture STAT', NOW() - INTERVAL '10 hours', NOW(), NOW()),
    ('ab9f8a72-67ae-5582-a5f7-c44910a3367f', '533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E-03', NULL, 'AVAILABLE', FALSE, 'Terminal clean verified · ventilator check OK', NOW() - INTERVAL '42 minutes', NOW(), NOW()),
    ('3c0a966f-1cfa-54c5-9610-b43dfcafc60f', '533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E-04', NULL, 'RESERVED', FALSE, 'Held for Kwame Appiah (58M), AVR in PACU recovery · ETA 25 mins', NOW() - INTERVAL '20 minutes', NOW(), NOW()),
    ('8ccb3348-ea1a-524f-a483-79bd37dc42c2', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-101-A', 'Room 101 — Acute Respiratory & Infectious Care', 'OCCUPIED', TRUE, NULL, NOW() - INTERVAL '5 days', NOW(), NOW()),
    ('fa927db8-e5e3-53d9-bb15-543ecc2f36e7', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-101-B', 'Room 101 — Acute Respiratory & Infectious Care', 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '1 day', NOW(), NOW()),
    ('999a73a9-3087-5152-8386-9f4dd461575d', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-101-C', 'Room 101 — Acute Respiratory & Infectious Care', 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '5 days', NOW(), NOW()),
    ('7b9e194f-a1c7-5ea4-964c-b0964312f654', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-101-D', 'Room 101 — Acute Respiratory & Infectious Care', 'CLEANING', FALSE, 'Linen swap & UV sanitization · Housekeeper: Joe Mensah', NOW() - INTERVAL '6 minutes', NOW(), NOW()),
    ('1a85158f-a469-51d0-b5f0-8e1cae98f668', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-102-A', 'Room 102 — General Cardiology & Stroke Step-Down', 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '4 days', NOW(), NOW()),
    ('a3a8759d-ac61-5240-bffe-c9951c93ed23', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-102-B', 'Room 102 — General Cardiology & Stroke Step-Down', 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '8 hours', NOW(), NOW()),
    ('5deb6eab-7538-504e-9b7e-243067a7cf2c', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-102-C', 'Room 102 — General Cardiology & Stroke Step-Down', 'AVAILABLE', FALSE, 'Oxygen pipeline certified', NOW() - INTERVAL '12 minutes', NOW(), NOW()),
    ('76a8f82b-e86f-55c8-888b-e68d9d9579f1', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-102-D', 'Room 102 — General Cardiology & Stroke Step-Down', 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '2 days', NOW(), NOW()),
    ('984316dc-2dff-5744-9e6d-700e8191949b', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-201', NULL, 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '2 days', NOW(), NOW()),
    ('36adb274-8d45-58a3-80f0-df4a8b824ea3', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-202', NULL, 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '1 day', NOW(), NOW()),
    ('353d7e03-faee-5d6c-9c28-0aad70cf2dd7', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-203', NULL, 'AVAILABLE', FALSE, NULL, NOW() - INTERVAL '3 hours', NOW(), NOW()),
    ('9d4defb2-d625-528c-81c6-d69a4a7c8bb3', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-204', NULL, 'AVAILABLE', FALSE, 'Cleaned', NOW() - INTERVAL '12 minutes', NOW(), NOW()),
    ('ea76511d-805f-5151-9576-91ba157fe620', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-205', NULL, 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '3 days', NOW(), NOW()),
    ('7de7e3c8-6624-54d5-80e2-3aafbe3a2287', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-206', NULL, 'RESERVED', FALSE, 'Theater hold', NOW() - INTERVAL '1 hour', NOW(), NOW()),
    ('7e0f6ddb-f22d-5543-bf1b-855d264fa8c6', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-207', NULL, 'AVAILABLE', FALSE, NULL, NOW() - INTERVAL '5 hours', NOW(), NOW()),
    ('38fcf8ab-265e-5e62-93c7-1861bd09a92a', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-208', NULL, 'OCCUPIED', FALSE, NULL, NOW() - INTERVAL '1 day', NOW(), NOW());

-- Remaining beds so each ward has its full capacity (ICU 12, A 64, B 58, C 46, D 100).
INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at)
SELECT gen_random_uuid(), '533136f5-d7e5-5372-a5dc-5036a8b7940b', 'E-' || LPAD(n::text, 2, '0'), NULL, 'AVAILABLE', FALSE, NULL, NOW(), NOW(), NOW()
FROM generate_series(5, 12) AS n;

INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at)
SELECT gen_random_uuid(), '04c860f6-c5c5-516a-bb18-3c03ce9c780b', 'A-' || (101 + (n - 1) / 4) || '-' || CHR(65 + (n - 1) % 4), 'Room ' || (101 + (n - 1) / 4), 'AVAILABLE', FALSE, NULL, NOW(), NOW(), NOW()
FROM generate_series(9, 64) AS n;

INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at)
SELECT gen_random_uuid(), '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', 'B-' || (200 + n), NULL, 'AVAILABLE', FALSE, NULL, NOW(), NOW(), NOW()
FROM generate_series(9, 58) AS n;

INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at)
SELECT gen_random_uuid(), '3333523c-f50e-5c7a-be6c-0f5a1b6a0ed7', 'C-' || (300 + n), NULL, 'AVAILABLE', FALSE, NULL, NOW(), NOW(), NOW()
FROM generate_series(1, 46) AS n;

INSERT INTO beds (id, ward_id, code, room, status, isolation, status_note, status_changed_at, created_at, updated_at)
SELECT gen_random_uuid(), 'c2c4e5d7-9e78-5e01-a4ed-0e90565fc1ea', 'D-' || (400 + n), NULL, 'AVAILABLE', FALSE, NULL, NOW(), NOW(), NOW()
FROM generate_series(1, 100) AS n;

INSERT INTO admissions (id, patient_id, bed_id, status, diagnosis, attending, notes, admitted_at, admitted_by, discharged_at, discharged_by, discharge_notes, created_at, updated_at) VALUES
    ('087db02c-9fe3-5e22-bcbb-1a0d4ec6261e', '5bfce7f2-9cbc-5ad5-9c3a-67ecdfe835d4', 'f7154f88-c1b4-509b-8b7b-2950a913e746', 'ACTIVE', 'Post-CABG Day 1', 'Dr. Kobina Asadu', NULL, NOW() - INTERVAL '28 hours', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('efb2b66f-bedf-52f6-8e55-4d2750be93c2', 'd40ef0ab-ccc5-5294-bd2f-888124e56d53', '859c7089-85ad-5a20-888b-88a7655d10e2', 'ACTIVE', 'Urosepsis / Septic Shock', 'Dr. E. Boateng', 'Sepsis protocol · Sr. Cynthia', NOW() - INTERVAL '10 hours', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('2868bc52-8197-5c91-9c68-6a89567ce1f7', '87d73355-9763-5385-adf3-4424f2238613', '8ccb3348-ea1a-524f-a483-79bd37dc42c2', 'ACTIVE', 'Community-Acquired Pneumonia', 'Dr. K. Frimpong', 'O2 2L/min · Cefotaxime IV · Meds due 15:00', NOW() - INTERVAL '5 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('1ddaaac2-23b0-5bbe-ba43-8bed6bab5488', '5bfd84ae-7a9d-5aaa-bbcc-8564155837b6', 'fa927db8-e5e3-53d9-bb15-543ecc2f36e7', 'ACTIVE', 'Hypertensive Crisis w/ Encephalopathy', 'Dr. Agyeman', 'Labetalol infusion · BP 148/94 (improving)', NOW() - INTERVAL '1 day', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('9c25876f-b27b-5c30-af10-dbf3ba849961', '96ac2d45-36b2-5061-90fe-882e0816b2a3', '999a73a9-3087-5152-8386-9f4dd461575d', 'DISCHARGE_READY', 'Uncomplicated Malaria & Gastroenteritis', NULL, 'Pharmacy bill cleared · NHIS approved · awaiting family transport', NOW() - INTERVAL '5 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('7d0c2d11-23d0-5f57-a6ea-21382f889888', '1d1918fb-ca52-5fcc-a3f4-e99661ccb976', '1a85158f-a469-51d0-b5f0-8e1cae98f668', 'ACTIVE', 'Ischemic CVA (Right Hemiparesis)', NULL, 'Physio at 14:00', NOW() - INTERVAL '4 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('c77ed51d-dc70-56d5-a58c-ed5a17e1bab2', '4811e1bf-4efd-5d65-875f-52f80b04195f', 'a3a8759d-ac61-5240-bffe-c9951c93ed23', 'ACTIVE', 'Acute Decompensated Heart Failure', NULL, 'Strict fluid chart', NOW() - INTERVAL '8 hours', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('9e6d505d-692f-528d-bf1e-77fa8fb849cd', '350baa0a-d09c-5c17-8b27-40f16dd1bb23', '76a8f82b-e86f-55c8-888b-e68d9d9579f1', 'ACTIVE', 'Uncontrolled Type 2 Diabetes / Foot Ulcer', NULL, 'Wound care 16:00', NOW() - INTERVAL '2 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('d0a726dc-1e10-53a3-bbda-63085f6e0d06', '46344e31-b17b-52c2-b162-6e2229d7b90d', '984316dc-2dff-5744-9e6d-700e8191949b', 'ACTIVE', 'Post-op Open Cholecystectomy', NULL, NULL, NOW() - INTERVAL '2 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('e73a7604-1008-5c6f-847c-91dc3dc6ecea', '9e4706fe-0e5a-5c40-a65d-2d66328929af', '36adb274-8d45-58a3-80f0-df4a8b824ea3', 'ACTIVE', 'Post-op Total Abdominal Hysterectomy', NULL, NULL, NOW() - INTERVAL '1 day', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('8528e792-7937-5ac3-baf8-1e6277c5a9a7', 'd3d12b3a-c8c3-51c7-ad50-7135b0643809', 'ea76511d-805f-5151-9576-91ba157fe620', 'ACTIVE', 'Post-op Thyroidectomy', NULL, NULL, NOW() - INTERVAL '3 days', 'Seed data', NULL, NULL, NULL, NOW(), NOW()),
    ('89f9434a-83b8-5615-9cb3-c119da0afce7', '2b7a45a4-c46d-5e7f-a4ef-7b5d1ec9a0ba', '38fcf8ab-265e-5e62-93c7-1861bd09a92a', 'ACTIVE', 'Post-op Caesarean Wound Revision', NULL, NULL, NOW() - INTERVAL '1 day', 'Seed data', NULL, NULL, NULL, NOW(), NOW());

-- Patients waiting for a bed.
INSERT INTO admission_requests (id, patient_id, source, urgency, diagnosis, notes, preferred_ward_id, suggested_bed_id, status, requested_at, requested_by, admission_id, resolved_at, created_at, updated_at) VALUES
    ('f1198300-5088-5a3e-958c-0d80a2490d38', '4da80c3c-c7cf-5103-8447-0fc50178b74f', 'Emergency Triage · Red', 'CRITICAL', 'Severe Bilateral Pneumonia w/ Hypoxia', 'ER Bed 04 · Requires high-flow O2 support. Preferred Ward A (Male Med) or Ward E ICU step-down.', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', '5deb6eab-7538-504e-9b7e-243067a7cf2c', 'PENDING', NOW() - INTERVAL '75 minutes', 'Seed data', NULL, NULL, NOW(), NOW()),
    ('543dd7d2-ace1-5dc5-8c9a-4dc84fa0ca3f', 'e040ca4f-ce49-53a4-b87a-fd4ec2c2b263', 'Theater Recovery / PACU', 'URGENT', 'Laparoscopic Appendectomy Post-Op', 'PACU Bay 2 · Private insurance · Vitals stable, Aldrete score 9/10. Ready for surgical ward bed transfer.', '454b4a62-eb4a-5fd9-97ca-2cd0f2c2d5ac', '353d7e03-faee-5d6c-9c28-0aad70cf2dd7', 'PENDING', NOW() - INTERVAL '35 minutes', 'Seed data', NULL, NULL, NOW(), NOW()),
    ('a5c5cb89-0215-5bad-a025-24857476982c', 'cb804f88-b9d5-5d77-87e9-b0b9d74a8bbf', 'Medical OPD Triage', 'ROUTINE', 'Diabetic Ketoacidosis (DKA)', 'OPD Bay 7 · Requires continuous IV insulin infusion & hourly glucometer protocol.', '04c860f6-c5c5-516a-bb18-3c03ce9c780b', NULL, 'PENDING', NOW() - INTERVAL '50 minutes', 'Seed data', NULL, NULL, NOW(), NOW());
