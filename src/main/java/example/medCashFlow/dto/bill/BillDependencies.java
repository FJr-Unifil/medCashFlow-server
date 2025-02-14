package example.medCashFlow.dto.bill;

import example.medCashFlow.model.AccountPlanning;
import example.medCashFlow.model.Involved;
import example.medCashFlow.model.PaymentMethod;

public record BillDependencies(
        Involved involved,
        AccountPlanning accountPlanning,
        PaymentMethod paymentMethod
) {}