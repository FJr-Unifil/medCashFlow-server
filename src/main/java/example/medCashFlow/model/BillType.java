package example.medCashFlow.model;

public enum BillType {
    INCOME,
    OUTCOME;

    public static BillType fromString(String type) {
        return BillType.valueOf(type.toUpperCase());
    }
}
