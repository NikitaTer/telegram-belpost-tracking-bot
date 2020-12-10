package by.nikiter.command;

import by.nikiter.model.db.service.ServiceManager;
import by.nikiter.model.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.TrackingUpdater;
import by.nikiter.model.UserState;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * Command that sends to user info about his trackings
 *
 * @author NikiTer
 * @see ParserHTML#getTrackingMessage(String)
 */
public class GetTrackingsInfoCommand extends BotCommand {

    private static final String IDENTIFIER = "get_trackings_info";
    private static final String DESC = "Выводит информацию о всех почтовых отправлениях";

    public GetTrackingsInfoCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        ServiceManager manager = new ServiceManager();
        manager.openSession();

        manager.getUserService().changeUserState(user.getUserName(),UserState.USING_BOT);

        try {
            if (!manager.getUserService().hasAnyTracking(user.getUserName())) {
                absSender.execute(new SendMessage(chat.getId(),
                        PropManager.getMessage("command.no_trackings")));
            } else {
                boolean isFirstLoop = true;
                List<String[]> pairs = manager.getUserService()
                        .getAllTrackingsNumbersAndNames(user.getUserName());
                for (String[] pair : pairs) {
                    String[] info = ParserHTML.getTrackingMessage(pair[0]);
                    if (info[0] == null) {
                        absSender.execute(new SendMessage(
                                chat.getId(),PropManager.getMessage("error.no_response")
                        ).enableHtml(true));
                        break;
                    }
                    if (isFirstLoop) {
                        isFirstLoop = false;
                        absSender.execute(new SendMessage(
                                chat.getId(),PropManager.getMessage("command.get_trackings_info")
                        ).enableHtml(true));
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append((PropManager.getMessage("tracking_message.tracking_name"))).append("\n")
                            .append(pair[1]).append("\n")
                            .append(PropManager.getMessage("tracking_message.tracking_number"))
                            .append(pair[0]).append("\n")
                            .append(PropManager.getMessage("tracking_message.tracking_info")).append("\n\n")
                            .append(info[0]);
                    if (info[1] != null) {
                        manager.getTrackingService()
                                .updateTrackingInfo(pair[0],info[1],user.getUserName());
                        TrackingUpdater.getInstance().startOrRestartUpdate(pair[0]);
                    }
                    absSender.execute(new SendMessage(chat.getId(),sb.toString()).enableHtml(true));
                }
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        manager.closeSession();
    }
}