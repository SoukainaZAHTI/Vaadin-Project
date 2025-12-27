package com.example.vaadinproject.services;

import com.example.vaadinproject.entities.*;
import com.example.vaadinproject.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        return userRepository.save(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public Optional<User> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().getPassword().equals(password) && user.get().getActif()) {
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
        user.setPassword(newPassword);
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