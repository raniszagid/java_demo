package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaAccountProducer {
    private final KafkaTemplate template;

    public void send(AccountDto accountDto) {
        Message<AccountDto> message = MessageBuilder
                .withPayload(accountDto)
                .setHeader(KafkaHeaders.TOPIC, "t1_demo_accounts")
                .build();
        try {
            template.send(message).get();
            log.info("AccountDto отправлен в топик");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        template.flush();

    }
}
