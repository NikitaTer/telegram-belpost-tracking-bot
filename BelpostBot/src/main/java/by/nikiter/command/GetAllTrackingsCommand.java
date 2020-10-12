package by.nikiter.command;

import by.nikiter.model.ParserJSON;
import by.nikiter.model.PropManager;
import by.nikiter.model.belpost.PostTracker;
import by.nikiter.model.belpost.entity.Item;
import by.nikiter.model.belpost.entity.OriginInfo;
import by.nikiter.model.belpost.entity.Trackinfo;
import by.nikiter.model.belpost.entity.Tracking;
import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class GetAllTrackingsCommand extends BotCommand {

    private static final String IDENTIFIER = "get_all_trackings";
    private static final String DESC = "Выводит данные о всех почтовых отправлениях";

    public GetAllTrackingsCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        Tracking tracking = ParserJSON.getInstance().getTracking(
                PostTracker.getInstance().getAllTracking(user, "ru")
        );

        try {
            if (tracking.getTrackingData().getTotal() == 0) {
                absSender.execute(new SendMessage(chat.getId(),
                        PropManager.getMessage("get_all_trackings.no_trackings")));
            } else {

                absSender.execute(
                        new SendMessage(chat.getId(), PropManager.getMessage("get_all_trackings")).enableHtml(true)
                );
                String arrowUp = EmojiParser.parseToUnicode("         :arrow_up:\n");

                for (Item item : tracking.getTrackingData().getItems()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(PropManager.getMessage("tracking_message.tracking_number"))
                            .append(item.getTrackingNumber()).append("\n")
                            .append(PropManager.getMessage("tracking_message.status"))
                            .append(item.getStatus()).append("\n")
                            .append(PropManager.getMessage("tracking_message.original_country"))
                            .append(item.getOriginalCountry()).append("\n")
                            .append(PropManager.getMessage("tracking_message.last_event"))
                            .append(item.getLastEvent()).append("\n")
                            .append(PropManager.getMessage("tracking_message.last_update_time"))
                            .append(item.getLastUpdateTime()).append("\n")
                            .append(PropManager.getMessage("tracking_message.tracking_info"))
                            .append("\n\n").append("=======================");
                    for (Trackinfo ti : item.getOriginInfo().getTrackinfo()) {
                        sb.append(PropManager.getMessage("tracking_message.spec_status"))
                                .append(ti.getStatusDescription()).append("\n")
                                .append(PropManager.getMessage("tracking_message.details"))
                                .append(ti.getDetails()).append("\n")
                                .append(PropManager.getMessage("tracking_message.date"))
                                .append(ti.getDate()).append("\n")
                                .append("=======================");
                        if (!ti.equals(
                                item.getOriginInfo().getTrackinfo().get(item.getOriginInfo().getTrackinfo().size() - 1)
                        )) {
                            sb.append(arrowUp).append(arrowUp).append(arrowUp).append(arrowUp);
                        }
                    }

                    absSender.execute(new SendMessage(chat.getId(),sb.toString()).enableHtml(true));
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
