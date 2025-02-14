package example.medCashFlow.mappers;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.model.*;
import example.medCashFlow.services.AccountPlanningService;
import example.medCashFlow.services.InvolvedService;
import example.medCashFlow.services.PaymentMethodService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface BillMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "data.name")
    @Mapping(target = "type", source = "data.type", qualifiedByName = "mapBillType")
    @Mapping(target = "clinic", source = "employee.clinic")
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "involved", source = "involved")
    @Mapping(target = "accountPlanning", source = "accountPlanning")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "installmentsAmount", source = "data.installments")
    @Mapping(target = "installments", ignore = true)
    Bill toBill(BillRegisterDTO data, Employee employee, Involved involved, AccountPlanning accountPlanning, PaymentMethod paymentMethod);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "data.name")
    @Mapping(target = "type", source = "data.type", qualifiedByName = "mapBillType")
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "involved", source = "involved")
    @Mapping(target = "accountPlanning", source = "accountPlanning")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "installmentsAmount", source = "data.installments")
    @Mapping(target = "installments", ignore = true)
    void updateBill(@MappingTarget Bill existingBill, BillRegisterDTO data, Involved involved, AccountPlanning accountPlanning, PaymentMethod paymentMethod);

    @Named("mapBillType")
    default BillType mapBillType(String type) {
        return BillType.fromString(type);
    }

    @Mapping(target = "involvedId", source = "involved.id")
    @Mapping(target = "accountPlanningId", source = "accountPlanning.id")
    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "installments", source = "installmentsAmount")
    BillOnlyResponseDTO toBillOnlyResponseDTO(Bill bill);

}
