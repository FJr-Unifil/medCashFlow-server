package example.medCashFlow.mappers;

import example.medCashFlow.dto.involved.InvolvedRegisterDTO;
import example.medCashFlow.dto.involved.InvolvedResponseDTO;
import example.medCashFlow.model.Clinic;
import example.medCashFlow.model.Involved;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvolvedMapper {

    @Mapping(source = "active", target = "isActive")
    InvolvedResponseDTO toResponseDTO(Involved involved);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "name", source = "data.name")
    @Mapping(target = "phone", source = "data.phone")
    Involved toInvolved(InvolvedRegisterDTO data, Clinic clinic);
}
