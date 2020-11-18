package by.nikiter.command;

import by.nikiter.model.PropManager;
import by.nikiter.model.UserState;
import by.nikiter.model.db.service.ServiceManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Command that starts dialog between user and bot.
 * Also creates reply keyboard for user
 *
 * @author NikiTer
 */
public class StartCommand extends BotCommand {

    private static final String IDENTIFIER = "start";
    private static final String DESC = "Начать использовать бот";

    public StartCommand() {
        super(IDENTIFIER, DESC);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        StringBuilder sb = new StringBuilder();

        ServiceManager manager = new ServiceManager();
        manager.openSession();

        if (manager.getUserService().hasUser(user.getUserName())) {
            sb.append(PropManager.getMessage("command.start.already"));
            manager.getUserService().changeUserState(user.getUserName(), UserState.USING_BOT);
        } else {
            sb.append(PropManager.getMessage("command.start"));
            manager.getUserService().addUser(user.getUserName(),chat.getId());
        }

        manager.closeSession();

        try {
            absSender.execute(new SendMessage(chat.getId(), sb.toString()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}