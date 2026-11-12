package example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UploadUrlRequest(
        @NotNull @JsonProperty("content_type") String contentType,
        @NotNull String filename) {
}
