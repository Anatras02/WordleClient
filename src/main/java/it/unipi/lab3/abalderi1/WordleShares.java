package it.unipi.lab3.abalderi1;

import com.google.gson.Gson;
import it.unipi.lab3.abalderi1.config.ConfigHandler;
import it.unipi.lab3.abalderi1.data.Share;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class WordleShares implements Runnable {
    private static final WordleShares instance = new WordleShares();
    private final List<Share> shares = new ArrayList<>();

    private static final String MULTICAST_ADDRESS = ConfigHandler.getInstance().getProperty("multicastAddress", "224.0.0.1");
    private static final int MULTICAST_PORT = ConfigHandler.getInstance().getIntProperty("multicastAddress", 4446);
    private Thread thread;


    private WordleShares() {

    }

    public static WordleShares getInstance() {
        return instance;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void run() {
        MulticastSocket socket = null;
        InetAddress group;
        NetworkInterface networkInterface;
        InetSocketAddress inetSocketAddress;

        try {
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            inetSocketAddress = new InetSocketAddress(group, MULTICAST_PORT);
        } catch (UnknownHostException | SocketException e) {
            throw new RuntimeException(e);
        }

        try {
            socket = new MulticastSocket(MULTICAST_PORT);
            socket.setSoTimeout(1000);
            socket.joinGroup(inetSocketAddress, networkInterface);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] buffer = new byte[1000];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    try {
                        socket.receive(packet);

                        String message = new String(packet.getData(), 0, packet.getLength());
                        Share share = new Gson().fromJson(message, Share.class);

                        synchronized (this.shares) {
                            this.shares.add(share);
                        }
                    } catch (SocketException e) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                } catch (SocketTimeoutException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.leaveGroup(inetSocketAddress, networkInterface);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Share> getShares() {
        synchronized (this.shares) {
            return new ArrayList<>(this.shares);
        }
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }
}
