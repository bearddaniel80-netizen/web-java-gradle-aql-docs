package example.security;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reads Docker secrets from /run/secrets/{name}, falling back to an env var for local dev. */
public final class Secrets {

    private Secrets() {}

    public static String read(String name) {
        Path path = Path.of("/run/secrets", name);
        if (Files.exists(path)) {
            try {
                return Files.readString(path).strip();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Missing secret or env var: " + name);
        }
        return value;
    }
}
