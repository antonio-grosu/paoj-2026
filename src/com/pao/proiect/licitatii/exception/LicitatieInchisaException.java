package com.pao.proiect.licitatii.exception;

public class LicitatieInchisaException extends Exception {
    public LicitatieInchisaException(int idLicitatie) {
        super("Licitatia cu id=" + idLicitatie + " nu mai este activa.");
    }
}
