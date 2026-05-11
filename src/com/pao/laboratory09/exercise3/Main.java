package com.pao.laboratory09.exercise3;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CoadaTranzactii coada = new CoadaTranzactii(5);
        ProcessorThread processorThread = new ProcessorThread(coada);
        Thread processor = new Thread(processorThread);

        ATMThread atm1 = new ATMThread(1, coada);
        ATMThread atm2 = new ATMThread(2, coada);
        ATMThread atm3 = new ATMThread(3, coada);

        processor.start();
        atm1.start();
        atm2.start();
        atm3.start();

        atm1.join();
        atm2.join();
        atm3.join();

        processorThread.activ = false;
        coada.notificaTerminat();
        processor.join();

        System.out.println("Toate tranzactiile procesate. Total: 12");
    }
}
