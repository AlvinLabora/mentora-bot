package io.mentora.mentora_bot.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "conversation", indexes = @Index(name = "idx_conversation_user", columnList = "telegram_user_id"))
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// FIX: no longer unique. A user has many messages (2 rows per exchange).
	@Column(name = "telegram_user_id", nullable = false)
	private Long telegramUserId;

	@Column(name = "role", nullable = false, length = 20)
	private String role;

	// FIX: default VARCHAR(255) is too small for AI replies.
	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}