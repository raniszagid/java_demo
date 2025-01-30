package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@LogDataSourceError
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTransactionConsumer {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    @KafkaListener(
            id = "transactionListener",
            topics = {"t1_demo_transactions"},
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=ru.t1.java.demo.dto.TransactionDto"}
    )
    public void listener(@Payload List<TransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            messageList.stream().forEach(transactionDto -> {
                accountService.get(transactionDto.getAccountId()).orElseThrow(ClientException::new);
                transactionDto.setTransactionTime(LocalDateTime.now());
                transactionService.save(transactionMapper.toEntity(transactionDto));
            });
        }
        finally {
            ack.acknowledge();
        }
    }
}
