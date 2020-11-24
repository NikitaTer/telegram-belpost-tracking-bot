package by.nikiter;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class App {

    /**
     * Creating and registering bot with default bot options
     * @see DefaultBotOptions
     */
    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(TgBot.getInstance());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
