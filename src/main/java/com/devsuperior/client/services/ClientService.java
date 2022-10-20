package com.devsuperior.client.services;

import com.devsuperior.client.dto.ClientDTO;
import com.devsuperior.client.entities.Client;
import com.devsuperior.client.repositories.ClientRepository;
import com.devsuperior.client.services.exceptions.DatabaseExceptions;
import com.devsuperior.client.services.exceptions.ResourceNotFoundExceptions;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id){
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundExceptions("Recurso não encontrado"));
        return new ClientDTO(client);
    }

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAll(Pageable pageable){
        Page<Client> clientPage =clientRepository.findAll(pageable);
        return clientPage.map(ClientDTO::new);
    }

    @Transactional
    public ClientDTO insert(ClientDTO clientDTO){
        Client client = new Client();
        copyDtoToEntity(client, clientDTO);

        client = clientRepository.save(client);

        return new ClientDTO(client);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO clientDTO){
        try{
            Client client = clientRepository.getReferenceById(id);
            copyDtoToEntity(client, clientDTO);
            client = clientRepository.save(client);
            return new ClientDTO(client);
        }catch (EntityNotFoundException ex){
            throw new ResourceNotFoundExceptions("Recurso não encontrado!!");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id){
        try{
            clientRepository.deleteById(id);
        }catch (EmptyResultDataAccessException ex){
            throw new ResourceNotFoundExceptions("Recurso não encontrado!!");
        }catch (DataIntegrityViolationException ex){
            throw new DatabaseExceptions("Falha de integridade referencial.");
        }
    }

    public void copyDtoToEntity(Client client, ClientDTO clientDTO){

        client.setName(clientDTO.getName());
        client.setChildren(clientDTO.getChildren());
        client.setCpf(clientDTO.getCpf());
        client.setIncome(clientDTO.getIncome());
        client.setBirthDate(clientDTO.getBirthDate());
    }
}
