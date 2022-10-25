package ru.telbot.telegrambot.model;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.boot.autoconfigure.web.WebProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Telbot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime start_date;
    private LocalDateTime finish_date;
    private String value;

    public long getId() {
        return id;
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public LocalDateTime getFinish_date() {
        return finish_date;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Telbot)) return false;
        Telbot telbot = (Telbot) o;
        return Objects.equals(getStart_date(), telbot.getStart_date()) && Objects.equals(getFinish_date(), telbot.getFinish_date()) && Objects.equals(getValue(), telbot.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "Telbot{" +
                "id=" + id +
                ", start_date=\"" + start_date + "\"" +
                ", finish_date=\"" + finish_date + "\"" +
                ", value=\"" + value + "\"" +
                "}";
    }
}
