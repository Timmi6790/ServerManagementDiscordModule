package de.timmi6790.server_management_module.reaction_roles;

import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.server_management_module.ServerManagementModule;
import de.timmi6790.server_management_module.utilities.EmoteUtilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;

import java.util.Map;
import java.util.Optional;

public class ReactionRankListener {
    private final Map<Long, Map<String, Long>> reactionRoles;

    public ReactionRankListener(final ServerManagementModule module) {
        this.reactionRoles = module.getConfig().getReactionRoles();
    }

    private Optional<Role> getAffectedRankId(@NonNull final Guild guild, final long messageId, @NonNull final String emoteName) {
        if (!this.reactionRoles.containsKey(messageId)) {
            return Optional.empty();
        }

        final Long rankId = this.reactionRoles.get(messageId).getOrDefault(emoteName, null);
        if (rankId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(guild.getRoleById(rankId));
    }

    @SubscribeEvent
    public void onReactionAdd(final GuildMessageReactionAddEvent event) {
        // TODO: Add a cooldown system to prevent rate limit
        this.getAffectedRankId(event.getGuild(), event.getMessageIdLong(), EmoteUtilities.getEmoteName(event.getReactionEmote()))
                .ifPresent(rank -> event.getGuild().addRoleToMember(event.getUserIdLong(), rank).queue());
    }

    @SubscribeEvent
    public void onReactionRemove(final GuildMessageReactionRemoveEvent event) {
        this.getAffectedRankId(event.getGuild(), event.getMessageIdLong(), EmoteUtilities.getEmoteName(event.getReactionEmote()))
                .ifPresent(rank -> event.getGuild().removeRoleFromMember(event.getUserIdLong(), rank).queue());
    }
}
