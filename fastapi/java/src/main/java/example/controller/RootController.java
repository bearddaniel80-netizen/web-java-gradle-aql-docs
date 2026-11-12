package example.controller;

import example.security.InternalKeyVerifier;
import example.service.ProcessRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RootController {

    private static final Pattern SAFE_NAME = Pattern.compile("[\\w.-]+");

    private final InternalKeyVerifier keys;
    private final ProcessRunner runner;

    public RootController(InternalKeyVerifier keys, ProcessRunner runner) {
        this.keys = keys;
        this.runner = runner;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/api/data")
    public Map<String, String> data(
            @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        keys.verify(key);
        return Map.of("message", "Hello from Spring Boot");
    }

    @GetMapping(value = "/api/{filename}", produces = MediaType.TEXT_HTML_VALUE)
    public String file(
            @PathVariable String filename,
            @RequestHeader(value = "X-Internal-Key", required = false) String key) throws IOException {
        keys.verify(key);
        if (!SAFE_NAME.matcher(filename).matches() || filename.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
        }
        Path cwd = Path.of("").toAbsolutePath();
        Path input = cwd.resolve(filename + ".md");
        Path output = cwd.resolve(filename + ".html");
        runner.render(List.of("pandoc", input.toString(), "-o", output.toString()));
        return Files.readString(output);
    }
}
