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

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client Socket for a local LAN Server Socket with event handling.
 *
 * @author marcocipriani01
 * @version 1.1
 */
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
     * Class constructor. Initializes the client without attempting a connection.
     */
    public SimpleClient() {
        super();
    }

    /**
     * Starts the connection to the client/server.
     */
    public void connect(String address, int port) throws ConnectionException {
        this.port = port;
        this.address = address;
        if (connected)
            throw new ConnectionException("Already connected!", ConnectionException.Type.ALREADY_CONNECTED);
        handler.post(() -> {
            try {
                socket = new Socket(this.address, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                startReading(socket, new BufferedReader(new InputStreamReader(socket.getInputStream())));
                connected = true;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                onError(new ConnectionException("Cannot connect the client!", e, ConnectionException.Type.CONNECTION));
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
     * @throws ConnectionException if the client is not connected.
     */
    @Override
    public void print(String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    @Override
    public void println(String msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void println(int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void print(int msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    @Override
    public void println(boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.println(msg));
    }

    @Override
    public void print(boolean msg) throws ConnectionException {
        ensureConnection();
        handler.post(() -> out.print(msg));
    }

    /**
     * Closes the connection.
     */
    @Override
    protected void disconnect0() throws IOException {
        socket.close();
    }
}