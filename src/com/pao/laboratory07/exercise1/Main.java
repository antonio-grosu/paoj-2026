package com.pao.laboratory07.exercise1;

import com.pao.laboratory07.exercise1.exceptions.CannotCancelFinalOrderException;
import com.pao.laboratory07.exercise1.exceptions.CannotRevertInitialOrderStateException;
import com.pao.laboratory07.exercise1.exceptions.OrderIsAlreadyFinalException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            OrderState initialState = OrderState.valueOf(scanner.next());
            Order order = new Order(initialState);
            System.out.println("Initial order state: " + initialState);

            while (true) {
                OrderCommand orderCommand = OrderCommand.valueOf(scanner.next());
                switch (orderCommand) {
                    case next -> {
                        try {
                            order.nextState();
                            System.out.println("Order state updated to: " + order.getState());
                        } catch (OrderIsAlreadyFinalException e) {
                            System.out.println("Order is already in a final state.");
                        }
                    }
                    case cancel -> {
                        try {
                            order.cancel();
                            System.out.println("Order has been canceled.");
                        } catch (CannotCancelFinalOrderException e) {
                            System.out.println("Cannot cancel a final state order.");
                        }
                    }
                    case undo -> {
                        try {
                            order.undoState();
                            System.out.println("Order state reverted to: " + order.getState());
                        } catch (CannotRevertInitialOrderStateException e) {
                            System.out.println("Cannot undo the initial order state.");
                        }
                    }
                    case QUIT -> {
                        System.out.println("User quit the program.");
                        return;
                    }
                }
            }
        }
    }
}
