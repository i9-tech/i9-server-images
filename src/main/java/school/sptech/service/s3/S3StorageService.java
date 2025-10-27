package school.sptech.service.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import school.sptech.service.StorageService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
@ConditionalOnProperty(name = "storage.service.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${storage.s3.bucket-name}")
    private String bucketName;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String salvar(byte[] arquivo, String nomeArquivo, String tipoConteudo) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(nomeArquivo)
                    .contentType(tipoConteudo)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(arquivo));

            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(nomeArquivo)
                    .build();

            String url = s3Client.utilities().getUrl(getUrlRequest).toString();
            System.out.println("Arquivo salvo no S3. URL: " + url);

            return url;
        } catch (Exception e) {
            System.err.println("Erro ao fazer upload para o S3: " + e.getMessage());
            throw new RuntimeException("Falha ao salvar arquivo no S3", e);
        }
    }
}
