package com.pao.laboratory07.exercise1.exceptions;

public class CannotCancelFinalOrderException extends RuntimeException {
    public CannotCancelFinalOrderException() {
        super("Cannot cancel a final state order.");
    }
}
