package school.sptech.config;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class CloudConfig {

    @Bean
    @ConditionalOnProperty(name = "storage.service.type", havingValue = "s3")
    public S3Client s3Client(
            @Value("${aws.region}") String region,
            @Value("${aws.access-key-id}") String accessKey,
            @Value("${aws.secret-access-key}") String secretKey,
            @Value("${aws.session-token}") String sessionToken) {

        if (sessionToken != null && !sessionToken.isEmpty()) {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsSessionCredentials.create(accessKey, secretKey, sessionToken)
                    ))
                    .build();
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
    @Bean
    @ConditionalOnProperty(name = "storage.service.type", havingValue = "azure")
    public BlobContainerClient blobContainerClient(
            @Value("${storage.azure.connection-string}") String connectionString,
            @Value("${storage.azure.container-name}") String containerName) {
        
        BlobContainerClient client = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
        
        if (!client.exists()) {
            client.create();
        }
        
        return client;
    }
}
