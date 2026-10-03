package io.mentora.mentora_bot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.mentora.mentora_bot.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	/**
	 * Newest messages first, limited by the Pageable (e.g. PageRequest.of(0, 20)).
	 * Callers must reverse the list to get chronological order.
	 */
	List<Conversation> findByTelegramUserIdOrderByIdDesc(Long telegramUserId, Pageable pageable);

	/**
	 * Deletes every stored message of one user with a single DELETE statement.
	 * (A derived deleteBy... method would load each row first, then delete them
	 * one by one.)
	 *
	 * @return number of deleted rows
	 */
	@Transactional
	@Modifying
	@Query("DELETE FROM Conversation c WHERE c.telegramUserId = :telegramUserId")
	int clearByTelegramUserId(@Param("telegramUserId") Long telegramUserId);
}