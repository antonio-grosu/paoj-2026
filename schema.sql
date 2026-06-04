-- Schema sistem licitatii — Etapa II (SQLite)
-- FK-urile sunt activate la conectare cu: PRAGMA foreign_keys = ON;
--
-- Pornirea normala ruleaza doar sectiunea CREATE (idempotenta).
-- Resetul complet (DROP) se face doar cu argumentul --reset / --demo.

-- === RESET (rulat doar la cerere) ===
-- Ordinea DROP conteaza: intai tabelele cu FK, apoi cele referite.
DROP TABLE IF EXISTS oferta;
DROP TABLE IF EXISTS licitatie;
DROP TABLE IF EXISTS produs;
DROP TABLE IF EXISTS utilizator;

-- === SCHEMA ===
-- Single-table inheritance: discriminator rol = CUMPARATOR / VANZATOR.
-- buget e relevant doar pentru cumparator, iban doar pentru vanzator.
CREATE TABLE IF NOT EXISTS utilizator (
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    rol   TEXT    NOT NULL,
    nume  TEXT    NOT NULL,
    email TEXT    NOT NULL,
    buget REAL,
    iban  TEXT
);

CREATE TABLE IF NOT EXISTS produs (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    cod         TEXT    NOT NULL UNIQUE,
    denumire    TEXT    NOT NULL,
    descriere   TEXT,
    categorie   TEXT    NOT NULL,
    pret_minim  REAL    NOT NULL,
    vanzator_id INTEGER NOT NULL,
    FOREIGN KEY (vanzator_id) REFERENCES utilizator(id)   -- FK #1
);

CREATE TABLE IF NOT EXISTS licitatie (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    produs_id  INTEGER NOT NULL,
    data_start TEXT    NOT NULL,                           -- ISO: yyyy-MM-ddTHH:mm:ss
    data_final TEXT    NOT NULL,
    stare      TEXT    NOT NULL,                           -- ACTIVA / INCHISA / ANULATA
    FOREIGN KEY (produs_id) REFERENCES produs(id)          -- FK #2
);

CREATE TABLE IF NOT EXISTS oferta (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    licitatie_id  INTEGER NOT NULL,
    cumparator_id INTEGER NOT NULL,
    suma          REAL    NOT NULL,
    data_oferta   TEXT    NOT NULL,
    FOREIGN KEY (licitatie_id)  REFERENCES licitatie(id),  -- FK #3
    FOREIGN KEY (cumparator_id) REFERENCES utilizator(id)  -- FK #4
);
