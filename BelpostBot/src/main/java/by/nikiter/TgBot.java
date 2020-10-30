package by.nikiter;

import by.nikiter.command.*;
import by.nikiter.model.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.belpost.PostTracker;
import by.nikiter.model.state.UserState;
import by.nikiter.model.state.UsersRep;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TgBot extends TelegramLongPollingCommandBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new TgBot(ApiContext.getInstance(DefaultBotOptions.class)));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public TgBot(DefaultBotOptions options) {
        super(options);

        HelpCommand helpCommand = new HelpCommand(this);

        register(new StartCommand(this));
        register(helpCommand);
        register(new AddTrackingCommand());
        register(new DeleteTrackingCommand());
        register(new GetAllTrackingsCommand());

        registerDefaultAction((absSender, message) -> {
            try {
                absSender.execute(new SendMessage(message.getChatId(), PropManager.getMessage("wrong_command")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {

        if (update.hasCallbackQuery()) {
            try {
                processCallbackQuery(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (update.hasMessage() && update.getMessage().hasText()
                && UsersRep.getInstance().getUserState(update.getMessage().getFrom()) != UserState.USING_BOT) {
            try {
                processUserAnswer(update.getMessage());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (!update.hasMessage() || !update.getMessage().hasText()) {
            try {
                execute(new SendMessage(update.getMessage().getChatId(), PropManager.getMessage("empty_message")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            try {
                execute(new SendMessage(update.getMessage().getChatId(), PropManager.getMessage("non_command")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void processCallbackQuery(CallbackQuery query) throws TelegramApiException {
        switch (UsersRep.getInstance().getUserState(query.getFrom())) {
            case DELETING_TRACKING:
                if (PostTracker.getInstance().deleteTracking(query.getFrom(),query.getData())) {
                    ParserHTML.getInstance().stopUpdating(query.getData());
                    execute(new SendMessage(
                            query.getMessage().getChatId(),
                            PropManager.getMessage("delete_tracking.done").replaceAll("%num%",query.getData())
                    ));
                }
                break;

            default:
                break;
        }
    }

    private void processUserAnswer(Message message) throws TelegramApiException {
        switch (UsersRep.getInstance().getUserState(message.getFrom())) {
            case ENTERING_TRACKING_NUMBER:
                Matcher matcher = Pattern.compile("[A-Z]{2}[0-9]{9}[A-Z]{2}").matcher(message.getText());
                StringBuilder sb = new StringBuilder();
                if (matcher.find()) {
                    do {
                        if (PostTracker.getInstance().isContainsTracker(message.getFrom(),matcher.group())) {
                            sb.append(
                                    PropManager.getMessage("add_tracking.already")
                                            .replaceAll("%num%",matcher.group())
                            ).append("\n");
                        } else {
                            PostTracker.getInstance().addUserTracking(message.getFrom(), matcher.group());
                            sb.append(
                                    PropManager.getMessage("add_tracking.added")
                                            .replaceAll("%num%", matcher.group())
                            ).append("\n");
                        }
                    } while (matcher.find());
                } else {
                    sb.append(PropManager.getMessage("add_tracking.failed"));
                }
                UsersRep.getInstance().updateUserState(message.getFrom(), UserState.USING_BOT);
                execute(new SendMessage(message.getChatId(),sb.toString()));
                break;

            default:
                break;
        }
    }

    @Override
    public String getBotUsername() {
        return PropManager.getData("bot.username");
    }


    @Override
    public String getBotToken() {
        return PropManager.getData("bot.token");
    }
}
