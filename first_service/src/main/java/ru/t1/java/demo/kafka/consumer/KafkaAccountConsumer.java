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
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.util.List;

@LogDataSourceError
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountConsumer {
    private final ClientService clientService;
    private final AccountService accountService;
    private final AccountMapper accountMapper;
    @KafkaListener(
            id = "accountListener",
            topics = {"${t1.kafka.topic.accounts}"},
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=ru.t1.java.demo.dto.AccountDto"}
    )
    public void listener(@Payload List<AccountDto> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка новых сообщений");
        try {
            log.info("Topic: " + topic);
            log.info("Key: " + key);
            messageList.stream().forEach(accountDto -> {
                validate(accountDto);
                accountService.save(accountMapper.toEntity(accountDto));
            });
        }
        finally {
            ack.acknowledge();
        }
    }
    private void validate(AccountDto accountDto) {
        clientService.get(accountDto.getClientId())
                .orElseThrow(() -> new ClientException(accountDto.getClientId()));
        if (!(accountDto.getAccountType().equals("DEBIT") || accountDto.getAccountType().equals("CREDIT")))
            throw new ClientException(accountDto.getAccountType());
    }
}
