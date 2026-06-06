package com.work.esotericaClients.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.service.ClientService;


public class CustomUserDetailService implements UserDetailsService{
    private final ClientService clientService;

    public CustomUserDetailService(ClientService clientService){
        this.clientService=clientService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Client client= clientService.getByDni(dni)
            .orElseThrow(() -> new UsernameNotFoundException("Cliente no encontrado owo"));
            
        return User.builder()
            .username(client.getDni())
            .password(client.getPasswordHash())
            .roles(
                client
                    .getRole()
                    .getRole_name()
                    .replace("ROLE_", "")
            )
            .build();
    }   
}
