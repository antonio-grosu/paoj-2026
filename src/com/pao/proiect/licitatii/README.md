# Sistem Licitatii — Etapa I

## 1.1 — Cele 10 actiuni / interogari posibile in sistem

1. Inregistrare utilizator (cumparator sau vanzator) in sistem
2. Adaugare produs nou de catre un vanzator
3. Creare licitatie pentru un produs, cu data de start si data de final
4. Plasare oferta (bid) de catre un cumparator pe o licitatie activa
5. Inchidere licitatie si determinarea ofertei castigatoare
6. Cautare utilizator dupa id
7. Listare produse sortate dupa pretul minim de pornire
8. Listare produse grupate pe categorie
9. Afisarea tuturor ofertelor unui cumparator
10. Afisarea produselor aflate in licitatii active

## 1.2 — Cele 8 tipuri de obiecte din domeniu

| Clasa | Descriere |
|---|---|
| `Utilizator` | Clasa abstracta de baza pentru orice participant in sistem |
| `Cumparator` | Utilizator care plaseaza oferte; are buget |
| `Vanzator` | Utilizator care listeaza produse; are IBAN |
| `Produs` | Obiectul scos la licitatie; are cod unic, categorie si pret minim |
| `CodProdus` | Clasa imutabila — identificator unic al unui produs |
| `Licitatie` | Evenimentul de licitare al unui produs; contine lista de oferte |
| `Oferta` | O suma propusa de un cumparator pentru o licitatie |
| `CategorieProdus` | Enum cu categoriile posibile: ELECTRONICE, ARTA, BIJUTERII etc. |
| `StareLicitatie` | Enum cu starile posibile: ACTIVA, INCHISA, ANULATA |

## Structura pachetelor

```
com.pao.proiect.licitatii/
├── Main.java
├── model/
│   ├── Utilizator.java       (clasa abstracta)
│   ├── Cumparator.java       (extends Utilizator)
│   ├── Vanzator.java         (extends Utilizator)
│   ├── Produs.java           (implements Comparable)
│   ├── CodProdus.java        (clasa imutabila)
│   ├── Licitatie.java
│   ├── Oferta.java
│   ├── CategorieProdus.java  (enum)
│   └── StareLicitatie.java   (enum)
├── service/
│   ├── UtilizatorService.java  (Singleton)
│   ├── ProdusService.java      (Singleton)
│   └── LicitatieService.java   (Singleton)
└── exception/
    ├── LicitatieInchisaException.java
    ├── OfertaInsuficientaException.java
    └── UtilizatorNegasitException.java
```
