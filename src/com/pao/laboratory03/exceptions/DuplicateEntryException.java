package com.pao.laboratory03.exceptions;

public class DuplicateEntryException extends RuntimeException{

    public DuplicateEntryException(String name){
        super("'" + name+"' exista deja!");
    }
    
}
