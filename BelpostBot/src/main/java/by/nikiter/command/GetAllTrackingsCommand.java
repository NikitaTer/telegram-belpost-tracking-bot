package by.nikiter.command;

import by.nikiter.model.parser.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.tracker.PostTracker;
import by.nikiter.model.UserState;
import by.nikiter.model.UsersRep;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GetAllTrackingsCommand extends BotCommand {

    private static final String IDENTIFIER = "get_all_trackings";
    private static final String DESC = "Выводит инофрмацию о всех почтовых отправлениях";

    public GetAllTrackingsCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        UsersRep.getInstance().setUserState(user, UserState.USING_BOT);

        try {

            if (!PostTracker.getInstance().hasTrackings(user)) {
                absSender.execute(new SendMessage(chat.getId(),
                        PropManager.getMessage("no_trackings")));
            } else {
                for (String trackingNum : PostTracker.getInstance().getAllTrackings(user)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(PropManager.getMessage("tracking_message.tracking_number"))
                            .append(trackingNum).append("\n")
                            .append(PropManager.getMessage("tracking_message.tracking_info")).append("\n\n")
                            .append(ParserHTML.getTrackingMessage(trackingNum));
                    PostTracker.getInstance().startUpdating(trackingNum);
                    absSender.execute(new SendMessage(chat.getId(),sb.toString()).enableHtml(true));
                }
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
