package de.timmi6790.server_management_module.commands;

import de.timmi6790.discord_framework.modules.command.AbstractCommand;
import de.timmi6790.discord_framework.modules.command.CommandParameters;
import de.timmi6790.discord_framework.modules.command.CommandResult;
import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.server_management_module.ServerManagementModule;
import de.timmi6790.server_management_module.utilities.EmoteUtilities;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public class MimicEmoteAddCommand extends AbstractCommand<ServerManagementModule> {
    private final Set<Long> activeUser = new HashSet<>();

    public MimicEmoteAddCommand() {
        super("MimicEmote", "Info", "The bot will add all emotes you add", "", "me");
    }

    @Override
    protected CommandResult onCommand(final CommandParameters commandParameters) {
        final long userId = commandParameters.getUserDb().getDiscordId();
        if (this.activeUser.contains(userId)) {
            this.activeUser.remove(userId);

            sendTimedMessage(
                    commandParameters,
                    getEmbedBuilder(commandParameters)
                            .setTitle("Disabled Mimic")
                            .setDescription("Disabled the mimic mode."),
                    90
            );
        } else {
            this.activeUser.add(userId);

            sendTimedMessage(
                    commandParameters,
                    getEmbedBuilder(commandParameters)
                            .setTitle("Enabled Mimic")
                            .setDescription("The bot will now mimic all your emote adds"),
                    90
            );
        }

        return CommandResult.SUCCESS;
    }

    @SubscribeEvent
    public void onReactionAdd(final GuildMessageReactionAddEvent event) {
        if (!this.activeUser.contains(event.getUserIdLong())) {
            return;
        }

        event.getChannel()
                .retrieveMessageById(event.getMessageIdLong())
                .queue(channel -> channel.addReaction(EmoteUtilities.getEmoteName(event.getReactionEmote())).queue());
    }
}
