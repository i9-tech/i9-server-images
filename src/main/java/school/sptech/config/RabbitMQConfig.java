package school.sptech.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange topicExchange() {
        String exchangeName = "notificacoes.topic";
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue filaProcessarImagens() {
        return new Queue("notificacoes.images.processar.queue");
    }

    @Bean
    public Queue filaImagensProcessadas() {
        return new Queue("notificacoes.images.processadas.queue", true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding bindingProcessarImagens(Queue filaProcessarImagens, TopicExchange topicExchange) {
        return BindingBuilder.bind(filaProcessarImagens)
                .to(topicExchange)
                .with("evento.images.processar");
    }

    @Bean
    public Binding bindingImagensProcessadas(Queue filaImagensProcessadas, TopicExchange topicExchange) {
        return BindingBuilder.bind(filaImagensProcessadas)
                .to(topicExchange)
                .with("evento.images.processadas.sucesso");
    }

}

