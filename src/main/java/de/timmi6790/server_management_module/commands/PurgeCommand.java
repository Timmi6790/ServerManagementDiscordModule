package de.timmi6790.server_management_module.commands;

import de.timmi6790.discord_framework.modules.command.AbstractCommand;
import de.timmi6790.discord_framework.modules.command.CommandParameters;
import de.timmi6790.discord_framework.modules.command.CommandResult;
import de.timmi6790.discord_framework.modules.command.exceptions.CommandReturnException;
import de.timmi6790.discord_framework.modules.command.properties.MinArgCommandProperty;
import de.timmi6790.server_management_module.ServerManagementModule;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.MessageChannel;

public class PurgeCommand extends AbstractCommand<ServerManagementModule> {
    public PurgeCommand() {
        super("purge", "Management", "Purges messages", "<amount>");

        this.addProperties(
                new MinArgCommandProperty(1)
        );
    }

    protected int getIntInRangeThrow(final CommandParameters commandParameters, final int argPos, final int lowerLimit, final int upperLimit) {
        final String arg = commandParameters.getArgs()[argPos];
        try {
            final int value = Integer.parseInt(arg);
            if (value >= lowerLimit && upperLimit >= value) {
                return value;
            }

            throw new CommandReturnException(
                    getEmbedBuilder(commandParameters)
                            .setTitle("Invalid Arg")
                            .appendDescription("%s is not between %s and %s", arg, lowerLimit, upperLimit)
            );
        } catch (final NumberFormatException ignore) {
            throw new CommandReturnException(
                    getEmbedBuilder(commandParameters)
                            .setTitle("Invalid Arg")
                            .appendDescription("%s is not a valid integer value!", arg)
            );
        }
    }

    @SneakyThrows
    @Override
    protected CommandResult onCommand(final CommandParameters commandParameters) {
        if (!commandParameters.isGuildCommand()) {
            sendMessage(
                    commandParameters,
                    getEmbedBuilder(commandParameters)
                            .setDescription("You can only use it inside a guild.")
            );
            return CommandResult.FAIL;
        }

        final int amount = this.getIntInRangeThrow(commandParameters, 0, 1, 100);
        final MessageChannel channel = commandParameters.getGuildTextChannel();

        channel.getHistory().retrievePast(amount).queue(channel::purgeMessages);
        return CommandResult.SUCCESS;
    }
}
