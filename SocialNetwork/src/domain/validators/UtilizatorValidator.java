package domain.validators;

import domain.User;

public class UtilizatorValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if (entity.getFirstName().length() < 2 || entity.getLastName().length() < 2)
            throw new ValidationException("Numele si prenumele trebuie sa contina minimum " +
                    "2 character");
        boolean ok1 = entity.getLastName().matches("[a-zA-Z]+");
        boolean ok2 = (entity.getFirstName().matches("[a-zA-Z]+"));
        if (ok1 == false || ok2 == false)
            throw new ValidationException("Numele si prenumele trebuie sa contian doar litere");
    }
}

