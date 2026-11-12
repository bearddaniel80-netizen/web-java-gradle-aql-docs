package example.config;

import example.security.Secrets;
import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.secure}") boolean secure) {
        return MinioClient.builder()
                .endpoint(endpoint.contains("://") ? endpoint : (secure ? "https://" : "http://") + endpoint)
                .credentials(Secrets.read("minio_app_user"), Secrets.read("minio_app_password"))
                .build();
    }
}
