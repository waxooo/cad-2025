package com.example.demo.service;

import com.example.demo.entity.Client;
import com.example.demo.entity.Guide;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Override
    @Transactional(readOnly = true)
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        System.out.println("=== Поиск пользователя: " + email + " ===");


        Optional<Guide> guideOpt = guideRepository.findByEmail(email);
        if (guideOpt.isPresent()) {
            Guide guide = guideOpt.get();

            if (guide.getRole() == null) {
                throw new UsernameNotFoundException("У гида не назначена роль: " + email);
            }
            String roleName = guide.getRole().getRoleName();
            System.out.println("✓ Найден гид: " + email + " с ролью: " + roleName);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

            // Дополнительная отладка
            System.out.println("   Authorities: " + authorities);

            return User.builder()
                    .username(guide.getEmail())
                    .password(guide.getPasswordHash())
                    .authorities(authorities)
                    .build();
        }

        Optional<Client> clientOpt = clientRepository.findByEmail(email);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (client.getRole() == null) {
                throw new UsernameNotFoundException("У клиента не назначена роль: " + email);
            }
            String roleName = client.getRole().getRoleName();
            System.out.println("✓ Найден клиент: " + email + " с ролью: " + roleName);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));

            System.out.println("   Authorities: " + authorities);

            return User.builder()
                    .username(client.getEmail())
                    .password(client.getPasswordHash())
                    .authorities(authorities)
                    .build();
        }

        System.out.println("✗ Пользователь не найден: " + email);
        throw new UsernameNotFoundException("Пользователь не найден: " + email);
    }
}