package it.unipi.lab3.abalderi1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Main {
    public static void main(String[] args) throws Exception {
        InetAddress inetAddress = InetAddress.getByName("wordle-server-container");

        WordleShares wordleShares = WordleShares.getInstance();

        try (Socket socket = new Socket(inetAddress, 3456)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            WordleCLI wordleCLI = new WordleCLI(outputStreamWriter, inputStreamReader);

            wordleCLI.startMenu();
        }

        wordleShares.stop();
    }
}