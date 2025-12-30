package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.*;
import com.example.vaadinproject.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return userRepository.findAll();
        } else {
            return userRepository.search(filterText);
        }
    }

    public long countUsers() {
        return userRepository.count();
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User saveUser(User user) {
        // Hash password if it's a new user or password has changed
        if (user.getId() == null || !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() &&
                passwordEncoder.matches(password, user.get().getPassword()) &&
                user.get().getActif()) {
            return user;
        }
        return Optional.empty();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Fetches user with all profile-related collections loaded.
     * This prevents LazyInitializationException in the UI layer.
     */
    @Transactional(readOnly = true)
    public Optional<UserProfileData> getUserProfileData(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        // Access lazy-loaded collections within the transaction
        // This forces Hibernate to load them before the session closes
        int organizedEventsCount = user.getEvenementsOrganises() != null
                ? user.getEvenementsOrganises().size()
                : 0;

        int reservationsCount = user.getReservations() != null
                ? user.getReservations().size()
                : 0;

        // Create a DTO with all the data needed for the profile view
        return Optional.of(new UserProfileData(
                user,
                organizedEventsCount,
                reservationsCount
        ));
    }

    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActif(false);
        return userRepository.save(user);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * DTO class to hold user profile data with preloaded statistics.
     * This prevents LazyInitializationException by loading all data
     * within a transaction before passing it to the view layer.
     */
    public static class UserProfileData {
        private final User user;
        private final int organizedEventsCount;
        private final int reservationsCount;

        public UserProfileData(User user, int organizedEventsCount,
                               int reservationsCount) {
            this.user = user;
            this.organizedEventsCount = organizedEventsCount;
            this.reservationsCount = reservationsCount;
        }

        public User getUser() {
            return user;
        }

        public int getOrganizedEventsCount() {
            return organizedEventsCount;
        }

        public int getReservationsCount() {
            return reservationsCount;
        }


    }
}