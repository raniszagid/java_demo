package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceErrorAspect {
    private final DataSourceErrorLogRepository errorLogRepository;
    @Pointcut("@within(LogDataSourceError)")
    public void loggingClasses() {}
    @AfterThrowing(
            pointcut = "loggingClasses() /*",
            throwing = "exception"
    )
    public void logToDatabase(JoinPoint joinPoint, Exception exception) {
        DataSourceErrorLog errorLog = DataSourceErrorLog.builder()
                .stacktrace(Arrays.toString(exception.getStackTrace()))
                .message(exception.getMessage())
                .methodSignature(joinPoint.getSignature().toShortString())
                .errorTime(LocalDateTime.now())
                .build();
        errorLogRepository.save(errorLog);
    }
}
