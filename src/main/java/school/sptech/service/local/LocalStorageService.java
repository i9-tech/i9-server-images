package school.sptech.service.local;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import school.sptech.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name = "storage.service.type", havingValue = "local")
public class LocalStorageService implements StorageService {

    @Value("${storage.local.upload-dir}")
    private String uploadDir;

    @Override
    public String salvar(byte[] arquivo, String nomeArquivo, String tipoConteudo) {
        try {
            Path diretorioPath = Paths.get(uploadDir);
            if (Files.notExists(diretorioPath)) {
                Files.createDirectories(diretorioPath);
            }

            Path arquivoPath = diretorioPath.resolve(nomeArquivo);
            Files.write(arquivoPath, arquivo);
            System.out.println("Arquivo salvo localmente em: " + arquivoPath.toAbsolutePath());

            return arquivoPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo localmente: " + e.getMessage());
            throw new RuntimeException("Falha ao salvar arquivo localmente", e);
        }
    }
}
