package ru.t1.java.demo.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricErrorLog {
    private String methodName;
    private String arguments;
    private Long maxExecutionDuration;
    private Long actualDuration;
}
