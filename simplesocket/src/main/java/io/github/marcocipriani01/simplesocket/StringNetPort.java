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
import java.net.Socket;

/**
 * Implementation of the {@link NetPort} class to send and receive strings.
 *
 * @author marcocipriani01
 * @version 1.0
 * @see NetPort
 * @see SimpleServer
 * @see SimpleClient
 */
public abstract class StringNetPort extends NetPort<String> {

    /**
     * Class constructor. Initializes the port without attempting a connection.
     */
    public StringNetPort() {
        super();
    }

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    public abstract void println(String msg) throws ConnectionException;

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    public abstract void println(int msg) throws ConnectionException;

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    @Override
    public abstract void print(String msg) throws ConnectionException;

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    public abstract void print(int msg) throws ConnectionException;

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    public abstract void print(boolean msg) throws ConnectionException;

    /**
     * Sends a message to all the clients.
     *
     * @param msg the message to send.
     */
    public abstract void println(boolean msg) throws ConnectionException;

    protected void startReading(Socket from, BufferedReader in) {
        new Thread(() -> {
            try {
                read(from, in);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                onError(new ConnectionException("Reading error!", e, ConnectionException.Type.INPUT));
            }
        }, from.toString() + " reader").start();
    }

    /**
     * Invoke it to start reading.
     */
    @Override
    protected void read(Socket from, BufferedReader in) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            onMessage(from, inputLine);
        }
    }
}