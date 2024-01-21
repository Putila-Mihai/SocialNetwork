package com.example.socialnetworkapp.domain.validators;

import com.example.socialnetworkapp.domain.User;

public class ValidatorUser implements Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
        validateEmail(entity.getEmail());
    }

    private void validateFirstName(String firstName) throws ValidationException {
        if(firstName.isEmpty())
            throw new ValidationException("0Numele nu trebuie sa fie nul!");
        else if(firstName.length() >= 30)
            throw new ValidationException("0Numele este pre lung");
        else if(! Character.isAlphabetic(firstName.charAt(0)))
            throw new ValidationException("0Prima litera trebuie sa fie o litera");
    }
    private void validateLastName(String lastName) throws ValidationException {
        if(lastName.isEmpty())
            throw new ValidationException("1Numele nu trebuie sa fie nul!");
        else if(lastName.length() >= 100)
            throw new ValidationException("1Numele este pre lung");
        else if(! Character.isAlphabetic(lastName.charAt(0)))
            throw new ValidationException("1Prima litera trebuie sa fie litera");
    }
    private void validateEmail(String email) throws ValidationException {
        if(email.isEmpty())
            throw new ValidationException("Email-ul nu trebuie sa fie nul");
        else if(email.length() >= 100)
            throw new ValidationException("email prea lung");
        else if(email.chars().filter(ch -> ch == '@').count() != 1)
            throw new ValidationException("Email-ul trebuie sa fie de forma ...@...");
    }
}
