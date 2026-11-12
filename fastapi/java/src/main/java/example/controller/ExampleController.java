package example.controller;

import example.security.InternalKeyVerifier;
import example.service.MarkdownService;
import example.service.ProcessRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/example")
public class ExampleController {

    private static final String FOLDER = "examples";

    private static final Pattern SAFE_NAME = Pattern.compile("[\\w.-]+");

    private final InternalKeyVerifier keys;
    private final ProcessRunner runner;
    private final MarkdownService markdown;

    public ExampleController(InternalKeyVerifier keys, ProcessRunner runner, MarkdownService markdown) {
        this.keys = keys;
        this.runner = runner;
        this.markdown = markdown;
    }

    @GetMapping("/list")
    public Map<String, List<String>> list(
            @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        keys.verify(key);
        runner.render(List.of("aql", "docs"));
        String out = runner.render(List.of("ls", FOLDER));
        List<String> result = Arrays.stream(out.split("\n"))
                .filter(s -> !s.isEmpty())
                .map(ExampleController::stripSuffix)
                .toList();
        return Map.of("result", result);
    }

    @GetMapping("/{filename}")
    public String renderFile(
            @PathVariable String filename,
            @RequestHeader(value = "X-Internal-Key", required = false) String key) throws IOException {
        keys.verify(key);
        if (!SAFE_NAME.matcher(filename).matches() || filename.contains("..")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid filename");
        }
        // log.info("FOLDER: {}", FOLDER);
        // log.info("filename: {}", filename);

        Path cwd = Path.of("").toAbsolutePath();
        Path input = cwd.resolve(FOLDER + "/" + filename + ".md");
        Path output = cwd.resolve(FOLDER + "/" + filename + ".html");

        markdown.mdLinksToHtml(input);
        runner.render(List.of("pandoc", "-f", "markdown-yaml_metadata_block",
                input.toString(), "-o", output.toString()));
        return Files.readString(output);
    }

    private static String stripSuffix(String name) {
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
