package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    public Optional<Client> get(Long id) {
        return clientRepository.findById(id);
    }
    public List<Client> getAll() {
        return clientRepository.findAll();
    }
    public void save(Client client) {
        client.setClientId(UUID.randomUUID());
        clientRepository.save(client);
    }
    public void change(Client old, ClientDto fresh) {
        if (fresh.getFirstName() != null)
            old.setFirstName(fresh.getFirstName());
        if (fresh.getLastName() != null)
            old.setLastName(fresh.getLastName());
        if (fresh.getMiddleName() != null)
            old.setMiddleName(fresh.getMiddleName());
        clientRepository.save(old);
    }
    public void delete(Client client) {
        clientRepository.delete(client);
    }
}
