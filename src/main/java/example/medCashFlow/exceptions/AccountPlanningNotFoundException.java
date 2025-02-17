package example.medCashFlow.exceptions;

public class AccountPlanningNotFoundException extends ResourceNotFoundException {

    public AccountPlanningNotFoundException(String identifier) {
        super("Plano de Contas", identifier);
    }

    public AccountPlanningNotFoundException() {
        super("Plano de contas não encontrado");
    }
}
