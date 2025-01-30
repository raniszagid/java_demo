package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogService errorLogService;
    @Pointcut("@within(ru.t1.java.demo.aop.annotation.LogDataSourceError)")
    public void loggingClasses() {}

    @AfterThrowing(
            pointcut = "loggingClasses() /*",
            throwing = "exception"
    )
    public void logToDatabase(JoinPoint joinPoint, Exception exception) {
        log.error("Аспект поймал исключение");
        DataSourceErrorLog errorLog = DataSourceErrorLog.builder()
                .stacktrace(Arrays.toString(exception.getStackTrace()))
                .message(exception.getMessage())
                .methodSignature(joinPoint.getSignature().toShortString())
                .errorTime(LocalDateTime.now())
                .build();
        errorLogService.save(errorLog, "t1_demo_metrics");
    }
}
