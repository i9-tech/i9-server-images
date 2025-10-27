package school.sptech.consumer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import school.sptech.dto.EventoImagemProcessadaDto;
import school.sptech.dto.EventoProcessamentoImagemDto;
import school.sptech.service.StorageService;

import java.util.UUID;

@Component
public class ImageProcessingConsumer {

    private final StorageService storageService;
    private final RabbitTemplate rabbitTemplate;

    public ImageProcessingConsumer(StorageService storageService, RabbitTemplate rabbitTemplate) {
        this.storageService = storageService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "notificacoes.images.processar.queue")
    public void processarImagem(@Payload EventoProcessamentoImagemDto evento) {
        System.out.printf("Evento de processamento de imagem recebido para o identificador: %s%n", evento.identificador());

        try {
            String nomeOriginal = evento.nomeArquivoOriginal();
            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }

            String nomeArquivoFinal = evento.identificador() + "-" + UUID.randomUUID() + extensao;
            String localizacao = storageService.salvar(evento.imagem(), nomeArquivoFinal, evento.tipoConteudo());

            System.out.printf("Imagem processada com sucesso! Identificador: %s, Localização: %s%n",
                    evento.identificador(), localizacao);

            EventoImagemProcessadaDto resposta = new EventoImagemProcessadaDto(evento.tipo(), evento.identificador(), localizacao);

            String responseRoutingKey = "evento.images.processadas.sucesso";
            String responseExchange = "notificacoes.topic";
            System.out.printf("Enviando evento de resposta para a exchange '%s' com a chave '%s'%n", responseExchange, responseRoutingKey);
            rabbitTemplate.convertAndSend(responseExchange, responseRoutingKey, resposta);

        } catch (Exception e) {
            System.err.printf("Falha ao processar imagem com identificador %s. Erro: %s%n",
                    evento.identificador(), e.getMessage());

        }
    }
}

