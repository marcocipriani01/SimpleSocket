package io.github.marcocipriani01.simplesocket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

/**
 * Server Socket for a local LAN server with event handling.
 *
 * @author marcocipriani01
 * @version 1.0
 */
public abstract class SimpleServer extends StringNetPort {

    /**
     * List of client sockets.
     */
    protected final HashMap<Socket, PrintWriter> clients = new HashMap<>();
    protected volatile ServerSocket serverSocket;

    /**
     * Class constructor. Initializes the client without attempting a connection.
     *
     * @param port the port of the new server.
     */
    public SimpleServer(int port) {
        super(port);
    }

    /**
     * Starts the socket and the connection.
     */
    @Override
    protected void connect0() throws IOException {
        serverSocket = new ServerSocket(port);
        connected = true;
        while (connected) {
            try {
                Socket socket = serverSocket.accept();
                if (!acceptClient(socket.getInetAddress())) {
                    socket.close();
                    continue;
                }
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clients) {
                    clients.put(socket, out);
                }
                startReading(socket, new BufferedReader(new InputStreamReader(socket.getInputStream())));
                onNewClient(socket);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    @Override
    public void println(String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Set<Socket> sockets = clients.keySet();
                for (Socket s : sockets) {
                    clients.get(s).println(msg);
                }
            }
        });
    }

    @Override
    public void println(int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Set<Socket> sockets = clients.keySet();
                for (Socket s : sockets) {
                    clients.get(s).println(msg);
                }
            }
        });
    }

    @Override
    public void print(int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            for (Socket s : clients.keySet()) {
                clients.get(s).print(msg);
            }
        });
    }

    @Override
    public void println(boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Set<Socket> sockets = clients.keySet();
                for (Socket s : sockets) {
                    clients.get(s).println(msg);
                }
            }
        });
    }

    @Override
    public void print(boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Set<Socket> sockets = clients.keySet();
                for (Socket s : sockets) {
                    clients.get(s).print(msg);
                }
            }
        });
    }

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    @Override
    public void print(String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Set<Socket> sockets = clients.keySet();
                for (Socket s : sockets) {
                    clients.get(s).print(msg);
                }
            }
        });
    }

    /**
     * Sends a message to a client.
     *
     * @param msg the message to send.
     */
    public void println(Socket client, String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).println(msg));
    }

    /**
     * Sends a message to a client.
     *
     * @param msg the message to send.
     */
    public void print(Socket client, String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).print(msg));
    }

    public void println(Socket client, int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).println(msg));
    }

    public void print(Socket client, int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).print(msg));
    }

    public void println(Socket client, boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).println(msg));
    }

    public void print(Socket client, boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> clients.get(client).print(msg));
    }

    @Override
    protected void startReading(Socket from, BufferedReader in) {
        new Thread(() -> read(from, in), from.toString() + " reader").start();
    }

    @Override
    protected void read(Socket from, BufferedReader in) {
        try {
            super.read(from, in);
        } catch (Exception e) {
            try {
                from.close();
            } catch (Exception ex) {
                Log.e(TAG, e.getMessage(), e);
            }
            synchronized (clients) {
                clients.remove(from);
            }
            onClientRemoved(from);
        }
    }

    /**
     * Closes the connection.
     */
    @Override
    protected void close0() throws IOException {
        synchronized (clients) {
            Set<Socket> sockets = clients.keySet();
            for (Socket s : sockets) {
                s.close();
            }
            clients.clear();
        }
        serverSocket.close();
        serverSocket = null;
    }

    /**
     * Invoked when a new message arrives from the clients.
     */
    @Override
    protected abstract void onMessage(Socket from, String msg);

    protected abstract boolean acceptClient(InetAddress address);

    protected abstract void onNewClient(Socket client);

    protected abstract void onClientRemoved(Socket client);

    public int getClientsCount() {
        return clients.size();
    }
}