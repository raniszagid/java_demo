package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.config.KafkaConfig;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.producer.TransactionResultProducer;
import ru.t1.java.demo.model.Acceptance;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.TransactionResult;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class TransactionAcceptConsumer {
    private final TransactionResultProducer producer;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final KafkaConfig kafkaConfig;
    private final long t;
    private final long n;
    public TransactionAcceptConsumer(TransactionResultProducer producer, TransactionService transactionService, AccountService accountService, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.kafkaConfig = kafkaConfig;
        t = kafkaConfig.getMinutesQuantity();
        n = kafkaConfig.getMaxNumberPerTime();
    }

    @KafkaListener(
            id = "acceptanceListener",
            topics = {"${t1.kafka.topic.acceptance}"},
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=ru.t1.java.demo.model.Acceptance"}
    )
    public void listener(@Payload List<Acceptance> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Accept consumer: Обработка новых сообщений");
        try {
            messageList.stream().forEach(this::sendAnswerResult);
        }
        finally {
            ack.acknowledge();
        }
    }
    private void sendAnswerResult(Acceptance acceptance) {
        TransactionStatus status = assignTransactionStatus(acceptance);
        TransactionResult result = TransactionResult.builder()
                .status(status)
                .transactionId(acceptance.getTransactionId())
                .accountId(acceptance.getAccountId())
                .build();
        producer.send(result);
    }
    private TransactionStatus assignTransactionStatus(Acceptance acceptance) {
        if (isFrequencyForbidden(acceptance)) {
            log.warn("Слишком много транзакций за последний промежуток времени");
            return TransactionStatus.BLOCKED;
        }
        else if (isTransactionAmountTooBig(acceptance)) {
            log.warn("Сумма транзакции списания больше, чем баланс счёта");
            return TransactionStatus.REJECTED;
        }
        log.info("По транзакции всё ОК");
        return TransactionStatus.ACCEPTED;
     }
    private boolean isFrequencyForbidden(Acceptance a) {
        Account account = accountService.getAccountByUUID(a.getAccountId());
        long number = transactionService.countTransactionsLastTimeByAccount(a.getTimestamp(), t,
                account.getId());
        log.info("транзакции конкретного счета за последнее время {}", number);
        return number >= n;
    }
    private boolean isTransactionAmountTooBig(Acceptance a) {
        return a.getAccountBalance() < 0.0;
    }
}
