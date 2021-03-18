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

import java.io.IOException;

/**
 * Exception related to connections, sockets, communication and data transfer in general.
 *
 * @author marcocipriani01
 * @version 1.3
 */
public class ConnectionException extends IOException {

    /**
     * The kind of the error occurred.
     */
    private Type type;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public ConnectionException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param type the type of error detected.
     * @see Type
     */
    public ConnectionException(Type type) {
        super();
        this.type = type;
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     * @param type    the type of error detected.
     * @see Type
     */
    public ConnectionException(String message, Type type) {
        super(message);
        this.type = type;
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.
     * @param type    the type of error detected.
     * @see Type
     */
    public ConnectionException(String message, Throwable cause, Type type) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A {@code null} value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @param type  the type of error detected.
     * @see Type
     */
    public ConnectionException(Throwable cause, Type type) {
        super(cause);
        this.type = type;
    }

    /**
     * @return the type of error detected.
     * @see Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Enum representing the kind of error detected.
     *
     * @author marcocipriani01
     * @version 0.1
     */
    public enum Type {
        /**
         * Generic or unknown error.
         */
        UNKNOWN,
        /**
         * Not connected.
         */
        NOT_CONNECTED,
        /**
         * Not started yet.
         */
        NOT_STARTED,
        /**
         * Already started.
         */
        ALREADY_STARTED,
        /**
         * Already connected.
         */
        ALREADY_CONNECTED,
        /**
         * Generic, port busy.
         */
        BUSY,
        /**
         * Error during the I/O.
         */
        IO,
        /**
         * Error in input transfer.
         */
        INPUT,
        /**
         * Error in output transfer.
         */
        OUTPUT,
        /**
         * Error during connection.
         */
        CONNECTION,
        /**
         * Error during connection, port busy.
         */
        PORT_BUSY,
        /**
         * Error during connection, no port found.
         */
        PORT_NOT_FOUND,
        /**
         * Host not found.
         */
        HOST_NOT_FOUND,
        /**
         * Error during disconnection.
         */
        UNABLE_TO_DISCONNECT,
        /**
         * Error that occurs when the client doesn't receive a response to an important request.
         */
        NO_RESPONSE,
        /**
         * Error that occurs when the client doesn't receive a valid response to a request, or a received message was invalid.
         * Could be both a warning or a fatal error.
         */
        PROTOCOL,
        /**
         * Occurs when the network interfaces are unreachable.
         */
        NETWORK_ERROR,
        /**
         * Connection timeout.
         */
        TIMEOUT
    }
}