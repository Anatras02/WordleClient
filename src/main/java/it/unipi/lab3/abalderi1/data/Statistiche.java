package it.unipi.lab3.abalderi1.data;

import java.util.HashMap;

public class Statistiche {
    private final int partiteGiocate;
    private final int partiteVinte;
    private final int streakVittorie;
    private final int maxStreakVittorie;
    private final HashMap<Integer, Integer> distribuzioneTentativi;

    public Statistiche(int partiteGiocate, int partiteVinte, int streakVittorie, int maxStreakVittorie, HashMap<Integer, Integer> distribuzioneTentativi) {
        this.partiteGiocate = partiteGiocate;
        this.partiteVinte = partiteVinte;
        this.streakVittorie = streakVittorie;
        this.maxStreakVittorie = maxStreakVittorie;
        this.distribuzioneTentativi = distribuzioneTentativi;
    }

    public String toString() {
        return "Partite giocate: " + partiteGiocate + "\n" +
                "Partite vinte: " + partiteVinte + "\n" +
                "Streak vittorie: " + streakVittorie + "\n" +
                "Max streak vittorie: " + maxStreakVittorie + "\n" +
                "Distribuzione tentativi: " + distribuzioneTentativi;
    }
}
