package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.TransactionResult;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionResultProducer {
    private final KafkaTemplate template;
    public void send(TransactionResult result) {
        String topic = "t1_demo_transaction_result";
        Message<TransactionResult> message = MessageBuilder
                .withPayload(result)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, result.getTransactionId().toString())
                .build();
        try {
            template.send(message).get();
            log.info("Результат транзакции отправлен в топик");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        template.flush();
    }
}
