package by.nikiter.command;

import by.nikiter.model.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.UserState;
import by.nikiter.model.comparator.UserTrackingCreatedAtComparator;
import by.nikiter.model.comparator.UserTrackingNameComparator;
import by.nikiter.model.db.entity.UserTrackingEntity;
import by.nikiter.model.db.service.ServiceManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Command that sends to user list of his trackings
 *
 * @author NikiTer
 */
public class GetTrackingsListCommand extends BotCommand {

    private static final String IDENTIFIER = "get_trackings_list";
    private static final String DESC = "Выводит список всех почтовых отправлений";

    public GetTrackingsListCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        manager.getUserService().changeUserState(user.getUserName(), UserState.USING_BOT);

        try {

            if (!manager.getUserService().hasTrackings(user.getUserName())) {
                absSender.execute(new SendMessage(chat.getId(),
                        PropManager.getMessage("command.no_trackings")));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(PropManager.getMessage("command.get_trackings_list")).append("\n");
                List<UserTrackingEntity> trackings = manager.getUserService().getAllTrackings(user.getUserName());
                trackings.sort(new UserTrackingCreatedAtComparator().thenComparing(new UserTrackingNameComparator()));
                for (UserTrackingEntity ute : trackings) {
                    sb.append(ute.getTracking().getNumber()).append(" - ").append(ute.getTrackingName()).append("\n");
                }
                absSender.execute(new SendMessage(chat.getId(),sb.toString()).enableHtml(true));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        manager.closeSession();
    }
}