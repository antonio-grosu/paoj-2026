package com.pao.laboratory13.exercise1;

/**
 * Motorul de protocol: parser + masina de stari pentru o sesiune de mesagerie.
 * O instanta = o sesiune independenta (folosita per client in bonus-ul ex2).
 */
public final class ProtocolEngine {
    private enum State { INIT, AUTH, OPEN, CLOSED }

    private State state = State.INIT;
    private int historyCount = 0;

    public String handle(String line) {
        String[] tok = line.trim().split("\\s+");
        String command = tok[0];

        // CLOSED este terminal: nicio comanda nu mai e acceptata.
        if (state == State.CLOSED) {
            return "ERR E_STATE CLOSED";
        }

        switch (command) {
            case "AUTH":      return auth(tok);
            case "OPEN":      return open(tok);
            case "SEND":      return send(tok);
            case "BROADCAST": return broadcast(tok);
            case "HISTORY":   return history(tok);
            case "CLOSE":     return close(tok);
            default:          return "ERR E_PARSE UNKNOWN_COMMAND";
        }
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    private String auth(String[] tok) {
        if (tok.length != 2) {
            return "ERR E_PARSE AUTH";
        }
        state = State.AUTH;
        historyCount = 0;
        return "OK AUTH user=" + tok[1];
    }

    private String open(String[] tok) {
        if (tok.length != 1) {
            return "ERR E_PARSE OPEN";
        }
        if (state == State.OPEN) {
            return "ERR E_STATE ALREADY_OPEN";
        }
        if (state != State.AUTH) {
            return "ERR E_STATE NOT_OPEN";
        }
        state = State.OPEN;
        return "OK OPEN";
    }

    private String send(String[] tok) {
        if (tok.length < 2) {
            return "ERR E_PARSE SEND";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        historyCount++;
        return "OK OPEN sent";
    }

    private String broadcast(String[] tok) {
        if (tok.length < 2) {
            return "ERR E_PARSE BROADCAST";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        historyCount++;
        return "OK OPEN broadcast";
    }

    private String history(String[] tok) {
        if (tok.length != 1) {
            return "ERR E_PARSE HISTORY";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        return "OK OPEN history=" + historyCount;
    }

    private String close(String[] tok) {
        if (tok.length != 1) {
            return "ERR E_PARSE CLOSE";
        }
        if (state != State.OPEN) {
            return "ERR E_STATE NOT_OPEN";
        }
        state = State.CLOSED;
        return "OK CLOSED";
    }
}
