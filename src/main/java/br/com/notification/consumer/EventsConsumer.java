package br.com.notification.consumer;

import br.com.notification.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventsConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${queue.notification}")
    private String queue;

    @RabbitListener(queues = "${queue.notification}")
    public void receiveMessage(String jsonMessage) throws JsonProcessingException {

        Message output = objectMapper.readValue(jsonMessage, Message.class);

        log.info("Event recevied from {}: {}", queue, output.toString());

    }
}
