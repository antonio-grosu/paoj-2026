package com.pao.laboratory03.exceptions;

public class InvalidAgeException extends RuntimeException{
    private final int age;

    public InvalidAgeException(int age){
        super("Vârsta " + age +" nu este validă (0-150)" );
        this.age = age;
    }

    public int getAge(){ return this.age; }

}
