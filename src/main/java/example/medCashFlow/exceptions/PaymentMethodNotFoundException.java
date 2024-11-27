package example.medCashFlow.exceptions;

public class PaymentMethodNotFoundException extends RuntimeException {
    public PaymentMethodNotFoundException(String message) {
        super(message);
    }

    public PaymentMethodNotFoundException() {
        super("Método de pagamento não encontrado");
    }
}
