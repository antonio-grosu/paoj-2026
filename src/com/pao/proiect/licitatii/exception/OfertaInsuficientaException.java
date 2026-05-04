package com.pao.proiect.licitatii.exception;

public class OfertaInsuficientaException extends Exception {
    public OfertaInsuficientaException(double ofertata, double minima) {
        super("Suma oferita (" + ofertata + ") este sub pretul minim acceptat (" + minima + ").");
    }
}
