package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.kafka.producer.KafkaErrorLogProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataSourceErrorLogService {
    private final DataSourceErrorLogRepository errorLogRepository;
    private final KafkaErrorLogProducer producer;
    public void save(DataSourceErrorLog errorLog, String topic) {
        try {
            log.debug("Попытка отправки в топик");
            Message<DataSourceErrorLog> message = MessageBuilder
                    .withPayload(errorLog)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                    .setHeader("error_type", "DATA_SOURCE")
                    .build();
            producer.sendDataSourceLog(message);
            log.info("DataSourceLog отправлен в топик");
        } catch (Exception e) {
            log.error("Ошибка отправки в топик");
            errorLogRepository.save(errorLog);
            log.info("Отправлено в БД");
        }
    }
}
