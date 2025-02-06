package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Acceptance;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionProducer {
    private final KafkaTemplate template;

    public void send(TransactionDto transactionDto) {
        Message<TransactionDto> message = MessageBuilder
                .withPayload(transactionDto)
                .setHeader(KafkaHeaders.TOPIC, "t1_demo_transactions")
                .setHeader(KafkaHeaders.KEY, transactionDto.getTransactionId().toString())
                .build();
        try {
            template.send(message).get();
            log.info("TransactionDto отправлен в топик");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        template.flush();
    }

    public void sendAcceptance(Acceptance acceptance) {
        Message<Acceptance> message = MessageBuilder
                .withPayload(acceptance)
                .setHeader(KafkaHeaders.TOPIC, "t1_demo_transaction_accept")
                .setHeader(KafkaHeaders.KEY, acceptance.getTransactionId().toString())
                .build();
        try {
            template.send(message).get();
            log.info("Отправляем Acceptance");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        template.flush();
    }
}
