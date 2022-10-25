package ru.telbot.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.telbot.telegrambot.model.Telbot;

@Repository
public interface TelegramBotRepository extends JpaRepository<Telbot, Long> {
    @Query(value = "SELECT value FROM telbot WHERE start_date = to_date('#{inpParam}','YYMMDD') LIMIT 1", nativeQuery = true)
    String getToken(@Param("inpParam") String startDate);
}