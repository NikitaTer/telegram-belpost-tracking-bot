package by.nikiter.command;

import by.nikiter.model.PropManager;
import by.nikiter.model.state.UserState;
import by.nikiter.model.state.UsersRep;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AddTrackingCommand extends BotCommand {

    private static final String IDENTIFIER = "add_tracking";
    private static final String DESC = "Добавить трэкинг";

    public AddTrackingCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        UsersRep.getInstance().updateUserState(user, UserState.ENTERING_TRACKING_NUMBER);

        try {
            absSender.execute(new SendMessage(chat.getId(), PropManager.getMessage("add_tracking.enter")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
