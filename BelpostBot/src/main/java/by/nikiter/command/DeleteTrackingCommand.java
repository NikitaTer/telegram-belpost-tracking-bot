package by.nikiter.command;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import by.nikiter.model.comparator.UserTrackingCreatedAtComparator;
import by.nikiter.model.comparator.UserTrackingNameComparator;
import by.nikiter.model.db.entity.UserTrackingEntity;
import by.nikiter.model.db.service.ServiceManager;
import by.nikiter.model.UserState;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Command that start the two-step process of deleting user tracking.
 * First step is change user state to {@link UserState#CHOOSING_TRACKING_TO_DELETE} and send a message with inline keyboard
 * Second step is handles by {@link TgBot#processNonCommandUpdate(Update)}
 *
 * @see by.nikiter.model.ParserHTML#getLastEvent(String)
 * @author NikiTer
 */
public class DeleteTrackingCommand extends BotCommand {

    private static final String IDENTIFIER = "delete_tracking";
    private static final String DESC = "Удалить отслеживаемое почтовое отправление";

    public DeleteTrackingCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        try {
            SendMessage message;
            if (manager.getUserService().hasTrackings(user.getUserName())) {
                message = new SendMessage(chat.getId(),PropManager.getMessage("command.delete_tracking.choose"))
                        .setReplyMarkup(getKeyboard(manager.getUserService().getAllTrackings(user.getUserName())));
                manager.getUserService().changeUserState(user.getUserName(), UserState.CHOOSING_TRACKING_TO_DELETE);
            } else {
                message = new SendMessage(chat.getId(),PropManager.getMessage("command.no_trackings"));
                manager.getUserService().changeUserState(user.getUserName(), UserState.USING_BOT);
            }
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        manager.closeSession();
    }

    /**
     * Method that makes inline keyboard out of user's trackings
     * @param trackings list of user's trackings
     * @return inline keyboard with user's trackings
     */
    private InlineKeyboardMarkup getKeyboard(List<UserTrackingEntity> trackings) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int i=0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
        for (UserTrackingEntity ute : trackings) {
            if (i==2) {
                rows.add(row);
                row = new ArrayList<>();
                i=0;
            }
            row.add(new InlineKeyboardButton(ute.getTrackingName()).setCallbackData(ute.getTracking().getNumber()));
            i++;
        }
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}