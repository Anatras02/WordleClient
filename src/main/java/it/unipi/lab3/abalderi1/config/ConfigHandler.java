package it.unipi.lab3.abalderi1.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Classe per la gestione delle configurazioni da un file di proprietà.
 * Questa classe permette di caricare e recuperare proprietà da un file specificato.
 */
public class ConfigHandler {
    private final String filePath;
    private Properties properties;
    private boolean isLoaded = false;
    private static ConfigHandler instance;

    /**
     * Costruttore della classe ConfigHandler.
     * Carica le proprietà da un file specificato nel percorso.
     *
     * @param filePath Il percorso del file di proprietà da caricare.
     * @throws IOException Se ci sono problemi durante la lettura del file.
     */
    private ConfigHandler(String filePath) throws IOException {
        this.filePath = filePath;
        new Thread(this::loadPropertiesInBackground).start();

        properties = new Properties();
        FileInputStream input = new FileInputStream(filePath);
        properties.load(input);
        input.close();
    }

    public static ConfigHandler getInstance() {
        if (instance == null) {
            try {
                instance = new ConfigHandler("config.properties");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    /**
     * Carica le proprietà in un thread separato in background.
     */
    private void loadPropertiesInBackground() {
        try {
            properties = new Properties();
            FileInputStream input = new FileInputStream(filePath);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            properties = null;
        }

        synchronized (this) {
            isLoaded = true;
            this.notifyAll();
        }
    }

    /**
     * Garantisce che le proprietà siano state caricate prima di accedere ad esse.
     * Se le proprietà non sono ancora caricate, il thread corrente attende.
     *
     * @throws InterruptedException Se il thread corrente è interrotto mentre è in attesa.
     */
    private void ensurePropertiesLoaded() throws InterruptedException {
        synchronized (this) {
            while (!isLoaded) {
                this.wait();
            }
        }
    }

    /**
     * Recupera una proprietà dal file caricato come una stringa.
     *
     * @param key La chiave della proprietà da recuperare.
     * @return Il valore della proprietà come stringa.
     */
    public String getProperty(String key) {
        try {
            ensurePropertiesLoaded();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return properties.getProperty(key);
    }

    /**
     * Recupera una proprietà dal file caricato come un intero.
     *
     * @param key La chiave della proprietà da recuperare.
     * @return Il valore della proprietà come intero.
     * @throws NumberFormatException Se il valore della proprietà non può essere convertito in un intero.
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Recupera una proprietà dal file caricato come una stringa. Se la chiave non esiste, viene restituito un valore predefinito.
     *
     * @param key          La chiave della proprietà da recuperare.
     * @param defaultValue Il valore predefinito da restituire se la chiave non esiste.
     * @return Il valore della proprietà come stringa o il valore predefinito se la chiave non esiste.
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Recupera una proprietà dal file caricato come un intero. Se la chiave non esiste o non può essere convertita in un intero, viene restituito un valore predefinito.
     *
     * @param key          La chiave della proprietà da recuperare.
     * @param defaultValue Il valore predefinito da restituire se la chiave non esiste o non può essere convertita.
     * @return Il valore della proprietà come intero o il valore predefinito.
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
