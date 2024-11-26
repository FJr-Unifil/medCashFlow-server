package example.medCashFlow.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("Você não tem permissão de acesso nessa rota do sistema");
    }

}
