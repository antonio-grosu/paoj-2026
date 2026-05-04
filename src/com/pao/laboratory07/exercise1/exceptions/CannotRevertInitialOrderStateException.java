package com.pao.laboratory07.exercise1.exceptions;

public class CannotRevertInitialOrderStateException extends RuntimeException {
    public CannotRevertInitialOrderStateException() {
        super("Cannot undo the initial order state.");
    }
}
