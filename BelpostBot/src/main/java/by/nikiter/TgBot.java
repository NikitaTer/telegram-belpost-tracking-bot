package by.nikiter;

import by.nikiter.command.AddTrackingCommand;
import by.nikiter.command.GetAllTrackingsCommand;
import by.nikiter.command.HelpCommand;
import by.nikiter.command.StartCommand;
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

        register(new StartCommand());
        register(helpCommand);
        register(new AddTrackingCommand());
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
            processCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage() && update.getMessage().hasText()
                && UsersRep.getInstance().getUserState(update.getMessage().getFrom()) != UserState.USING_BOT) {
            processUserAnswer(update.getMessage());
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

    private void processCallbackQuery(CallbackQuery query) {

    }

    private void processUserAnswer(Message message) {
        try {
            switch (UsersRep.getInstance().getUserState(message.getFrom())) {
                case ENTERING_TRACKING_NUMBER:
                    switch (
                            PostTracker.getInstance().createTracking(message.getFrom(), message.getText(), "ru")
                    ) {
                        case 200:
                            execute(
                                    new SendMessage(message.getChatId(),
                                            PropManager.getMessage("add_tracking.added"))
                            );
                            break;

                        case 4016:
                            if (
                                    PostTracker.getInstance().hasTrackingNumbers(message.getFrom()) &&
                                    PostTracker.getInstance().getAllTrackingNumbers(message.getFrom())
                                    .contains(message.getText())) {
                                execute(
                                        new SendMessage(message.getChatId(),
                                        PropManager.getMessage("add_tracking.already"))
                                );
                            } else {
                                PostTracker.getInstance().addTracking(message.getFrom(),message.getText());
                                execute(
                                        new SendMessage(message.getChatId(),
                                                PropManager.getMessage("add_tracking.added"))
                                );
                            }
                            break;

                        default:
                            break;
                    }
                    UsersRep.getInstance().updateUserState(message.getFrom(), UserState.USING_BOT);
                    break;

                default:
                    break;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
