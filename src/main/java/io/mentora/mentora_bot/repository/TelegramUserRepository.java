package io.mentora.mentora_bot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mentora.mentora_bot.entity.TelegramUser;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

	Optional<TelegramUser> findByTelegramUserId(Long telegramUserId);
}
