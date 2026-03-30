package com.pao.laboratory05.biblioteca;
import java.util.*;

public class BibliotecaService {

    private static BibliotecaService instance;
    private Carte[] carti = new Carte[0];

    private BibliotecaService() {
    }

    public static BibliotecaService getInstance() {
        if (instance == null) {
            instance = new BibliotecaService();
        }
        return instance;
    }

    void addCarte(Carte carte){
        Carte[] newCarti = new Carte[carti.length + 1];
        System.arraycopy(carti, 0, newCarti, 0, carti.length);
        newCarti[carti.length] = carte;
        carti = newCarti;
        System.out.println("Carte adăugată: " + carte.getTitlu());
    }

    void listSortedByRating(){
        Carte[] copy = new Carte[carti.length];
        System.arraycopy(carti, 0, copy, 0, carti.length);
        Arrays.sort(copy);
        for (Carte carte : copy) {
            System.out.println(carte);
        }
    }

    void listSortedBy(Comparator<Carte> comparator){
        Carte[] copy = new Carte[carti.length];

        System.arraycopy(carti, 0, copy, 0, carti.length);

        Arrays.sort(copy, comparator);
        for (Carte carte : copy) {
            System.out.println(carte);
        }
    }
}
