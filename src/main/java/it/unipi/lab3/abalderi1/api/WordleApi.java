package it.unipi.lab3.abalderi1.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unipi.lab3.abalderi1.WordleShares;
import it.unipi.lab3.abalderi1.api.exceptions.ApiException;
import it.unipi.lab3.abalderi1.data.Statistiche;
import it.unipi.lab3.abalderi1.data.User;

import java.io.*;
import java.util.function.Function;

public class WordleApi {
    private final OutputStreamWriter outputStreamWriter;
    private final InputStreamReader inputStreamReader;
    private final Gson gson = new Gson();

    public WordleApi(OutputStreamWriter outputStreamWriter, InputStreamReader inputStreamReader) {
        this.outputStreamWriter = outputStreamWriter;
        this.inputStreamReader = inputStreamReader;
    }

    private String readFromSocket(InputStreamReader inputStreamReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int c;

        while ((c = inputStreamReader.read()) != '\n') {
            stringBuilder.append((char) c);
        }

        return stringBuilder.toString();
    }

    private void writeToSocket(Writer outputStreamWriter, String message) throws Exception {
        outputStreamWriter.write(message + "\n");
        outputStreamWriter.flush();
    }

    private JsonObject runCommandAndReturnResponseAsJson(Writer outputStreamWriter, InputStreamReader inputStreamReader, String command) {
        Gson gson = new Gson();

        try {
            writeToSocket(outputStreamWriter, command);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'invio del comando al server: " + command, e);
        }

        String response;
        try {
            response = readFromSocket(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException("Errore durante la lettura della risposta del server: " + command, e);
        }

        try {
            return gson.fromJson(response, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il parsing della risposta del server: " + response, e);
        }
    }

    private <T> T handleResponse(JsonObject response, Function<JsonObject, T> onSuccess) throws ApiException {
        String status = response.get("status").getAsString();
        JsonElement body = response.get("body");

        if (body == null) {
            throw new ApiException("MISSING", "Il server non ha inviato il body");
        }

        if (body.isJsonObject()) {
            JsonObject bodyObject = body.getAsJsonObject();

            if (status.equals("SUCCESS")) {
                return onSuccess.apply(bodyObject);
            } else {
                throw new ApiException(status, bodyObject.get("message").getAsString());
            }
        } else {
            throw new ApiException("MISSING", "Il body non è un oggetto");
        }
    }

    private void startWordleSharesThread() {
        WordleShares wordleShares = WordleShares.getInstance();

        Thread thread = new Thread(wordleShares);
        thread.start();

        wordleShares.setThread(thread);
    }

    public boolean isPartitaIniziata() {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "is_partita_gia_iniziata?"
        );

        return response.get("status").getAsString().equals("TRUE");

    }

    public User registrati(String username, String password) throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "registration?username=" + username + ",password=" + password
        );


        return handleResponse(
                response,
                (body) -> {
                    startWordleSharesThread();

                    return gson.fromJson(body.get("message").getAsString(), User.class);
                }
        );
    }

    public User login(String username, String password) throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "login?username=" + username + ",password=" + password
        );

        return handleResponse(
                response,
                (body) -> {
                    startWordleSharesThread();

                    return gson.fromJson(body.get("message").getAsString(), User.class);
                }
        );
    }

    public void inizializzaPartita() throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "play?"
        );

        handleResponse(
                response,
                (body) -> true
        );
    }

    public void inviaParola(String parola) throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "send_word?word=" + parola
        );

        handleResponse(
                response,
                (body) -> true
        );
    }


    public Statistiche getStatistiche() throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "statistic?"
        );

        return handleResponse(
                response,
                (body) -> gson.fromJson(body.get("message").getAsString(), Statistiche.class)
        );
    }

    public void condidiPartita() throws ApiException {
        JsonObject response = runCommandAndReturnResponseAsJson(
                outputStreamWriter,
                inputStreamReader,
                "share?"
        );

        handleResponse(
                response,
                (body) -> true
        );
    }
}

