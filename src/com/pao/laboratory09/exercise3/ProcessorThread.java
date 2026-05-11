package com.pao.laboratory09.exercise3;

public class ProcessorThread implements Runnable {
    public volatile boolean activ = true;
    private final CoadaTranzactii coada;

    public ProcessorThread(CoadaTranzactii coada) {
        this.coada = coada;
    }

    @Override
    public void run() {
        while (activ || !coada.isEmpty()) {
            try {
                if (coada.isEmpty() && !activ) break;
                Tranzactie t = coada.extrage();
                System.out.printf("[Processor] Factura #%d - %.2f RON | %s%n",
                        t.getId(), t.getSuma(), t.getData());
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
