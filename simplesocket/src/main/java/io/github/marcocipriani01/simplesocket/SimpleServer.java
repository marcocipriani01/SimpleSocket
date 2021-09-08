/*
 * Copyright 2021 Marco Cipriani (@marcocipriani01)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.marcocipriani01.simplesocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Server Socket for a local LAN server with event handling.
 *
 * @author marcocipriani01
 * @version 1.2
 */
@SuppressWarnings("unused")
public abstract class SimpleServer extends StringNetPort {

    /**
     * List of client sockets.
     */
    protected final HashMap<Socket, PrintWriter> clients = new HashMap<>();
    protected volatile ServerSocket serverSocket;

    public void connect(int port) {
        this.port = port;
        if (connected)
            throw new IllegalStateException("Already connected!");
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                connected = true;
                onConnected();
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
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        new Thread(() -> {
                            try {
                                read(socket, reader);
                            } catch (Exception e) {
                                try {
                                    socket.close();
                                } catch (Exception ex) {
                                    onError(ex);
                                }
                                synchronized (clients) {
                                    clients.remove(socket);
                                }
                                onClientRemoved(socket);
                            }
                        }, socket.getInetAddress() + " reader").start();
                        onNewClient(socket);
                    } catch (Exception e) {
                        onError(e);
                    }
                }
            } catch (Exception e) {
                onError(e);
            }
        }, "SimpleServer connection").start();
    }

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    @Override
    public void println(String msg) {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Collection<PrintWriter> writers = clients.values();
                for (PrintWriter pw : writers) {
                    pw.println(msg);
                }
            }
        });
    }

    @Override
    public void println(int msg) {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Collection<PrintWriter> writers = clients.values();
                for (PrintWriter pw : writers) {
                    pw.println(msg);
                }
            }
        });
    }

    @Override
    public void print(int msg) {
        ensureConnection();
        handler.post(() -> {
            Collection<PrintWriter> writers = clients.values();
            for (PrintWriter pw : writers) {
                pw.print(msg);
            }
        });
    }

    @Override
    public void println(boolean msg) {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Collection<PrintWriter> writers = clients.values();
                for (PrintWriter pw : writers) {
                    pw.println(msg);
                }
            }
        });
    }

    @Override
    public void print(boolean msg) {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Collection<PrintWriter> writers = clients.values();
                for (PrintWriter pw : writers) {
                    pw.print(msg);
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
    public void print(String msg) {
        ensureConnection();
        handler.post(() -> {
            synchronized (clients) {
                Collection<PrintWriter> writers = clients.values();
                for (PrintWriter pw : writers) {
                    pw.print(msg);
                }
            }
        });
    }

    /**
     * Sends a message to a client.
     *
     * @param msg the message to send.
     */
    public void println(Socket client, String msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.println(msg));
    }

    /**
     * Sends a message to a client.
     *
     * @param msg the message to send.
     */
    public void print(Socket client, String msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.print(msg));
    }

    public void println(Socket client, int msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.println(msg));
    }

    public void print(Socket client, int msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.print(msg));
    }

    public void println(Socket client, boolean msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.println(msg));
    }

    public void print(Socket client, boolean msg) {
        ensureConnection();
        final PrintWriter pw;
        synchronized (clients) {
            pw = clients.get(client);
        }
        if (pw == null)
            throw new IllegalArgumentException("Not a client!");
        handler.post(() -> pw.print(msg));
    }

    /**
     * Closes the connection.
     */
    @Override
    protected void disconnect0() {
        synchronized (clients) {
            Set<Socket> sockets = clients.keySet();
            for (Socket s : sockets) {
                try {
                    s.close();
                } catch (Exception e) {
                    onError(e);
                }
            }
            clients.clear();
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            onError(e);
        }
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
        synchronized (clients) {
            return clients.size();
        }
    }
}