package example.medCashFlow.mappers;

import example.medCashFlow.dto.clinic.ClinicRegisterDTO;
import example.medCashFlow.dto.clinic.ClinicResponseDTO;
import example.medCashFlow.model.Clinic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

    @Mapping(source = "active", target = "isActive")
    ClinicResponseDTO toResponseDTO(Clinic clinic);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Clinic toClinic(ClinicRegisterDTO clinicRegisterDTO);
}
