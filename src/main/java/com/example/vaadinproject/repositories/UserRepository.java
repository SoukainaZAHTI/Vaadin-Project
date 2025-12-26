package com.example.vaadinproject.repositories;

import org.springframework.stereotype.Repository;

import com.example.vaadinproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


@Repository


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select c from User c " +
            "where lower(c.prenom) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.nom) like lower(concat('%', :searchTerm, '%'))")
     List<User> search(@Param("searchTerm") String searchTerm);
    Optional<User> findByEmail(String email);

}
