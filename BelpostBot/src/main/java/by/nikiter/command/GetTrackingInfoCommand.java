package by.nikiter.command;

import by.nikiter.TgBot;
import by.nikiter.model.PropManager;
import by.nikiter.model.UserState;
import by.nikiter.model.comparator.UserTrackingCreatedAtComparator;
import by.nikiter.model.comparator.UserTrackingNameComparator;
import by.nikiter.model.db.entity.UserTrackingEntity;
import by.nikiter.model.db.service.ServiceManager;
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
 * Command that start the two-step process of getting info about tracking.
 * First step is change user state to {@link UserState#CHOOSING_TRACKING_TO_GET} and send a message with inline keyboard
 * Second step is handles by {@link TgBot#processNonCommandUpdate(Update)}
 *
 * @author NikiTer
 */
public class GetTrackingInfoCommand extends BotCommand {

    private static final String IDENTIFIER = "get_tracking_info";
    private static final String DESC = "Выводит информацию о выбранном почтовом отправлении";

    public GetTrackingInfoCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        try {
            SendMessage message;
            if (manager.getUserService().hasTrackings(user.getUserName())) {
                message = new SendMessage(chat.getId(),PropManager.getMessage("command.get_tracking_info.choose"))
                        .setReplyMarkup(getKeyboard(manager.getUserService()
                                .getAllTrackingsNumbersAndNames(user.getUserName())));
                manager.getUserService().changeUserState(user.getUserName(), UserState.CHOOSING_TRACKING_TO_GET);
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
     * @param pairs list of user's trackings
     * @return inline keyboard with user's trackings
     */
    private InlineKeyboardMarkup getKeyboard(List<String[]> pairs) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int i=0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String[] pair : pairs) {
            if (i==2) {
                rows.add(row);
                row = new ArrayList<>();
                i=0;
            }
            row.add(new InlineKeyboardButton(pair[1]).setCallbackData(pair[0]));
            i++;
        }
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}
