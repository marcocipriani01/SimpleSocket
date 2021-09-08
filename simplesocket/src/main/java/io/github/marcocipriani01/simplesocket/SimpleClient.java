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
import java.net.Socket;

/**
 * Client Socket for a local LAN Server Socket with event handling.
 *
 * @author marcocipriani01
 * @version 1.2
 */
@SuppressWarnings("unused")
public abstract class SimpleClient extends StringNetPort {

    /**
     * The server IP.
     */
    private volatile String address = null;
    private volatile Socket socket;
    /**
     * Output.
     */
    private volatile PrintWriter out;

    /**
     * Starts the connection to the client/server.
     */
    public void connect(String address, int port) {
        if (connected)
            throw new IllegalStateException("Already connected!");
        this.port = port;
        this.address = address;
        handler.post(() -> {
            try {
                socket = new Socket(this.address, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(() -> {
                    try {
                        read(socket, reader);
                    } catch (Exception e) {
                        onError(e);
                        if (connected) disconnect();
                    }
                }, socket.getInetAddress() + " reader").start();
                connected = true;
                onConnected();
            } catch (Exception e) {
                onError(e);
            }
        });
    }

    /**
     * @return the server ip.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Invoked when a new message arrives from the server.
     */
    @Override
    public abstract void onMessage(Socket from, String msg);

    /**
     * Sends a message to the server.
     *
     * @param msg the message you want to send.
     */
    @Override
    public void print(String msg) {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    @Override
    public void println(String msg) {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void println(int msg) {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void print(int msg) {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    @Override
    public void println(boolean msg) {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void print(boolean msg) {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    /**
     * Closes the connection.
     */
    @Override
    protected void disconnect0() {
        try {
            socket.close();
        } catch (Exception e) {
            onError(e);
        }
        socket = null;
    }
}