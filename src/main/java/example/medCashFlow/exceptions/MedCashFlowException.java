package example.medCashFlow.exceptions;

public abstract class MedCashFlowException extends RuntimeException {

    public MedCashFlowException(String message) {
        super(message);
    }
}
