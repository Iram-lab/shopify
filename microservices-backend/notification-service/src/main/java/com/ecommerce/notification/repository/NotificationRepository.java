package com.ecommerce.notification.repository;

import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    Page<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type, Pageable pageable);

    List<Notification> findBySentFalseOrderByCreatedAtAsc();
}
