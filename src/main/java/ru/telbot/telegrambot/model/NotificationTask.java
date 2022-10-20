package ru.telbot.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long telegram_chat_id;
    private String message_text;
    private LocalDateTime message_datetime;
    private LocalDateTime send_datetime;

    public long getId() {
        return id;
    }

    public long getTelegram_chat_id() {
        return telegram_chat_id;
    }

    public void setTelegram_chat_id(long telegram_chat_id) {
        this.telegram_chat_id = telegram_chat_id;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public LocalDateTime getMessage_datetime() {
        return message_datetime;
    }

    public void setMessage_datetime(LocalDateTime message_datetime) {
        this.message_datetime = message_datetime;
    }

    public LocalDateTime getSend_datetime() {
        return send_datetime;
    }

    public void setSend_datetime(LocalDateTime send_datetime) {
        this.send_datetime = send_datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationTask)) return false;
        NotificationTask notifTask = (NotificationTask) o;
        return getId() == notifTask.getId() && getTelegram_chat_id() == notifTask.getTelegram_chat_id() && Objects.equals(getMessage_text(), notifTask.getMessage_text()) && Objects.equals(getMessage_datetime(), notifTask.getMessage_datetime()) && Objects.equals(getSend_datetime(), notifTask.getSend_datetime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTelegram_chat_id(), getMessage_text());
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", telegram_chat_id=" + telegram_chat_id +
                ", message_text='" + message_text + '\'' +
                ", message_datetime=" + message_datetime +
                ", send_datetime=" + send_datetime +
                '}';
    }
}
