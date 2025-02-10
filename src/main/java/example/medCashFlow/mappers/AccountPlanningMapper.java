package example.medCashFlow.mappers;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Clinic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountPlanningMapper {

    @Mapping(source = "clinic.id", target = "clinicId")
    AccountPlanningResponseDTO toResponseDTO(AccountPlanning accountPlanning);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "data.name")
    AccountPlanning toAccountPlanning(AccountPlanningRegisterDTO data, Clinic clinic);
}
