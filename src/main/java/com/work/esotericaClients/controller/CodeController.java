package com.work.esotericaClients.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.domain.entity.Code;
import com.work.esotericaClients.service.CodeService;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    private final CodeService service;

    public CodeController(CodeService service){
        this.service=service;
    }

    @PostMapping
    public Code createdCode(
        @RequestBody Code code){
            return service.createCode(code);
    }
    
    @PutMapping("/{id}")
    public Code updateCode (
        @PathVariable Long id,
        @RequestBody Code code){
            return service.updateCode(id, code);
    }

    @DeleteMapping("/{id}")
    public void deleteCode(
        @PathVariable Long id){
            service.deleteCode(id);
    }
    @GetMapping
    public List<Code> getCodes(){
        return service.getCodes();
    }

    @GetMapping("/title/{title}")
    public List<Code> getByTitle(
        @PathVariable String title){
            return service.getByTitle(title);
    }

    @GetMapping("/createdAt/{createdAt}")
    public List<Code> getByCreatedAt(
        @PathVariable LocalDate createdAt){
            return service.getByCreatedAt(createdAt);
    }

    @GetMapping("/expiration/{expiration}")
    public List<Code> getByExpiration(
        @PathVariable LocalDate expiration){
            return service.getByExpiration(expiration);
    }
}
