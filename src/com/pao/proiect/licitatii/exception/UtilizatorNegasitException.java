package com.pao.proiect.licitatii.exception;

public class UtilizatorNegasitException extends Exception {
    public UtilizatorNegasitException(int id) {
        super("Utilizatorul cu id=" + id + " nu a fost gasit.");
    }
}
