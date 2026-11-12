package example.controller;

import example.model.UploadUrlRequest;
import example.security.InternalKeyVerifier;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MinioController {

    private final MinioClient minio;
    private final InternalKeyVerifier keys;
    private final String bucket;

    public MinioController(MinioClient minio, InternalKeyVerifier keys,
                           @Value("${minio.bucket}") String bucket) {
        this.minio = minio;
        this.keys = keys;
        this.bucket = bucket;
    }

    @PostMapping("/api/uploads")
    public Map<String, String> createUploadUrl(
            @Valid @RequestBody UploadUrlRequest file,
            @RequestHeader(value = "X-Internal-Key", required = false) String key) {
        keys.verify(key);

        String extension = file.filename().contains(".")
                ? file.filename().substring(file.filename().lastIndexOf('.'))
                : "";
        String objectName = UUID.randomUUID() + extension;

        try {
            String url = minio.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucket)
                    .object(objectName)
                    .expiry(1, TimeUnit.DAYS)
                    .build());
            return Map.of("object_name", objectName, "url", url);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    // Like the original, this endpoint does not require the internal key.
    @GetMapping("/api/downloads/{*objectName}")
    public Map<String, String> createDownloadUrl(@PathVariable String objectName) {
        String name = objectName.startsWith("/") ? objectName.substring(1) : objectName;
        try {
            String url = minio.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(name)
                    .expiry(10, TimeUnit.MINUTES)
                    .build());
            return Map.of("url", url);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
