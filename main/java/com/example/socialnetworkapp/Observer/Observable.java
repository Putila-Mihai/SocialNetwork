package com.example.socialnetworkapp.Observer;

import java.util.ArrayList;

public interface Observable {
    ArrayList<Observer> observers = new ArrayList<Observer>();

    default void addObserver(Observer e)
    {
        observers.add(e);
    }
    default void notifyAllObservers() {
        observers.forEach(Observer::update);
    }
}
