package ru.ulstu.diskservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.ulstu.diskservice.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
