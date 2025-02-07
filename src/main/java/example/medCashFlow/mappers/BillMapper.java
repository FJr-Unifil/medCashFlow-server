package example.medCashFlow.mappers;

import example.medCashFlow.dto.bill.BillOnlyResponseDTO;
import example.medCashFlow.dto.bill.BillRegisterDTO;
import example.medCashFlow.dto.bill.BillResponseDTO;
import example.medCashFlow.model.*;
import example.medCashFlow.services.AccountPlanningService;
import example.medCashFlow.services.InvolvedService;
import example.medCashFlow.services.PaymentMethodService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BillMapper {

    @Autowired
    protected InvolvedService involvedService;

    @Autowired
    protected AccountPlanningService accountPlanningService;

    @Autowired
    protected PaymentMethodService paymentMethodService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type")
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "clinic", ignore = true)
    @Mapping(target = "involved", source = "involvedId")
    @Mapping(target = "accountPlanning", source = "accountPlanningId")
    @Mapping(target = "paymentMethod", source = "paymentMethodId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "installmentsAmount", source = "installments")
    @Mapping(target = "installments", ignore = true)
    public abstract Bill toBill(BillRegisterDTO data);

    protected BillType mapBillType(String type) {
        return BillType.valueOf(type.toUpperCase());
    }

    protected Involved mapToInvolved(Long id) {
        return involvedService.getInvolvedById(id);
    }

    protected AccountPlanning mapToAccountPlanning(Long id) {
        return accountPlanningService.getAccountPlanningById(id);
    }

    protected PaymentMethod mapToPaymentMethod(Long id) {
        return paymentMethodService.getPaymentMethodById(id);
    }

    @Mapping(target = "involvedId", source = "involved.id")
    @Mapping(target = "accountPlanningId", source = "accountPlanning.id")
    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "installments", source = "installmentsAmount")
    public abstract BillOnlyResponseDTO toBillOnlyResponseDTO(Bill bill);

}
