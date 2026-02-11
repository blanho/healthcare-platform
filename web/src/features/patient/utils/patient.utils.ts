

import type { PatientResponse, PatientSummaryResponse } from '../types';

export function formatPatientName(
  firstName: string,
  lastName: string,
  middleName?: string | null
): string {
  if (middleName) {
    return `${firstName} ${middleName} ${lastName}`;
  }
  return `${firstName} ${lastName}`;
}

export function getPatientInitials(patient: Pick<PatientResponse, 'firstName' | 'lastName'>): string {
  return `${patient.firstName[0] ?? ''}${patient.lastName[0] ?? ''}`.toUpperCase();
}

export function calculateAge(dateOfBirth: string | Date): number {
  const dob = typeof dateOfBirth === 'string' ? new Date(dateOfBirth) : dateOfBirth;
  const today = new Date();
  let age = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate())) {
    age--;
  }

  return age;
}

export function isMinor(dateOfBirth: string | Date): boolean {
  return calculateAge(dateOfBirth) < 18;
}

export function formatPhoneNumber(phone: string): string {
  const cleaned = phone.replace(/\D/g, '');

  if (cleaned.length === 10) {
    return `(${cleaned.slice(0, 3)}) ${cleaned.slice(3, 6)}-${cleaned.slice(6)}`;
  }

  if (cleaned.length === 11 && cleaned[0] === '1') {
    return `+1 (${cleaned.slice(1, 4)}) ${cleaned.slice(4, 7)}-${cleaned.slice(7)}`;
  }

  return phone;
}

export function maskSSN(ssn: string): string {
  if (!ssn || ssn.length < 4) return ssn;
  return `XXX-XX-${ssn.slice(-4)}`;
}

export function formatAddressOneLine(address: {
  street?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
} | null): string {
  if (!address) return '';

  const parts = [
    address.street,
    address.city,
    address.state,
    address.zipCode,
  ].filter(Boolean);

  return parts.join(', ');
}

export function formatAddressMultiLine(address: {
  street?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
} | null): string[] {
  if (!address) return [];

  const lines: string[] = [];

  if (address.street) {
    lines.push(address.street);
  }

  const cityStateZip = [
    address.city,
    address.state ? `${address.state}${address.zipCode ? ' ' + address.zipCode : ''}` : address.zipCode,
  ].filter(Boolean).join(', ');

  if (cityStateZip) {
    lines.push(cityStateZip);
  }

  if (address.country) {
    lines.push(address.country);
  }

  return lines;
}

export function hasCompleteContactInfo(patient: PatientResponse): boolean {
  return Boolean(
    patient.email &&
    patient.phoneNumber &&
    patient.address?.street &&
    patient.address?.city &&
    patient.address?.state &&
    patient.address?.zipCode
  );
}

export function hasEmergencyContact(patient: PatientResponse): boolean {
  return Boolean(
    patient.emergencyContact?.name &&
    patient.emergencyContact?.phoneNumber
  );
}

export function sortPatientsByName(
  patients: PatientSummaryResponse[],
  direction: 'asc' | 'desc' = 'asc'
): PatientSummaryResponse[] {
  return [...patients].sort((a, b) => {
    const comparison = a.fullName.localeCompare(b.fullName);
    return direction === 'asc' ? comparison : -comparison;
  });
}

export function filterPatientsByQuery(
  patients: PatientSummaryResponse[],
  query: string
): PatientSummaryResponse[] {
  if (!query.trim()) return patients;

  const lowerQuery = query.toLowerCase();

  return patients.filter((patient) =>
    patient.fullName.toLowerCase().includes(lowerQuery) ||
    patient.email.toLowerCase().includes(lowerQuery) ||
    patient.medicalRecordNumber.toLowerCase().includes(lowerQuery)
  );
}
