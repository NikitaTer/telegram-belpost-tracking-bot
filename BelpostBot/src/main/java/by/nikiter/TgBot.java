package by.nikiter;

import by.nikiter.command.HelpCommand;
import by.nikiter.command.StartCommand;
import by.nikiter.model.PropManager;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TgBot extends TelegramLongPollingCommandBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new TgBot(ApiContext.getInstance(DefaultBotOptions.class)));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public TgBot(DefaultBotOptions options) {
        super(options);

        HelpCommand helpCommand = new HelpCommand(this);

        register(new StartCommand());
        register(helpCommand);

        registerDefaultAction((absSender, message) -> {
            try {
                absSender.execute(new SendMessage(message.getChatId(), PropManager.getMessage("wrong_command")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        try {
            execute(new SendMessage(update.getCallbackQuery().getMessage().getChatId(), PropManager.getMessage("non_command")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return PropManager.getData("bot.username");
    }


    @Override
    public String getBotToken() {
        return PropManager.getData("bot.token");
    }
}
