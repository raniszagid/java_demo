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
import ru.t1.java.demo.kafka.producer.KafkaTransactionProducer;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Acceptance;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;
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
    private final ClientService clientService;
    private final KafkaTransactionProducer transactionProducer;
    @KafkaListener(
            id = "transactionListener",
            topics = {"${t1.kafka.topic.transactions}"},
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=ru.t1.java.demo.dto.TransactionDto"}
    )
    public void listener(@Payload List<TransactionDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Transaction consumer: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            messageList.stream().forEach(this::writeTransaction);
        }
        finally {
            ack.acknowledge();
        }
    }
    private void writeTransaction(TransactionDto transactionDto) {
        Account account = accountService
                .get(transactionDto.getAccountId());
        if (account.getAccountStatus().equals(AccountStatus.OPEN)) {
            log.info("Счёт открытый");
            transactionService.saveAccepting(transactionDto);
            accountService.changeBalance(account, transactionDto.getAmount());
            Acceptance acceptance = Acceptance.builder()
                    .clientId(clientService.get(account.getClientId()).getClientId())
                    .accountId(account.getAccountId())
                    .transactionId(transactionDto.getTransactionId())
                    .timestamp(transactionDto.getTransactionTime())
                    .transactionAmount(transactionDto.getAmount())
                    .accountBalance(account.getBalance())
                    .build();
            transactionProducer.sendAcceptance(acceptance);
        }
        else {
            log.warn("Счёт не открытый, транзакция не сохранена");
        }
    }
}
