package school.sptech.service.blob;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import school.sptech.service.StorageService;

import java.io.ByteArrayInputStream;


@Service
@ConditionalOnProperty(name = "storage.service.type", havingValue = "azure")
public class AzureBlobStorageService implements StorageService {

    private final BlobContainerClient containerClient;

    public AzureBlobStorageService(BlobContainerClient containerClient) {
        this.containerClient = containerClient;
    }

    @Override
    public String salvar(byte[] arquivo, String nomeArquivo, String tipoConteudo) {
        try {
            BlobClient blobClient = containerClient.getBlobClient(nomeArquivo);

            blobClient.upload(new ByteArrayInputStream(arquivo), arquivo.length, true);

            BlobHttpHeaders headers = new BlobHttpHeaders();
            headers.setContentType(tipoConteudo);
            blobClient.setHttpHeaders(headers);

            String url = blobClient.getBlobUrl();
            System.out.println("Arquivo salvo no Azure Blob Storage. URL: " + url);

            return url;
        } catch (Exception e) {
            System.err.println("Erro ao fazer upload para o Azure Blob Storage: " + e.getMessage());
            throw new RuntimeException("Falha ao salvar arquivo no Azure", e);
        }
    }
}
