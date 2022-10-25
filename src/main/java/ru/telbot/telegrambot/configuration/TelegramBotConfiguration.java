package ru.telbot.telegrambot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.telbot.telegrambot.repository.TelegramBotRepository;

@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String offset;

    @Autowired
    private TelegramBotRepository telBotRepo;

    @Bean
    public TelegramBot telegramBot() {
        //System.out.println("token = " + telBotRepo.getToken(offset));
        TelegramBot bot = new TelegramBot(offset);
        bot.execute(new DeleteMyCommands());
        return bot;
    }

}
