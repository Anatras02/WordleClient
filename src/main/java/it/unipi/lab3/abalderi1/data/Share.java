package it.unipi.lab3.abalderi1.data;

import java.util.List;

public class Share {
    String username;
    int tentativi;
    List<String> partita;

    public Share(String username, int tentativi, List<String> partita) {
        this.username = username;
        this.tentativi = tentativi;
        this.partita = partita;
    }

    @Override
    public String toString() {
        String partitaString = String.join("\n", partita);

        return "----- Share Info -----\n" +
                "Username: " + username + "\n" +
                "Tentativi: " + tentativi + "\n" +
                "Partita:\n" + partitaString + "\n" +
                "----------------------";
    }
}
