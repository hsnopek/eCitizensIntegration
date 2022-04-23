package hr.hsnopek.ecitizensintegration.domain.feature.authorization.exceptions;

public class RegistrationFormException extends RuntimeException{

    private static final long serialVersionUID = -1L;

    public RegistrationFormException(String message) {
        super(message);
    }
}