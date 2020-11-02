package by.nikiter.command;

import by.nikiter.model.PropManager;
import by.nikiter.model.tracker.PostTracker;
import by.nikiter.model.UserState;
import by.nikiter.model.UsersRep;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class DeleteTrackingCommand extends BotCommand {

    private static final String IDENTIFIER = "delete_tracking";
    private static final String DESC = "Удалить отслеживаемое почтовое отправление";

    public DeleteTrackingCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        try {
            SendMessage message;
            if (PostTracker.getInstance().hasTrackings(user)) {
                message = new SendMessage(chat.getId(),PropManager.getMessage("delete_tracking.choose"))
                        .setReplyMarkup(getKeyboard(PostTracker.getInstance().getAllTrackings(user)));
                UsersRep.getInstance().setUserState(user, UserState.DELETING_TRACKING);
            } else {
                message = new SendMessage(chat.getId(),PropManager.getMessage("no_trackings"));
                UsersRep.getInstance().setUserState(user, UserState.USING_BOT);
            }
            absSender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getKeyboard(List<String> trackings) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int i=0;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String tr : trackings) {
            if (i==2) {
                rows.add(row);
                row = new ArrayList<>();
                i=0;
            }
            row.add(new InlineKeyboardButton(tr).setCallbackData(tr));
            i++;
        }
        rows.add(row);

        return new InlineKeyboardMarkup(rows);
    }
}
