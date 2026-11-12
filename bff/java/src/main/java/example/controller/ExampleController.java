package example.controller;

import example.service.FastapiClient;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/examples")
public class ExampleController {
    private final FastapiClient fastapi;

    public ExampleController(FastapiClient fastapi) {
        this.fastapi = fastapi;
    }

    private static String segment(String value) {
        return UriUtils.encodePathSegment(value, StandardCharsets.UTF_8);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Object> exampleDefault(@PathVariable String filename) {
        return fastapi.get("/api/example/" + segment(filename));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> exampleList() {
        return fastapi.get("/api/example/list");
    }

}

    