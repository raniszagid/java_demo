package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.mapper.ClientMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@LogDataSourceError
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @GetMapping
    public List<ClientDto> getAll() {
        return clientService.getAll().stream().map(clientMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ClientDto getCertain(@PathVariable("id") Long id) {
        Client client = clientService.get(id);
        return clientMapper.toDto(client);
    }

    @PostMapping
    public void create(@RequestBody ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        clientService.save(client);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id,
                       @RequestBody ClientDto clientDto) {
        Client client = clientService.get(id);
        clientService.change(client, clientDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        Client client = clientService.get(id);
        clientService.delete(client);
    }
}
