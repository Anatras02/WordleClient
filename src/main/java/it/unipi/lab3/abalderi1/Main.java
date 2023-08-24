package it.unipi.lab3.abalderi1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import it.unipi.lab3.abalderi1.data.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static String readFromSocket(InputStreamReader inputStreamReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int c;

        while ((c = inputStreamReader.read()) != '\n') {
            stringBuilder.append((char) c);
        }

        return stringBuilder.toString();
    }

    private static void writeToSocket(Writer outputStreamWriter, String message) throws Exception {
        outputStreamWriter.write(message + "\n");
        outputStreamWriter.flush();
    }

    static String readPassword() {
        Console console = System.console();
        if (console != null) {
            char[] password = console.readPassword();
            return new String(password);
        } else {
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine();
        }
    }

    private static JsonObject runCommandAndReturnResponseAsJson(Writer outputStreamWriter, InputStreamReader inputStreamReader, String command) throws Exception {
        Gson gson = new Gson();

        writeToSocket(outputStreamWriter, command);
        String response = readFromSocket(inputStreamReader);

        return gson.fromJson(response, JsonObject.class);
    }

    private static int getIntegerInput(Scanner scanner, int min, int max) {
        int input = 0;
        boolean isValid;

        do {
            System.out.print("Inserisci un numero intero: ");
            isValid = true;

            try {
                input = scanner.nextInt();

                if (input < min || input > max) {
                    isValid = false;
                }
            } catch (InputMismatchException e) {
                isValid = false;
            }

            if (!isValid) {
                System.out.println("Input non valido. Per favore inserisci un numero intero compreso tra " + min + " e " + max + ".");
            }
            scanner.nextLine();

        } while (!isValid);

        return input;
    }

    public static void main(String[] args) throws Exception {
        InetAddress inetAddress = InetAddress.getLoopbackAddress();

        try (Socket socket = new Socket(inetAddress, 3456)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            Gson gson = new Gson();

            System.out.println("Buongiorno, benvenuto su Wordle.");


            Scanner scanner = new Scanner(System.in);
            int input;
            User user = null;
            JsonObject response;

            do {
                System.out.println("Cosa vuoi fare?");
                System.out.println("1. Registrati");
                System.out.println("2. Accedi");

                input = getIntegerInput(scanner, 1, 2);


                System.out.println();

                switch (input) {
                    case 1:
                        System.out.print("Inserisci l'username: ");
                        String username = scanner.nextLine();

                        System.out.print("Inserisci la password: ");
                        String password = readPassword();

                        response = runCommandAndReturnResponseAsJson(
                                outputStreamWriter,
                                inputStreamReader,
                                "registration?username=" + username + ",password=" + password
                        );


                        if (response.get("status").getAsString().equals("200")) {
                            user = gson.fromJson(response.get("body").getAsString(), User.class);

                            System.out.println("Registrazione avvenuta con successo. Benvenuto " + user.getUsername() + "!");
                        } else {
                            String bodyString = response.get("body").getAsString();
                            JsonObject bodyObject = gson.fromJson(bodyString, JsonObject.class);

                            System.out.println("ERRORE: " + bodyObject.get("message").getAsString());
                        }
                        break;
                    case 2:
                        // implement login
                        System.out.print("Inserisci l'username: ");
                        username = scanner.nextLine();

                        System.out.print("Inserisci la password: ");
                        password = readPassword();

                        response = runCommandAndReturnResponseAsJson(
                                outputStreamWriter,
                                inputStreamReader,
                                "login?username=" + username + ",password=" + password
                        );

                        if (response.get("status").getAsString().equals("200")) {
                            user = gson.fromJson(response.get("body").getAsString(), User.class);

                            System.out.println("Login avvenuto con successo. Benvenuto " + user.getUsername() + "!");
                        } else {
                            String bodyString = response.get("body").getAsString();
                            JsonObject bodyObject = gson.fromJson(bodyString, JsonObject.class);

                            System.out.println("ERRORE: " + bodyObject.get("message").getAsString());
                        }
                        break;
                    default:
                        System.out.println("Input non valido.");
                        break;
                }

                System.out.println();
            } while (user == null);


            response = runCommandAndReturnResponseAsJson(
                    outputStreamWriter,
                    inputStreamReader,
                    "is_partita_gia_iniziata?"
            );


            if(response.get("status").getAsString().equals("FALSE")) {
                System.out.println("\nCosa vuoi fare?");
                System.out.println("1. Inizia partita");
                System.out.println("2. Mostra statistiche");
                System.out.println("3. Condividi partita");
                System.out.println("4. Mostra condivisioni");

                input = getIntegerInput(scanner, 1, 4);

                switch (input) {
                    case 1:
                        // implement play
                        response = runCommandAndReturnResponseAsJson(
                                outputStreamWriter,
                                inputStreamReader,
                                "play?"
                        );

                        if (response.get("status").getAsString().equals("200")) {
                            System.out.println("Partita iniziata con successo.");
                        } else {
                            String bodyString = response.get("body").getAsString();
                            JsonObject bodyObject = gson.fromJson(bodyString, JsonObject.class);

                            System.out.println("\nERRORE: " + bodyObject.get("message").getAsString());
                        }
                        break;
                    case 2:
                        // implement statistic
                        response = runCommandAndReturnResponseAsJson(
                                outputStreamWriter,
                                inputStreamReader,
                                "statistic?"
                        );

                        if (response.get("status").getAsString().equals("200")) {
                            System.out.println("Statistiche:");
                            System.out.println(response.get("body").getAsString());
                        } else {
                            String bodyString = response.get("body").getAsString();
                            JsonObject bodyObject = gson.fromJson(bodyString, JsonObject.class);

                            System.out.println("\nERRORE: " + bodyObject.get("message").getAsString());
                        }
                        break;
                    case 3:
                        // implement share
                        response = runCommandAndReturnResponseAsJson(
                                outputStreamWriter,
                                inputStreamReader,
                                "share?"
                        );

                        if (response.get("status").getAsString().equals("200")) {
                            System.out.println("Partita condivisa con successo.");
                        } else {
                            String bodyString = response.get("body").getAsString();
                            JsonObject bodyObject = gson.fromJson(bodyString, JsonObject.class);

                            System.out.println("\nERRORE: " + bodyObject.get("message").getAsString());
                        }
                        break;
                }
            }
            else {
                System.out.println("\nCosa vuoi fare?");

                System.out.println("1. Invia parola");
                System.out.println("2. Mostra statistiche");
            }






            /*

            runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "registration?username=anatras02,password=miameo12");

            runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "login?username=anatras02,password=miameo12");

            runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "play?");
                runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "send_word?word=hhsdbksaba");

            runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "statistic?");

            runCommandAndPrintResponse(outputStreamWriter, inputStreamReader, "share?");
             */

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}