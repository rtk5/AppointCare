package com.appointment.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private String message;
    private LocalDate date;
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification() {}

    public Notification(User recipient, Appointment appointment, String message, NotificationType type) {
        this.recipient = recipient;
        this.appointment = appointment;
        this.message = message;
        this.date = LocalDate.now();
        this.isRead = false;
        this.type = type;
    }

    public void sendSMS()   { System.out.println("[SMS] To: "   + recipient.getPhone() + " | " + message); }
    public void sendEmail() { System.out.println("[EMAIL] To: " + recipient.getEmail() + " | " + message); }
    public void markRead()  { this.isRead = true; }

    public Long getNotificationId()              { return notificationId; }
    public void setNotificationId(Long v)        { this.notificationId = v; }
    public User getRecipient()                   { return recipient; }
    public void setRecipient(User v)             { this.recipient = v; }
    public Appointment getAppointment()          { return appointment; }
    public void setAppointment(Appointment v)    { this.appointment = v; }
    public String getMessage()                   { return message; }
    public void setMessage(String v)             { this.message = v; }
    public LocalDate getDate()                   { return date; }
    public void setDate(LocalDate v)             { this.date = v; }
    public boolean isRead()                      { return isRead; }
    public void setRead(boolean v)               { this.isRead = v; }
    public NotificationType getType()            { return type; }
    public void setType(NotificationType v)      { this.type = v; }
}
