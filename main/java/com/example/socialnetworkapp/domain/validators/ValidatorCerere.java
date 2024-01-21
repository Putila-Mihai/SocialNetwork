package com.example.socialnetworkapp.domain.validators;

import com.example.socialnetworkapp.domain.Cerere;
import com.example.socialnetworkapp.service.UserService;

public class ValidatorCerere implements Validator<Cerere>{

    @Override
    public void validate(Cerere entity) throws ValidationException {
        for ( Cerere c : UserService.getInstance().getAllCereri()){
            if(c.equals(entity)) throw new ValidationException("Cererea exista deja");
        }
    }
}
