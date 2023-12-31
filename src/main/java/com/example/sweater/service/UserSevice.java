package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserSevice implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${hostname}")
    private String hostname;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return user;
    }

    public boolean addUser(User user) {
        if (userRepo.findByUsername(user.getUsername()) != null) return false;
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        sendMessage(user);
        return true;
    }

    private void sendMessage(User user) {
        if (StringUtils.hasText(user.getEmail())) {
            String message = String.format("Hello, %s!" +
                    "Добро пожаловать. Пожалуйста, перейдите по следующей ссылке: http://%s/activate/%s",
                    user.getUsername(), hostname, user.getActivationCode());
            mailSender.send(user.getEmail(), "Код активации", message);
        }
    }

    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user != null) {
            user.setActivationCode(null);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);
        Set<Role> roles = form.keySet().stream()
                .filter(key -> Arrays.stream(Role.values())
                        .map(Role::name)
                        .collect(Collectors.toSet())
                        .contains(key))
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userRepo.save(user);
    }

    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        boolean isEmailChanged = !Objects.equals(email, userEmail);
        if (isEmailChanged) {
            user.setEmail(email);
            if (!StringUtils.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
                sendMessage(user);
            }
        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
        userRepo.save(user);
    }

    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        userRepo.save(user);
    }

    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        userRepo.save(user);
    }
}
