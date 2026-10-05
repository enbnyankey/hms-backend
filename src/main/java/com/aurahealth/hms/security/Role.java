package com.aurahealth.hms.security;

/**
 * Mirrors the frontend's Role union in hms/lib/auth.tsx. Keep these two in
 * sync — this is the server-side source of truth once the Next.js app calls
 * this API instead of relying on client-only checks.
 */
public enum Role {
    admin,
    doctor,
    student_doctor,
    nurse,
    pharmacist,
    lab_tech,
    billing_clerk,
    front_desk,
    compliance
}
