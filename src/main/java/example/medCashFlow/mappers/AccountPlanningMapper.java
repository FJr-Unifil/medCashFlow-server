package example.medCashFlow.mappers;

import example.medCashFlow.dto.accountPlanning.AccountPlanningRegisterDTO;
import example.medCashFlow.dto.accountPlanning.AccountPlanningResponseDTO;
import example.medCashFlow.model.AccountPlanning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountPlanningMapper {

    @Mapping(source = "clinic.id", target = "clinicId")
    AccountPlanningResponseDTO toResponseDTO(AccountPlanning accountPlanning);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    AccountPlanning toAccountPlanning(AccountPlanningRegisterDTO accountPlanningRegisterDTO);
}
