package com.tometracker.service;

import com.tometracker.db.model.User;
import com.tometracker.db.repository.UserRepository;
import com.tometracker.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AchievementService achievementService;

    public UserService(UserRepository userRepository, AchievementService achievementService) {
        this.userRepository = userRepository;
        this.achievementService = achievementService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }

    public String addUser(UserDTO user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            return "Username already exists";
        }
        String bcryptPass = new BCryptPasswordEncoder().encode(user.password());
        User newUser = new User(user.username(), bcryptPass, List.of("ROLE_USER"));
        userRepository.save(newUser);
        achievementService.initializeUserAchievements(newUser);
        return "User registered successfully";
    }
}
