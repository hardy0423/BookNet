package com.ydrah.book.repositories;

import com.ydrah.book.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
}
