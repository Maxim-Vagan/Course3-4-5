package ru.telbot.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.telbot.telegrambot.model.NotificationTask;
import ru.telbot.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private boolean dasNeedToTreateNotifies = false;

    @Autowired
    private NotificationTaskRepository notifTaskRepo;

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void runScheduledProc(){
        String NowMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        List<NotificationTask> notificationList = notifTaskRepo.getNotification(NowMinute);
        if (notificationList.size() > 0){
            logger.debug("Scheduling Autosend Notifications on current time ({})", NowMinute);
            notificationList.forEach(notify -> {
                SendMessage messageToChat = new SendMessage(notify.getTelegram_chat_id(),
                        "*Уведомление:* " + notify.getMessage_text()).parseMode(ParseMode.MarkdownV2);
                SendResponse responseFromBot = telegramBot.execute(messageToChat);
            });
        }
    }

    private String parseNotificationTask(String inpMessage){
        Pattern pattern1 = Pattern.compile("/add_task\s+(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2})\s+(.+)");
        Pattern pattern2 = Pattern.compile("/show_tasks\s+(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2})");
        Matcher matcher1 = pattern1.matcher(inpMessage);
        Matcher matcher2 = pattern2.matcher(inpMessage);
        String param1 = null;
        String param2 = null;
        String param3 = null;
        if (matcher1.matches()){
            param1 = matcher1.group(1);
            param2 = matcher1.group(2);
        } else if (matcher2.matches()) { param3 = matcher2.group(1); }
        if ((param1==null || param2==null) && param3 == null) {
            return """
            ERROR Нераспознан формат уведомления,\r\n
            либо дата назначения задания,\r\n
            либо отсутствует текст уведомления;\r\n
            Просьба повторить, но по шаблону. Пример:\r\n
            /add_task 31.12.2022 Не забудьте поздравить родню с Новым Годом!
            Для вывода, к примеру, всех уведомлений в запланированную дату и час 
            /show_tasks 31.12.2022 13
            """;
        } else if (param3 != null) {
            return param3;
        }else {
            return String.join("&", param1, param2);
        }
    }

    @Override
    public int process(List<Update> updates) {

        updates.forEach(update -> {
            String messageText = null;
            LocalDateTime sendTime = LocalDateTime.now();
            LocalDateTime justNow = sendTime.truncatedTo(ChronoUnit.SECONDS);
            String content = update.message().text();
            long chatId = update.message().chat().id();
            logger.info("Processing Chat update:In chat \"{}\" From \"{}\" came message - \"{}\"",
                    update.message().chat().firstName()+" "+update.message().chat().lastName(),
                    update.message().from().firstName()+" "+update.message().from().lastName(),
                    content);
            if (content.contains("/start")) {
                messageText = "Принята команда /start. Для вызова справки отправьте /help";
                dasNeedToTreateNotifies = true;
            } else if (content.contains("/help")) {
                messageText = """
                        Список форматов команд чат боту:\r\n
                        /add_task DD.MM.YYYY HH24:MI Текст уведомления\r\n
                        где /add_task тип команды для добавления уведомления в журнал заданий;\r\n
                        DD.MM.YYYY HH24:MI формат даты и времени, когда следует отправить уведомление собеседнику;\r\n
                        Текст уведомления - сама фраза уведомления, которую следует при выполнении задания отправить в чат собеседнику;\r\n
                        /show_tasks DD.MM.YYYY HH24\r\n
                        где /show_tasks тип команды для вывода уведомлений в журнале заданий, которые назначены на указанную дату в указанный час;
                        DD.MM.YYYY HH24 формат даты и часа, назначенных заданий\r\n
                        /help - команда вывода подсказки\r\n
                        /start - включение режима обработки команд\r\n
                        /end - выключение режима обработки команд
                        """;
            } else if ((content.contains("/add_task") || content.contains("/show_tasks")) && dasNeedToTreateNotifies) {
                String notification = parseNotificationTask(content);
                if (notification.startsWith("ERROR")) {
                    messageText = notification.replaceAll("ERROR", "\uD83D\uDE22 ");
                } else if (content.contains("/add_task")) {
                    sendTime = LocalDateTime.parse(notification.split("&")[0], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                    notifTaskRepo.addNotifOnDate(justNow,
                            notification.split("&")[1],
                            sendTime,
                            update.message().chat().id());
                    messageText = "Принята команда /add_task. Задание уведомления установлено на\r\n" +
                    sendTime.truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                } else if (content.contains("/show_tasks")){
                    List<String> recSet = notifTaskRepo.getNotifOnDate(notification);
                    if (recSet.size() > 0) messageText = String.join("\r\n", recSet);
                    else messageText = "На выбранную дату и час ничего не запланировано!";
                }
            } else if (content.contains("/end")) {
                messageText = "Принята команда /end. Для вызова справки отправьте /help";
                dasNeedToTreateNotifies = false;
            }
            if (messageText != null){
                SendMessage messageToChat = new SendMessage(chatId, messageText);
                SendResponse responseFromBot = telegramBot.execute(messageToChat);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
