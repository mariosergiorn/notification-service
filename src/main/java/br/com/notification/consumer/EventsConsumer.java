package br.com.notification.consumer;

import br.com.notification.config.KeyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class EventsConsumer {

    @Autowired
    KeyManager keyManager;

    @Value("${web.client.uri}")
    private String uri;

    @Bean
    public void subscribeToEvents() {
        WebClient client = WebClient.create(uri); // URL do servidor SSE

        Flux<String> eventStream = client.get()
                .uri("/subscribe")
                .retrieve()
                .bodyToFlux(String.class);

        eventStream.subscribe(event -> {
            try {
                log.info("Mensagem criptografada: {}", event);
                log.info("Evento recebido: {}", keyManager.decryptAndVerify(event));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, error -> log.error("Erro ao receber evento: " + error), () -> log.info("Assinatura de eventos concluída"));
    }
}
