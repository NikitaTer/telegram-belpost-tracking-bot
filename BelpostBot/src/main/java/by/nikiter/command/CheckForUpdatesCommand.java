package by.nikiter.command;

import by.nikiter.model.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.TrackingUpdater;
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
 * Command that checking for updates all users trackings.
 *
 * @author NikiTer
 */
public class CheckForUpdatesCommand extends BotCommand {

    private static final String IDENTIFIER = "check_for_updates";
    private static final String DESC = "Проверить наличие обновлений по всем почтовым отправлениям";

    public CheckForUpdatesCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        manager.getUserService().changeUserState(user.getUserName(), UserState.USING_BOT);

        boolean isNoUpdates = true;
        List<String> numbers = manager.getUserService().getAllTrackingsNumbers(user.getUserName());
        for (String num : numbers) {
            String lastEvent = ParserHTML.getLastEvent(num);
            if (lastEvent != null) {
                if (    manager.getTrackingService().updateTrackingInfo(num, lastEvent)
                        && isNoUpdates
                ) {
                    isNoUpdates = false;
                }
                TrackingUpdater.getInstance().startOrRestartUpdate(num);
            }
        }

        if (isNoUpdates) {
            try {
                absSender.execute(new SendMessage(
                        chat.getId(),
                        PropManager.getMessage("command.check_for_updates.no_info")
                ).enableHtml(true));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        manager.closeSession();
    }
}
