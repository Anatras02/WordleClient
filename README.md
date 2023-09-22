# WordleClient

## Introduzione

Il progetto presentato è un'applicazione client per il gioco "Wordle", sviluppata con un'architettura modulare in Java. Questa architettura permette una chiara separazione delle responsabilità e una facile estensibilità.

---

### Main.java: Il Punto di Partenza

La classe `Main` serve come entry point dell'applicazione.

**Architettura e Flusso**:

- **Connessione al Server**: La classe inizia stabilendo una connessione con il server attraverso un socket. Questa connessione è essenziale per garantire una comunicazione bidirezionale tra client e server.
- **Singleton WordleShares**: Prima di avviare l'interfaccia utente, l'applicazione inizializza l'istanza singleton di `WordleShares` e la lancia su un thread separato.
- **Inizializzazione dell'Interfaccia CLI**: Una volta stabilita la connessione e inizializzato `WordleShares`, l'applicazione avvia l'interfaccia CLI, permettendo all'utente di interagire con il gioco.

---

### WordleCLI: L'Interfaccia Utente

`WordleCLI` rappresenta l'interfaccia utente dell'applicazione, progettata con un'architettura modulare.

**Architettura e Design**:

- **Separazione tra Logica e Presentazione**: La classe `WordleCLI` è stata progettata per separare la logica di elaborazione dalle operazioni di I/O. Questo design modulare facilita eventuali estensioni o modifiche future, come la migrazione verso un'interfaccia grafica.
- **Interazione con WordleApi**: `WordleCLI` interagisce direttamente con `WordleApi` per inviare richieste e ricevere risposte dal server. Questa interazione è gestita attraverso metodi ben definiti, garantendo una chiara separazione delle responsabilità.

---

### WordleShares: Gestione delle Condivisioni

### WordleShares.java

La classe `WordleShares` rappresenta un componente fondamentale dell'applicazione, responsabile della ricezione e memorizzazione delle condivisioni di Wordle da altri utenti attraverso una rete multicast.

**Caratteristiche Principali**:

- **Singleton Pattern**: `WordleShares` è implementato come un singleton, garantendo che ci sia una sola istanza di questa classe nell'intera applicazione. Questo design assicura che tutte le condivisioni ricevute siano centralizzate in un unico punto, rendendo più semplice l'accesso e la gestione da qualsiasi parte dell'applicazione.
- **Gestione Multicast**: La classe utilizza un indirizzo multicast e una porta specifica per ascoltare le condivisioni. Questo permette a più client di inviare e ricevere condivisioni simultaneamente, facilitando la comunicazione tra diversi utenti.
- **Thread Separato**: Dopo il login, `WordleShares` viene eseguito su un thread separato, permettendo di ascoltare le condivisioni in background senza interferire con altre operazioni dell'utente, il thread viene settato all’interno dell’oggetto per permettere di interromperlo in seguito mediante il metodo **`stop`**
- **Gestione Interruzioni**: La classe è stata progettata per rispondere agli interrupt. Questo permette di fermare in modo sicuro e pulito il thread quando l'applicazione viene chiusa o quando non è più necessario ricevere condivisioni.
- **Sincronizzazione**: L'accesso alla lista di condivisioni è sincronizzato per garantire la thread-safety, evitando potenziali problemi di concorrenza quando si accede o si modifica la lista da thread diversi.

---

### WordleApi: Comunicazione con il Server

`WordleApi` è il ponte tra l'applicazione client e il server, progettato con un'architettura modulare e scalabile.

**Architettura e Design**:

- **Model-Driven Design**: `WordleApi` utilizza un sistema di modelli per trasformare le risposte JSON del server in oggetti Java. Questa scelta architetturale semplifica l'elaborazione dei dati e garantisce una chiara separazione tra la logica di comunicazione e l'elaborazione dei dati.
- **Gestione Centralizzata delle Eccezioni**: Tutte le eccezioni relative alla comunicazione con il server sono gestite attraverso la classe `ApiException`. Questo design centralizzato facilita la gestione degli errori e fornisce un feedback dettagliato all'utente.