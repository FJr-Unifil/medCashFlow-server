package example.medCashFlow.exceptions;

public class PaymentMethodNotFoundException extends ResourceNotFoundException {

    public PaymentMethodNotFoundException(String identifier) {
        super("Método de Pagamento", identifier);
    }

    public PaymentMethodNotFoundException() {
        super("Método de pagamento não encontrado");
    }
}
