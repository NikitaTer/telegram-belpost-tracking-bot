package by.nikiter.command;

import by.nikiter.model.PropManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class StartCommand extends BotCommand {

    private static final String IDENTIFIER = "start";
    private static final String DESC = "Начать использовать бот";

    public StartCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder sb = new StringBuilder();

        sb.append(PropManager.getMessage("start"));

        try {
            absSender.execute(new SendMessage(chat.getId(), sb.toString()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
