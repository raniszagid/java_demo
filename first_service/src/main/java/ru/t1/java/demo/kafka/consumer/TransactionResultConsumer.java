package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.config.KafkaConfig;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.TransactionResult;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@Component
public class TransactionResultConsumer {
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final long minutes;
    private final KafkaConfig kafkaConfig;
    @Autowired
    public TransactionResultConsumer(TransactionService transactionService, AccountService accountService, KafkaConfig kafkaConfig) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.kafkaConfig = kafkaConfig;
        minutes = kafkaConfig.getMinutesQuantity();
    }
    @KafkaListener(
            id = "transactionResultListener",
            topics = {"${t1.kafka.topic.result}"},
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=ru.t1.java.demo.model.TransactionResult"}
    )
    public void listener(@Payload List<TransactionResult> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("TransactionResult consumer: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            messageList.stream().forEach(this::processTransactionResult);
        }
        finally {
            ack.acknowledge();
        }
    }

    private void processTransactionResult(TransactionResult result) {
        TransactionStatus status = result.getStatus();
        switch (status) {
            case ACCEPTED -> {
                transactionService.changeStatus(result.getTransactionId(), result.getStatus());
                log.info("Транзакции выставлен статус ACCEPTED");
            }
            case REJECTED -> {
                log.warn("Денег на счету недостаточно, транзакция отменена");
                transactionService.changeStatus(result.getTransactionId(), result.getStatus());
                Double transactionAmount = transactionService.getAmountByUUID(result.getTransactionId());
                accountService.changeBalanceByUUIDAfterRejection(result.getAccountId(),
                        transactionAmount);
            }
            case BLOCKED -> {
                log.warn("В топик с результатами пришло сообщение со статусом Blocked");
                Account account = accountService.getAccountByUUID(result.getAccountId());
                transactionService.blockTransactions(account.getId(), result.getTransactionId(), minutes);
                double frozenAmount = transactionService.summaryBlockedTransactionsOfAccount(account.getId());
                accountService.frozeAmountAndBlockAccount(account, frozenAmount);
            }
        }
    }
}
