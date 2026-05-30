package com.work.esotericaClients.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.work.esotericaClients.domain.entity.Code;
import com.work.esotericaClients.repository.CodeRepository;

@Service
public class CodeService {
    private final CodeRepository repository;

    public CodeService(CodeRepository repository){
        this.repository=repository;
    }

    public List<Code> getByTitle(String title){
        return repository.findByTitle(title);
    }

    public List<Code> getByCreatedAt(LocalDate createdAt){
        return repository.findByCreatedAt(createdAt);
    }

    public List<Code> getByExpiration(LocalDate expiration){
        return repository.findByExpiration(expiration);
    }

    public Code createCode(Code code){
        return repository.save(code);
    }

    public void deleteCode(Long id){
        repository.deleteById(id);
    }

    public Code updateCode(Long id, Code code){
        return repository.save(code);
    }

    //frond-end screen
    public List<Code> getCodes(){
        return repository.findAll();
    }

}
