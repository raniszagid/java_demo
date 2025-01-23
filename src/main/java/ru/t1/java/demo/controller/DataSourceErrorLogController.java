package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.List;

@RequestMapping("/err")
@RestController
@RequiredArgsConstructor
public class DataSourceErrorLogController {
    // контроллер создан исключительно для удобства просмотра базы даннх с логами ошибок через HTTP-запрос
    // практического смысла не несёт
    private final DataSourceErrorLogRepository repository;
    @GetMapping
    public List<DataSourceErrorLog> read() {
        return repository.findAll();
    }
}
