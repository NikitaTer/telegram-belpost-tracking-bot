package by.nikiter.command;

import by.nikiter.model.PropManager;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class HelpCommand extends BotCommand {

    private static final String IDENTIFIER = "help";
    private static final String DESC = "Возвращает список всех команд";

    private final ICommandRegistry commandRegistry;

    public HelpCommand(ICommandRegistry commandRegistry) {
        super(IDENTIFIER, DESC);
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

        StringBuilder sb = new StringBuilder();

        sb.append(PropManager.getMessage("help"));

        commandRegistry.getRegisteredCommands().forEach(cmd -> sb
                .append(cmd.toString()).append("\n\n"));

        try {
            absSender.execute(new SendMessage(chat.getId(),sb.toString()).enableHtml(true));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
