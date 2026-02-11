package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.Patient;
import com.healthcare.patient.service.PatientDomainMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {PatientDomainMapper.class}
)
public interface PatientMapper {

    @Mapping(target = "fullName", expression = "java(patient.getFullName())")
    @Mapping(target = "age", expression = "java(patient.getAge())")
    @Mapping(target = "isMinor", expression = "java(patient.isMinor())")
    @Mapping(target = "hasActiveInsurance", expression = "java(patient.hasActiveInsurance())")
    PatientResponse toResponse(Patient patient);

    @Mapping(target = "fullName", expression = "java(patient.getFullName())")
    @Mapping(target = "age", expression = "java(patient.getAge())")
    PatientSummaryResponse toSummaryResponse(Patient patient);
}
