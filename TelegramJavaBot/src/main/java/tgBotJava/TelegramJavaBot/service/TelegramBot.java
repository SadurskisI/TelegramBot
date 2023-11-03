package tgBotJava.TelegramJavaBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgBotJava.TelegramJavaBot.config.BotConfig;
import tgBotJava.TelegramJavaBot.entity.User;
import tgBotJava.TelegramJavaBot.entity.WeatherData;
import tgBotJava.TelegramJavaBot.entity.WorkTime;
import tgBotJava.TelegramJavaBot.repository.UserRepository;
import tgBotJava.TelegramJavaBot.repository.UserRoleRepository;
import tgBotJava.TelegramJavaBot.repository.WorkTimeRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private WorkTimeRepository workTimeRepository;
    final BotConfig config;

    /**
     * Command list for telegram keyboard menu
     *
     * @param config class to bot configuration with token and name
     */
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start bot"));
        listOfCommands.add(new BotCommand("/stop", "stop bot"));
        listOfCommands.add(new BotCommand("/workstart", "press to start working day"));
        listOfCommands.add(new BotCommand("/workend", "press to end working day"));
        listOfCommands.add(new BotCommand("/stat", "statistic command for admins only"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error in command bot list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    /* locally written city for weather Api */
    String userCity = "Riga";


    /**
     * Method for command usage
     *
     * @param update for getting a message
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/stop":
                    stopCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/workstart":
                    startWork(update.getMessage());
                    break;
                case "/workend":
                    stopWork(update.getMessage());
                    break;
                case "/stat":
                    getStat(update.getMessage());
                    break;
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");

            }
        }
    }

    /**
     * Method for adding user in db
     *
     * @param msg used for user chat values
     */
    private void registerUser(Message msg) {
        if (userRepository.findByChatId(msg.getChatId()) == null) {
            long chatId = msg.getChatId();
            Chat chat = msg.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            user.setIsActive("True");
            user.setRole("USER");

            userRepository.save(user);
            log.info("user saved to db " + user);
        }
    }

    /**
     * Method for bot start and monitor active users
     *
     * @param chatId choose correct user
     * @param name   getting userName from user
     */
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", Nice to meet you!";
        log.info("Replied to user " + name);
        User user = userRepository.findByChatId(chatId);
        user.setIsActive("True");

        userRepository.save(user);
        sendMessage(chatId, answer);
    }

    /**
     * Method for stopping bot and change active user to false
     *
     * @param chatId choose correct user
     * @param name   getting userName from user
     */
    private void stopCommandReceived(long chatId, String name) {
        String answer = "Bye, " + name + "!";
        log.info("Replied to user " + name);

        User user = userRepository.findByChatId(chatId);
        user.setIsActive("False");

        userRepository.save(user);
        sendMessage(chatId, answer);
    }

    /**
     * Method to save user info when he started work day
     *
     * @param msg request user info
     */
    /* tmp variable for Timestamp at work starting time. */
    WorkTime workTime = new WorkTime();
    Timestamp tmpStartedAt = null;
    private void startWork(Message msg) {
        String answer = "Have a nice work day, " + msg.getChat().getFirstName() + "!";
        log.info(msg.getChat().getFirstName() + " started work day");

        workTime.setUserId(msg.getChatId());
        workTime.setUserName(msg.getChat().getFirstName());
        tmpStartedAt = new Timestamp(System.currentTimeMillis());

        sendMessage(msg.getChatId(), answer);
    }



    /**
     * Method for calculate user workday time and response some weather info in chat
     *
     * @param msg request user info
     */
    private void stopWork(Message msg) {

        workTime.setStartedAt(tmpStartedAt);
        Timestamp tmpEndedAt = new Timestamp(System.currentTimeMillis());
        workTime.setEndedAt(tmpEndedAt);

        long timeDifference = tmpEndedAt.getTime() - tmpStartedAt.getTime();
        long hours = timeDifference / (60 * 60 * 1000);
        long minutes = (timeDifference / (60 * 1000)) % 60;
        String hrsWorked = String.format("%02d:%02d", hours, minutes);
        workTime.setHoursWorked(hrsWorked);
        WeatherData weatherData = weatherService.getWeatherData(userCity);
        String weatherDescription = weatherData.getWeather()[0].getDescription();
        double temperatureInKelvin = weatherData.getMain().getTemp();
        double celsiusTemp = Math.round(temperatureInKelvin - 273.15);
        workTime.setWeather(weatherDescription);

        workTimeRepository.save(workTime);
        String answer = "End working day for " + msg.getChat().getFirstName() +
                "! The weather in " + userCity + " is `" + weatherDescription +
                "` with a temperature of " + celsiusTemp + "°C.";
        log.info(msg.getChat().getFirstName() + " ended work day");

        sendMessage(msg.getChatId(), answer);
    }

    /**
     * Method for admins statistic to see active users
     *
     * @param msg request user info
     */
    private void getStat(Message msg) {
        User user = userRepository.findByChatId(msg.getChatId());
        assert user != null;

        if (user.getRole().equals("ADMIN")) {
            int active = userRepository.findActiveUsers();
            String answer = "Active users count is " + active;
            log.info(msg.getChat().getFirstName() + " is used statistic command");
            sendMessage(msg.getChatId(), answer);
        } else {
            sendMessage(msg.getChatId(), "У вас нет прав администратора.");
        }
    }

    /**
     * Method to allow bot answer to current chats
     *
     * @param chatId     requested chatId to answer
     * @param textToSend answer text
     */
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
