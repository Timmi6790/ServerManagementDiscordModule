package de.timmi6790.server_management_module.logger;

import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.discord_framework.utilities.discord.DiscordMessagesUtilities;
import de.timmi6790.server_management_module.ServerManagementModule;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
public class JoinLeaveLogger {
    private final ServerManagementModule module;

    private Optional<TextChannel> getLogChannel() {
        return Optional.ofNullable(this.module.getDiscord().getTextChannelById(this.module.getConfig().getJoinChannelId()));
    }

    @SubscribeEvent
    public void onMemberJoin(final GuildMemberJoinEvent event) {
        this.getLogChannel().ifPresent(channel ->
                DiscordMessagesUtilities.sendMessage(
                        channel,
                        DiscordMessagesUtilities.getEmbedBuilder(event.getUser(), event.getMember())
                                .setTitle("Join")
                                .appendDescription("%s joined", event.getUser().getAsMention())
                                .setTimestamp(LocalDateTime.now())
                ));
    }

    @SubscribeEvent
    public void onMemberLeave(final GuildMemberRemoveEvent event) {
        this.getLogChannel().ifPresent(channel ->
                DiscordMessagesUtilities.sendMessage(
                        channel,
                        DiscordMessagesUtilities.getEmbedBuilder(event.getUser(), event.getMember())
                                .setTitle("Leave")
                                .appendDescription("%s left", event.getUser().getAsMention())
                                .setTimestamp(LocalDateTime.now())
                ));
    }
}
