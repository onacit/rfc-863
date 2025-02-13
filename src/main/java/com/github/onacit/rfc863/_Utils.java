package com.github.onacit.rfc863;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

@Slf4j
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class _Utils {

    static void readQuit(final BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader, "reader is null");
        for (String l; (l = reader.readLine()) != null; ) {
            if (l.toUpperCase().contains("QUIT")) {
                break;
            }
        }
    }

    static void readQuit(final Reader reader) throws IOException {
        Objects.requireNonNull(reader, "reader is null");
        readQuit(new BufferedReader(reader));
    }

    static void readQuit(final InputStream stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        readQuit(new InputStreamReader(stream));
    }

    /**
     * Keep reading lines from the {@link System#in standard input stream} until it reads a line contains {@code quit}.
     *
     * @throws IOException if an I/O error occurs.
     */
    static void readQuit() throws IOException {
        readQuit(System.in);
    }

    /**
     * Keep reading lines from the {@link System#in standard input stream} until it reads a line contains {@code quit},
     * and {@link Callable#call() calls} specified task.
     *
     * @param callable the task to {@link Callable#call() call}.
     */
    static void readQuitAndCall(final Callable<?> callable) {
        Objects.requireNonNull(callable, "callable is null");
        Thread.ofPlatform().name("read-quit-and-call").start(() -> {
            try {
                readQuit();
            } catch (final IOException ioe) {
                log.error("failed to read quit", ioe);
            } finally {
                log.debug("calling {}", callable);
                try {
                    callable.call();
                } catch (final Exception e) {
                    log.debug("failed to call {}", callable, e);
                }
            }
        });
    }

    /**
     * Keep reading lines from the {@link System#in standard input stream} until it reads a line contains {@code quit},
     * and {@link Runnable#run() runs} specified task.
     *
     * @param runnable the task to {@link Runnable#run() run}.
     * @see #readQuitAndCall(Callable)
     */
    static void readQuitAndRun(final Runnable runnable) {
        Objects.requireNonNull(runnable, "runnable is null");
        readQuitAndCall(() -> {
            log.debug("running {}", runnable);
            runnable.run();
            return null;
        });
    }

    /**
     * Keep reading lines from the {@link System#in standard input stream} until it reads a line contains {@code quit},
     * and {@link Closeable#close() closes} specified closeable.
     *
     * @param closeable the closeable to {@link Closeable#close() close}.
     * @see #readQuitAndCall(Callable)
     * @see Closeable#close()
     */
    static void readQuitAndClose(final Closeable closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        readQuitAndCall(() -> {
            log.debug("closing {}", closeable);
            closeable.close();
            return null;
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    static void acceptCommandAndClasspath(final BiConsumer<? super String, ? super String> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        final var info = ProcessHandle.current().info();
        final var command = info.command().orElseThrow();
        log.debug("command: {}", command);
        final String classpath;
        {
            String cp = null;
            final var arguments = info.arguments().orElseThrow();
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].equals("-classpath")) {
                    cp = arguments[i + 1];
                    break;
                }
            }
            classpath = cp;
        }
        log.debug("classpath: {}", Optional.ofNullable(classpath).orElseThrow());
        consumer.accept(command, classpath);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private _Utils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
