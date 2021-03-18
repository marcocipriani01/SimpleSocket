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

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

/**
 * Network client or server base class.
 *
 * @param <MessageType> the message type.
 * @author marcocipriani01
 * @version 1.1
 * @see StringNetPort
 */
public abstract class NetPort<MessageType> {

    protected static final String TAG = "SimpleSocket";
    protected final HandlerThread thread = new HandlerThread("SimpleSocket thread");
    /**
     * Port.
     */
    protected volatile int port = -1;
    /**
     * Connection state.
     */
    protected volatile boolean connected = false;
    protected Handler handler;

    /**
     * Class constructor. Initializes the port without attempting a connection.
     */
    public NetPort() {
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    public void terminate() {
        if (connected)
            throw new IllegalStateException("Disconnect before terminating!");
        thread.quitSafely();
        handler = null;
    }

    protected abstract void onError(Exception e);

    /**
     * Sends a message to the server or clients.
     *
     * @param msg the message to send.
     */
    public abstract void print(MessageType msg) throws ConnectionException;

    /**
     * Invoked when a new message arrives from the server / client.
     */
    protected abstract void onMessage(Socket from, MessageType msg);

    /**
     * Invoke it to start reading.
     */
    protected abstract void read(Socket from, BufferedReader in) throws IOException;

    /**
     * @return the current TCP port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Closes the connection.
     */
    public void disconnect() throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            try {
                disconnect0();
                connected = false;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                onError(new ConnectionException("Cannot disconnect the socket!", e, ConnectionException.Type.UNABLE_TO_DISCONNECT));
            }
        });
    }

    /**
     * Closes the connection.
     */
    protected abstract void disconnect0() throws IOException;

    /**
     * Returns the connection state of the current client.
     *
     * @return the connection state, connected or not.
     */
    public boolean isConnected() {
        return connected;
    }

    protected final void ensureConnection() throws ConnectionException {
        if (!connected)
            throw new ConnectionException("Not connected!", ConnectionException.Type.NOT_CONNECTED);
    }
}