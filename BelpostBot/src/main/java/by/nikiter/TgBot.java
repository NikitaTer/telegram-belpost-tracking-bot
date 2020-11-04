package by.nikiter;

import by.nikiter.command.*;
import by.nikiter.model.PropManager;
import by.nikiter.model.tracker.PostTracker;
import by.nikiter.model.UserState;
import by.nikiter.model.UsersRep;
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

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Telegram bot class
 * @author NikiTer
 */
public class TgBot extends TelegramLongPollingCommandBot {

    /**
     * Creating and registering bot with default bot options
     * @see DefaultBotOptions
     */
    public static void main(String[] args) {
        ApiContextInitializer.init();

        TelegramBotsApi api = new TelegramBotsApi();

        try {
            api.registerBot(new TgBot(ApiContext.getInstance(DefaultBotOptions.class)));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bot initialization.
     * Creating and registering commands and default action
     * @see StartCommand
     * @see HelpCommand
     * @see AddTrackingsCommand
     * @see DeleteTrackingCommand
     * @see GetAllTrackingsCommand
     * @see TelegramLongPollingCommandBot#registerDefaultAction(BiConsumer)
     */
    public TgBot(DefaultBotOptions options) {
        super(options);

        HelpCommand helpCommand = new HelpCommand(this);

        register(new StartCommand(this));
        register(helpCommand);
        register(new AddTrackingsCommand());
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

        PostTracker.getInstance().setBot(this);
    }

    /**
     * Method that called when bot receive non-command message.
     * If message contains callback query call {@link TgBot#processCallbackQuery(CallbackQuery)}
     * If message contains a response to the bot call {@link TgBot#processUserAnswer(Message)}
     *
     * @param update Object that contains non-command message and info about it
     * @see TgBot#processCallbackQuery(CallbackQuery)
     * @see TgBot#processUserAnswer(Message)
     */
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

    /**
     * Method that handles callback query according to user state
     *
     * @param query callback query
     * @throws TelegramApiException when bot can't send message
     * @see UserState
     */
    private void processCallbackQuery(CallbackQuery query) throws TelegramApiException {
        switch (UsersRep.getInstance().getUserState(query.getFrom())) {
            case DELETING_TRACKING:
                if (PostTracker.getInstance().deleteTracking(query.getFrom(),query.getData())) {
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

    /**
     * Method that handles user's response according to its state
     *
     * @param message message from user
     * @throws TelegramApiException when bot can't send message
     * @see UserState
     */
    private void processUserAnswer(Message message) throws TelegramApiException {
        switch (UsersRep.getInstance().getUserState(message.getFrom())) {
            case ENTERING_TRACKING_NUMBER:
                Matcher matcher = Pattern.compile("[A-Z]{2}[0-9]{9}[A-Z]{2}").matcher(message.getText());
                StringBuilder sb = new StringBuilder();
                if (matcher.find()) {
                    do {
                        if (PostTracker.getInstance().hasTracking(message.getFrom(),matcher.group())) {
                            sb.append(
                                    PropManager.getMessage("add_trackings.already")
                                            .replaceAll("%num%",matcher.group())
                            ).append("\n");
                        } else {
                            PostTracker.getInstance().addTracking(message.getFrom(), matcher.group());
                            sb.append(
                                    PropManager.getMessage("add_trackings.added")
                                            .replaceAll("%num%", matcher.group())
                            ).append("\n");
                        }
                    } while (matcher.find());
                } else {
                    sb.append(PropManager.getMessage("add_trackings.failed"));
                }
                UsersRep.getInstance().setUserState(message.getFrom(), UserState.USING_BOT);
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
