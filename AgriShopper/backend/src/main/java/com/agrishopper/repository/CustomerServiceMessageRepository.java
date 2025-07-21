package com.agrishopper.repository;

import com.agrishopper.model.CustomerServiceMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CustomerServiceMessageRepository extends JpaRepository<CustomerServiceMessage, Long> {
    
    /**
     * 根据会话ID查找消息，按创建时间排序（未删除）
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.sessionId = :sessionId AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createTime ASC")
    List<CustomerServiceMessage> findBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 根据会话ID分页查找消息（未删除）
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.sessionId = :sessionId AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createTime DESC")
    Page<CustomerServiceMessage> findBySessionIdPageable(@Param("sessionId") Long sessionId, Pageable pageable);
    
    /**
     * 根据会话ID查找未读消息（未删除）
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.sessionId = :sessionId AND m.isRead = false AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createTime ASC")
    List<CustomerServiceMessage> findUnreadBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 根据会话ID和发送者类型查找消息
     */
    List<CustomerServiceMessage> findBySessionIdAndSenderTypeOrderByCreateTimeAsc(Long sessionId, Integer senderType);
    
    /**
     * 根据会话ID查找最后一条消息（未删除）
     */
    @Query("SELECT m FROM CustomerServiceMessage m WHERE m.sessionId = :sessionId AND (m.isDeleted = false OR m.isDeleted IS NULL) ORDER BY m.createTime DESC")
    List<CustomerServiceMessage> findLastMessageBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);
    
    /**
     * 统计会话未读消息数量（未删除）
     */
    @Query("SELECT COUNT(m) FROM CustomerServiceMessage m WHERE m.sessionId = :sessionId AND m.isRead = false AND (m.isDeleted = false OR m.isDeleted IS NULL)")
    long countUnreadMessagesBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * 统计用户所有会话的未读消息数量（未删除）
     */
    @Query("SELECT COUNT(m) FROM CustomerServiceMessage m JOIN CustomerServiceSession s ON m.sessionId = s.id WHERE s.userId = :userId AND m.isRead = false AND m.senderType != 1 AND (m.isDeleted = false OR m.isDeleted IS NULL) AND (s.isDeleted = false OR s.isDeleted IS NULL)")
    long countUnreadMessagesByUserId(@Param("userId") Long userId);
    
    /**
     * 标记会话消息为已读（未删除）
     */
    @Modifying
    @Query("UPDATE CustomerServiceMessage m SET m.isRead = true, m.readTime = :readTime WHERE m.sessionId = :sessionId AND m.senderType != 1 AND (m.isDeleted = false OR m.isDeleted IS NULL)")
    void markMessagesAsReadBySessionId(@Param("sessionId") Long sessionId, @Param("readTime") LocalDateTime readTime);
    
    /**
     * 根据时间范围查找消息
     */
    List<CustomerServiceMessage> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据发送者类型查找消息
     */
    List<CustomerServiceMessage> findBySenderTypeOrderByCreateTimeDesc(Integer senderType);
    
    /**
     * 根据消息类型查找消息
     */
    List<CustomerServiceMessage> findByMessageTypeOrderByCreateTimeDesc(Integer messageType);
    
    /**
     * 查找AI生成的消息
     */
    List<CustomerServiceMessage> findByAiGeneratedTrueOrderByCreateTimeDesc();
    
    /**
     * 根据AI模型查找消息
     */
    List<CustomerServiceMessage> findByAiModelOrderByCreateTimeDesc(String aiModel);
    
    /**
     * 软删除会话的所有消息
     */
    @Modifying
    @Query("UPDATE CustomerServiceMessage m SET m.isDeleted = true WHERE m.sessionId = :sessionId")
    void softDeleteBySessionId(@Param("sessionId") Long sessionId);
} 