package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.kafka.producer.KafkaErrorLogProducer;
import ru.t1.java.demo.model.MetricErrorLog;

import java.util.Arrays;
import java.util.UUID;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
@Order(0)
public class MetricAspect {
    private final KafkaErrorLogProducer producer;
    @Pointcut("@annotation(metric)")
    public void getPointcut(Metric metric) {}
    @Around(value = "getPointcut(metric)", argNames = "joinPoint,metric")
    public Object measureMethodExecutionDuration(ProceedingJoinPoint joinPoint, Metric metric) {
        Object result = null;
        long start = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        long end = System.currentTimeMillis();
        long executionDuration = end - start;
        if (executionDuration > metric.value()) {
            log.error("Продолжительность выполнения метода слишком велика");
            String topic = "t1_demo_metrics";
            MetricErrorLog metricLog = MetricErrorLog.builder()
                    .methodName(joinPoint.getSignature().getName())
                    .arguments(Arrays.toString(joinPoint.getArgs()))
                    .maxExecutionDuration(metric.value())
                    .actualDuration(executionDuration)
                    .build();
            Message<MetricErrorLog> message = MessageBuilder
                    .withPayload(metricLog)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, UUID.randomUUID().toString())
                    .setHeader("error_type", "METRICS")
                    .build();
            producer.sendMetricLog(message);
        }
        return result;
    }

}
