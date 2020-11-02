package by.nikiter.command;

import by.nikiter.model.PropManager;
import by.nikiter.model.UsersRep;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class StartCommand extends BotCommand {

    private static final String IDENTIFIER = "start";
    private static final String DESC = "Начать использовать бот";

    private final ICommandRegistry commandRegistry;

    public StartCommand(ICommandRegistry commandRegistry) {
        super(IDENTIFIER, DESC);
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder sb = new StringBuilder();

        if (UsersRep.getInstance().hasUser(user)) {
            sb.append(PropManager.getMessage("start.already"));
        } else {
            sb.append(PropManager.getMessage("start"));
            UsersRep.getInstance().addUser(user, chat);
        }

        try {
            absSender.execute(new SendMessage(chat.getId(), sb.toString()).setReplyMarkup(getKeyboard()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup getKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow rowFirst = new KeyboardRow();
        rowFirst.add("/help");
        rowFirst.add("/add_tracking");
        rowFirst.add("/get_all_trackings");

        KeyboardRow rowSecond = new KeyboardRow();
        rowFirst.add("/delete_tracking");

        rows.add(rowFirst);
        rows.add(rowSecond);

        return new ReplyKeyboardMarkup(rows).setResizeKeyboard(true);
    }
}