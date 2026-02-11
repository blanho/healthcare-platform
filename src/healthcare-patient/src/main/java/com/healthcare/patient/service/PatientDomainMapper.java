package com.healthcare.patient.service;

import com.healthcare.patient.api.dto.PatientMapper;
import com.healthcare.patient.domain.Address;
import com.healthcare.patient.domain.EmergencyContact;
import com.healthcare.patient.domain.Insurance;
import com.healthcare.patient.domain.Patient;
import com.healthcare.patient.api.dto.AddressDto;
import com.healthcare.patient.api.dto.EmergencyContactDto;
import com.healthcare.patient.api.dto.InsuranceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PatientDomainMapper {

    Address toAddress(AddressDto dto);

    AddressDto toAddressDto(Address address);

    @Mapping(target = "isActive", expression = "java(insurance != null && insurance.isActive())")
    InsuranceDto toInsuranceDto(Insurance insurance);

    @Mapping(target = "effectiveDate", source = "effectiveDate")
    @Mapping(target = "expirationDate", source = "expirationDate")
    Insurance toInsurance(InsuranceDto dto);

    EmergencyContact toEmergencyContact(EmergencyContactDto dto);

    EmergencyContactDto toEmergencyContactDto(EmergencyContact emergencyContact);

    void updateAddress(@MappingTarget Address address, AddressDto dto);
}
