package example.medCashFlow.exceptions;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Você não tem permissão de acesso nessa rota do sistema");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
