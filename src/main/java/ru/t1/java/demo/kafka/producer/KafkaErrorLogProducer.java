package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.MetricErrorLog;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaErrorLogProducer {
    private final KafkaTemplate template;
    public void sendDataSourceLog(Message message) throws ExecutionException, InterruptedException {
        template.send(message).get();
        log.debug("Отправка в топик");
        template.flush();
    }
    public void sendMetricLog(Message<MetricErrorLog> message) {
        try {
            template.send(message).get();
            log.info("MetricLog отправлен в топик");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        template.flush();
    }
}
