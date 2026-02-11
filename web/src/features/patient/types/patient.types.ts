import type { Gender, BloodType, PatientStatus } from '@/types';

export interface CreatePatientRequest {
  firstName: string;
  middleName?: string;
  lastName: string;
  dateOfBirth: string;
  gender: Gender;
  bloodType?: BloodType;
  email: string;
  phoneNumber: string;
  secondaryPhone?: string;
  socialSecurityNumber?: string;
  address?: AddressDto;
  insurance?: InsuranceDto;
  emergencyContact?: EmergencyContactDto;
}

export interface UpdatePatientRequest {
  firstName?: string;
  middleName?: string;
  lastName?: string;
  dateOfBirth?: string;
  gender?: Gender;
  bloodType?: BloodType;
  email?: string;
  phoneNumber?: string;
  secondaryPhone?: string;
  address?: AddressDto;
  insurance?: InsuranceDto;
  emergencyContact?: EmergencyContactDto;
}

export interface PatientResponse {
  id: string;
  firstName: string;
  middleName: string | null;
  lastName: string;
  fullName: string;
  dateOfBirth: string;
  age: number;
  gender: Gender;
  bloodType: BloodType | null;
  email: string;
  phoneNumber: string;
  secondaryPhone: string | null;
  medicalRecordNumber: string;
  status: PatientStatus;
  isMinor: boolean;
  hasActiveInsurance: boolean;
  address: AddressDto | null;
  insurance: InsuranceDto | null;
  emergencyContact: EmergencyContactDto | null;
  createdAt: string;
  updatedAt: string;
}

export interface PatientSummaryResponse {
  id: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  medicalRecordNumber: string;
  dateOfBirth: string;
  age: number;
  status: PatientStatus;
}

export interface PatientSearchCriteria {
  name?: string;
  email?: string;
  phoneNumber?: string;
  medicalRecordNumber?: string;
  status?: PatientStatus;
}

export interface AddressDto {
  street?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
}

export interface InsuranceDto {
  providerName?: string;
  policyNumber?: string;
  groupNumber?: string;
  holderName?: string;
  holderRelationship?: string;
  effectiveDate?: string;
  expirationDate?: string;
  isActive?: boolean;
}

export interface EmergencyContactDto {
  name?: string;
  relationship?: string;
  phoneNumber?: string;
  email?: string;
}
