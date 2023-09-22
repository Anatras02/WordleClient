package it.unipi.lab3.abalderi1;

import it.unipi.lab3.abalderi1.api.WordleApi;
import it.unipi.lab3.abalderi1.api.exceptions.ApiException;
import it.unipi.lab3.abalderi1.data.Statistiche;
import it.unipi.lab3.abalderi1.data.User;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class WordleCLI {

    private final WordleApi api;
    private boolean isPlaying = false;

    public WordleCLI(OutputStreamWriter outputStreamWriter, InputStreamReader inputStreamReader) {
        this.api = new WordleApi(outputStreamWriter, inputStreamReader);
    }

    public void startMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Wordle CLI");
            System.out.println("1. Registrati");
            System.out.println("2. Login");
            System.out.println("3. Esci");
            System.out.print("Scegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            System.out.println();

            switch (choice) {
                case 1 -> {
                    System.out.print("Inseriscci username: ");
                    String username = scanner.nextLine();
                    System.out.print("Inserisci password: ");
                    String password = scanner.nextLine(); // TODO: Aggiungere inserimento password sicuro
                        System.out.print("Inserisci conferma password: ");
                    String confirmPassword = scanner.nextLine();

                    if (!password.equals(confirmPassword)) {
                        System.out.println("Le password non coincidono!");
                        break;
                    }

                    try {
                        User user = api.registrati(username, password);

                        loggedInMenu(user);
                    } catch (ApiException e) {
                        switch (e.getStatus()) {
                            case "MISSING" -> System.out.println(e.getMessage());
                            case "ALREADY_REGISTERED" -> System.out.println("Username già in uso!");
                            case "INVALID_PERMISSION" -> {
                                System.out.println("Non hai il permesso per eseguire questa azione, ti manca: " + e.getMessage());
                                isPlaying = false;
                            }
                            default -> System.out.println("Errore sconosciuto: " + e.getMessage());
                        }
                    }
                }
                case 2 -> {
                    System.out.print("Inseriscci username: ");
                    String username = scanner.nextLine();
                    System.out.print("Inserisci password: ");
                    String password = scanner.nextLine(); // TODO: Aggiungere inserimento password sicuro

                    try {
                        User user = api.login(username, password);


                        loggedInMenu(user);
                    } catch (ApiException e) {
                        switch (e.getStatus()) {
                            case "MISSING", "INVALID_LOGIN" -> System.out.println(e.getMessage());
                            case "INVALID_PERMISSION" -> {
                                System.out.println("Non hai il permesso per eseguire questa azione, ti manca: " + e.getMessage());
                                isPlaying = false;
                            }
                            default -> System.out.println("Errore sconosciuto: " + e.getMessage());
                        }
                    }
                }
                case 3 -> {
                    System.out.println("Arrivederci!");
                    return;
                }
                default -> System.out.println("Scelta non valida, riprova.");
            }
        }
    }

    public void loggedInMenu(User user) {
        System.out.println("Benvenuto " + user.getUsername() + "!");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();

            System.out.println("1. Inizia partita");
            System.out.println("2. Visualizza statistiche");
            System.out.println("3. Condividi risultato");
            System.out.println("4. Mostra risultati");
            System.out.println("5. Logout");
            System.out.print("Scegli un opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            System.out.println();

            switch (choice) {
                case 1 -> {
                    try {
                        api.inizializzaPartita();
                        isPlaying = true;

                        playGame(scanner);
                    } catch (ApiException e) {
                        switch (e.getStatus()) {
                            case "ALREADY_STARTED" -> System.out.println("Partita già iniziata!");
                            case "ALREADY_PLAYED" -> System.out.println("Hai già giocato oggi!");
                            default -> System.out.println("Errore sconosciuto: " + e.getMessage());
                        }
                    }
                }
                case 2 -> {
                        try {
                        Statistiche statistiche = api.getStatistiche();

                        System.out.println("Statistiche:");
                        System.out.println(statistiche.toString());

                        System.out.println("Premi invio per continuare...");
                        scanner.nextLine();
                    } catch (ApiException e) {
                        if (e.getStatus().equals("INVALID_PERMISSION")) {
                            System.out.println("Non hai il permesso per eseguire questa azione, ti manca: " + e.getMessage());
                        } else {
                            System.out.println("Errore sconosciuto: " + e.getMessage());
                        }
                    }
                }
                case 3 -> {
                    try {
                        api.condidiPartita();
                    } catch (ApiException e) {
                        switch (e.getStatus()) {
                            case "GENERIC_ERROR" -> System.out.println("Errore nella condivisione della partita");
                            case "INVALID_PERMISSION" -> {
                                System.out.println("Non hai il permesso per eseguire questa azione, ti manca: " + e.getMessage());
                            }
                            default -> System.out.println("Errore sconosciuto: " + e.getMessage());
                        }
                    }
                }
                case 4 -> WordleShares.getInstance().getShares().forEach(System.out::println);
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void playGame(Scanner scanner) {
        while (isPlaying) {
            System.out.print("Indovina la parola: ");
            String guessedWord = scanner.nextLine();

            try {
                api.inviaParola(guessedWord);

                System.out.println("Congratulazioni, hai indovinato la parola!");
                isPlaying = false;

                return;
            } catch (ApiException e) {
                switch (e.getStatus()) {
                    case "MISSING", "INVALID_WORD" -> System.out.println(e.getMessage());
                    case "GAMEOVER" -> {
                        System.out.println("Hai usato tutti i tentativi, la parola era: " + e.getMessage());
                        isPlaying = false;
                    }
                    case "ADVISE" ->
                            System.out.println("Consiglio: " + ColoredStringConverter.convert(guessedWord, e.getMessage()));
                    case "INVALID_PERMISSION" -> {
                        System.out.println("Non hai il permesso per eseguire questa azione, ti manca: " + e.getMessage());
                        isPlaying = false;
                    }
                    default -> System.out.println("Errore sconosciuto: " + e.getMessage());
                }
            }
        }
    }
}