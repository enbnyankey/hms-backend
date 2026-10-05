-- Demo accounts, one per role, so the login page can authenticate against a
-- real users table instead of trusting a client-supplied role. All accounts
-- share the same demo password: Passw0rd!
--
-- The hash below is a real BCrypt hash of "Passw0rd!" (generated with
-- Python's bcrypt library, 10 rounds) — BCryptPasswordEncoder accepts $2a/$2b
-- interchangeably, so this verifies correctly against Spring Security's
-- BCryptPasswordEncoder at login time.
INSERT INTO users (id, name, email, password_hash, role, active, created_at) VALUES
    ('7db2ea10-b103-4ae7-ba65-5f3dc3415869', 'Nana Adjei',        'admin@aurahealth.demo',          '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'admin',          TRUE, NOW()),
    ('fb65941a-6b05-4d0e-b2a1-b79498fd2719', 'Dr. Ama Boateng',   'doctor@aurahealth.demo',         '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'doctor',         TRUE, NOW()),
    ('28cec7a1-868d-4de8-a5c4-57cdd1c1d38b', 'Kwame Mensah',      'student.doctor@aurahealth.demo', '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'student_doctor', TRUE, NOW()),
    ('44955a13-12b9-4c66-a753-18cb1f9f28e5', 'Efua Owusu',        'nurse@aurahealth.demo',          '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'nurse',          TRUE, NOW()),
    ('ceb1a7d7-3758-4045-80e1-1920b2e9c164', 'Yaw Darko',         'pharmacist@aurahealth.demo',     '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'pharmacist',     TRUE, NOW()),
    ('9e2ef8ba-d14e-45cd-86cb-4f0a29d8aad8', 'Abena Sarpong',     'labtech@aurahealth.demo',        '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'lab_tech',       TRUE, NOW()),
    ('3e0b0bca-7611-4b53-9b68-0539235063a4', 'Kojo Antwi',        'billing@aurahealth.demo',        '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'billing_clerk',  TRUE, NOW()),
    ('7dd36c9a-d77d-4e6b-9ef7-1ce6018047e7', 'Akosua Frimpong',   'frontdesk@aurahealth.demo',      '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'front_desk',     TRUE, NOW()),
    ('af82622c-9352-4fbf-bee8-8ee75b74f602', 'Esi Appiah',        'compliance@aurahealth.demo',     '$2b$10$z2ozqiZOaShx5zNE9KhkP.MhRFJRMZcWSgHPA1gIHJP7dGnR16IWK', 'compliance',     TRUE, NOW());
