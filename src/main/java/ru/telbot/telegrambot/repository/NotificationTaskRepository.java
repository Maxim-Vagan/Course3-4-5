package ru.telbot.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.telbot.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    @Query(value = "SELECT to_char(send_datetime, 'DD.MM.YYYY HH24:MI:SS') || ' ' || message_text " +
            "FROM notification_task " +
            "WHERE send_datetime BETWEEN to_timestamp(:sendDateT || ':00:00', 'DD.MM.YYYY hh24:mi:ss') " +
            "AND to_timestamp(:sendDateT || ':59:59', 'DD.MM.YYYY hh24:mi:ss') ORDER BY send_datetime", nativeQuery = true)
    List<String> getNotifOnDate(@Param("sendDateT") String taskDate);

    @Query(value = "SELECT ntask.id, message_datetime, message_text, send_datetime, telegram_chat_id " +
            "FROM public.notification_task ntask " +
            "WHERE ntask.send_datetime = to_timestamp(:sendDateT, 'DD.MM.YYYY hh24:mi:ss')", nativeQuery = true)
    List<NotificationTask> getNotification(@Param("sendDateT") String taskDate);

    @Modifying
    @Query(value = "INSERT INTO notification_task (message_datetime," +
            "message_text," +
            "send_datetime," +
            "telegram_chat_id) " +
            "VALUES (:msgDateT, :msgText, :sendDateT, :chatID)", nativeQuery = true)
    @Transactional
    int addNotifOnDate(@Param("msgDateT") LocalDateTime date1,
                           @Param("msgText") String msg,
                           @Param("sendDateT") LocalDateTime date2,
                           @Param("chatID") long chatId);
}
