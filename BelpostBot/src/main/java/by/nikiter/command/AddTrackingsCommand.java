package by.nikiter.command;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import by.nikiter.model.UserState;
import by.nikiter.model.db.service.ServiceManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Command that start the two-step process of adding a new tracking to user.
 * First step is change user state to {@link UserState#ENTERING_TRACKING} and send a message
 * Second step is handles by {@link TgBot#processNonCommandUpdate(Update)}
 *
 * @author NikiTer
 */
public class AddTrackingsCommand extends BotCommand {

    private static final String IDENTIFIER = "add_trackings";
    private static final String DESC = "Добавить почтовые отправления для отслеживания";

    public AddTrackingsCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        ServiceManager manager = new ServiceManager();
        manager.openSession();
        manager.getUserService().changeUserState(user.getUserName(), UserState.ENTERING_TRACKING);
        manager.closeSession();

        try {
            absSender.execute(new SendMessage(chat.getId(), PropManager.getMessage("command.add_trackings.enter")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}