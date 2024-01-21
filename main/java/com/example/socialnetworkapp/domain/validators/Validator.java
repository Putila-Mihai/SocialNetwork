package com.example.socialnetworkapp.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}