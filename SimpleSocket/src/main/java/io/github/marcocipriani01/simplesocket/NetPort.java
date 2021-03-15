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
 * @version 1.0
 * @see StringNetPort
 */
public abstract class NetPort<MessageType> {

    protected static final String TAG = "SimpleSocket";
    /**
     * Port.
     */
    protected final int port;
    protected final HandlerThread thread = new HandlerThread("SimpleSocket thread");
    /**
     * Connection state.
     */
    protected volatile boolean connected = false;
    protected Handler handler;

    /**
     * Class constructor. Initializes the port without attempting a connection.
     *
     * @param port the port.
     */
    public NetPort(int port) {
        this.port = port;
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    /**
     * Starts the connection to the client/server.
     */
    public final void connect() throws ConnectionException {
        if (connected)
            throw new ConnectionException("Already connected!", ConnectionException.Type.ALREADY_CONNECTED);
        new Thread(() -> {
            try {
                connect0();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                onError(new ConnectionException("Cannot connect the socket!", e, ConnectionException.Type.CONNECTION));
            }
        }, TAG + " connection").start();
    }

    /**
     * Starts the connection to the client / server.
     */
    protected abstract void connect0() throws IOException;

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
    public final void close() throws ConnectionException {
        ensureConnection();
        handler.post(() -> {
            try {
                close0();
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
    protected abstract void close0() throws IOException;

    /**
     * Returns the connection state of the current client.
     *
     * @return the connection state, connected or not.
     */
    public final boolean isConnected() {
        return connected;
    }

    protected final void ensureConnection() throws ConnectionException {
        if (!connected)
            throw new ConnectionException("Not connected!", ConnectionException.Type.NOT_CONNECTED);
    }
}