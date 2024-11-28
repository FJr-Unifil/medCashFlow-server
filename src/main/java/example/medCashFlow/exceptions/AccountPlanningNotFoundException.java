package example.medCashFlow.exceptions;

public class AccountPlanningNotFoundException extends RuntimeException {
    public AccountPlanningNotFoundException(String message) {
        super(message);
    }

    public AccountPlanningNotFoundException() {
        super("Plano de contas não encontrado");
    }
}
