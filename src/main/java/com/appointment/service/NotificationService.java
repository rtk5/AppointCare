package com.appointment.service;

import com.appointment.model.Notification;
import com.appointment.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    public List<Notification> getForUser(Long userId) {
        return notificationRepository.findByRecipientUserIdOrderByDateDesc(userId);
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipientUserIdAndIsReadFalse(userId);
        unread.forEach(Notification::markRead);
        notificationRepository.saveAll(unread);
    }

    public void markRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.markRead();
            notificationRepository.save(n);
        });
    }
}
