package com.example.socialnetworkapp.service;

public interface Service<E> {
    public String getEntities();

    public E removeEntity(Long id);
}
