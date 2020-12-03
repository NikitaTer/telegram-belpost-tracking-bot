package by.nikiter;

import by.nikiter.command.*;
import by.nikiter.model.ParserHTML;
import by.nikiter.model.PropManager;
import by.nikiter.model.TrackingUpdater;
import by.nikiter.model.db.service.ServiceManager;
import by.nikiter.model.UserState;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Telegram bot class
 * @author NikiTer
 */
public class TgBot extends TelegramLongPollingCommandBot {

    private static volatile TgBot instance = null;

    public static TgBot getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (TgBot.class) {
            if (instance == null) {
                instance = new TgBot(ApiContext.getInstance(DefaultBotOptions.class));
            }
            return instance;
        }
    }

    /**
     * Bot initialization.
     * Creating and registering commands and default action
     * @see StartCommand
     * @see HelpCommand
     * @see AddTrackingsCommand
     * @see DeleteTrackingCommand
     * @see GetTrackingsInfoCommand
     * @see TelegramLongPollingCommandBot#registerDefaultAction(BiConsumer)
     */
    public TgBot(DefaultBotOptions options) {
        super(options);

        HelpCommand helpCommand = new HelpCommand(this);

        register(new StartCommand());
        register(helpCommand);
        register(new AddTrackingsCommand());
        register(new DeleteTrackingCommand());
        register(new GetTrackingsListCommand());
        register(new GetTrackingsInfoCommand());
        register(new GetTrackingInfoCommand());
        register(new CheckForUpdatesCommand());

        registerDefaultAction((absSender, message) -> {
            try {
                absSender.execute(new SendMessage(message.getChatId(), PropManager.getMessage("wrong_command")));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
        });

        TrackingUpdater.getInstance().init();
        TrackingUpdater.getInstance().startPinging();
    }

    /**
     * Method that called when bot receive non-command message.
     * If message contains callback query call {@link TgBot#processCallbackQuery(CallbackQuery)}
     * If message contains a response to the bot call {@link TgBot#processUserMessage(Message)}
     *
     * @param update Object that contains non-command message and info about it
     * @see TgBot#processCallbackQuery(CallbackQuery)
     * @see TgBot#processUserMessage(Message)
     */
    @Override
    public void processNonCommandUpdate(Update update) {

        try {

            if (update.hasCallbackQuery()) {
                processCallbackQuery(update.getCallbackQuery());
            } else if (!update.hasMessage() || !update.getMessage().hasText()) {
                execute(new SendMessage(update.getMessage().getChatId(), PropManager.getMessage("empty_message")));
            } else {
                processUserMessage(update.getMessage());
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
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

        ServiceManager manager = new ServiceManager();
        manager.openSession();

        switch (UserState.getEnum(manager.getUserService().getUserState(query.getFrom().getUserName()).getName())) {
            case CHOOSING_TRACKING_TO_DELETE: {
                String name = manager.getUserService().getTrackingName(query.getFrom().getUserName(), query.getData());
                if (manager.getUserService().removeTracking(query.getFrom().getUserName(), query.getData())) {
                    execute(new SendMessage(
                            query.getMessage().getChatId(),
                            PropManager.getMessage("command.delete_tracking.done")
                                    .replaceAll("%name%", name)
                    ));
                    if (manager.getTrackingService().tryToDeleteTracking(query.getData())) {
                        TrackingUpdater.getInstance().stopUpdate(query.getData());
                    }
                } else {
                    execute(new SendMessage(
                            query.getMessage().getChatId(),
                            PropManager.getMessage("command.delete_tracking.failed")
                                    .replaceAll("%name%", name)
                                    .replaceAll("%num%", query.getData())
                    ));
                }
                manager.getUserService().changeUserState(query.getFrom().getUserName(), UserState.USING_BOT);
                break;
            }

            case CHOOSING_TRACKING_TO_GET: {
                String[] info = ParserHTML.getTrackingMessage(query.getData());
                if (info[0] == null) {
                    execute(new SendMessage(query.getMessage().getChatId(),
                            PropManager.getMessage("error.no_response")
                    ));
                    break;
                }
                String name = manager.getUserService().getTrackingName(query.getFrom().getUserName(), query.getData());
                StringBuilder sb = new StringBuilder();
                sb.append((PropManager.getMessage("tracking_message.tracking_name"))).append("\n")
                        .append(name).append("\n")
                        .append(PropManager.getMessage("tracking_message.tracking_number"))
                        .append(query.getData()).append("\n")
                        .append(PropManager.getMessage("tracking_message.tracking_info")).append("\n\n")
                        .append(info[0]);
                if (info[1] != null) {
                    manager.getTrackingService().updateTrackingInfo(query.getData(),info[1]);
                    TrackingUpdater.getInstance().startOrRestartUpdate(query.getData());
                }
                execute(new SendMessage(query.getMessage().getChatId(),sb.toString()).enableHtml(true));
                manager.getUserService().changeUserState(query.getFrom().getUserName(), UserState.USING_BOT);
                break;
            }

            default:
                break;
        }

        manager.closeSession();
    }

    /**
     * Method that handles user's message according to its state
     *
     * @param message message from user
     * @throws TelegramApiException when bot can't send message
     * @see UserState
     */
    private void processUserMessage(Message message) throws TelegramApiException {

        ServiceManager manager = new ServiceManager();
        manager.openSession();

        switch (UserState.getEnum(manager.getUserService().getUserState(message.getFrom().getUserName()).getName())) {
            case USING_BOT:
                execute(new SendMessage(message.getChatId(), PropManager.getMessage("non_command")));
                break;

            case ENTERING_TRACKING: {
                Matcher matcher = Pattern.compile("[A-Z]{2}[0-9]{9}[A-Z]{2} [^\n%]{1,255}\n")
                        .matcher(message.getText() + "\n");
                StringBuilder sb = new StringBuilder();
                if (matcher.find()) {
                    do {
                        String[] temp = matcher.group().replaceFirst(" ", "%")
                                .replaceAll("\n", "").split("%");
                        String trackingNumber = temp[0];
                        String trackingName = temp[1];

                        if (manager.getUserService().hasTracking(message.getFrom().getUserName(), trackingNumber)) {
                            sb.append(
                                    PropManager.getMessage("command.add_trackings.already")
                                            .replaceAll("%num%", trackingNumber)
                                            .replaceAll("%name%", manager.getUserService().getTrackingName(
                                                    message.getFrom().getUserName(), trackingNumber
                                                    )
                                            )
                            ).append("\n");
                        } else {
                            manager.getUserService()
                                    .addTracking(message.getFrom().getUserName(), trackingNumber, trackingName);
                            sb.append(
                                    PropManager.getMessage("command.add_trackings.added")
                                            .replaceAll("%num%", trackingNumber)
                                            .replaceAll("%name%", trackingName)
                            ).append("\n");
                        }
                    } while (matcher.find());
                } else {
                    sb.append(PropManager.getMessage("command.add_trackings.failed"));
                }

                manager.getUserService().changeUserState(message.getFrom().getUserName(), UserState.USING_BOT);
                execute(new SendMessage(message.getChatId(), sb.toString()));
                break;
            }

            default:
                break;
        }

        manager.closeSession();
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
