package de.timmi6790.server_management_module;

import de.timmi6790.discord_framework.modules.AbstractModule;
import de.timmi6790.discord_framework.modules.command.CommandModule;
import de.timmi6790.discord_framework.modules.config.ConfigModule;
import de.timmi6790.discord_framework.modules.database.DatabaseModule;
import de.timmi6790.discord_framework.modules.event.EventModule;
import de.timmi6790.discord_framework.utilities.discord.DiscordMessagesUtilities;
import de.timmi6790.server_management_module.commands.MimicEmoteAddCommand;
import de.timmi6790.server_management_module.commands.PurgeCommand;
import de.timmi6790.server_management_module.detection.DetectionManager;
import de.timmi6790.server_management_module.detection.detections.IllegalCharacterDetection;
import de.timmi6790.server_management_module.detection.detections.MassTagDetection;
import de.timmi6790.server_management_module.detection.detections.SpamDetection;
import de.timmi6790.server_management_module.logger.JoinLeaveLogger;
import de.timmi6790.server_management_module.reaction_roles.ReactionRankListener;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MarkdownUtil;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ServerManagementModule extends AbstractModule {
    private DetectionManager detectionManager;

    public ServerManagementModule() {
        super("ServerManagement");

        this.addDependenciesAndLoadAfter(
                CommandModule.class,
                DatabaseModule.class,
                EventModule.class,
                ConfigModule.class
        );

        this.addGatewayIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.GUILD_BANS
        );
    }

    private Config config;

    @Override
    public void onInitialize() {
        this.config = this.getModuleOrThrow(ConfigModule.class).registerAndGetConfig(this, new Config());

        this.detectionManager = new DetectionManager(this.getModuleOrThrow(EventModule.class));
        this.detectionManager.addDetections(
                new MassTagDetection(this),
                new IllegalCharacterDetection(this),
                new SpamDetection(this)
        );

        final MimicEmoteAddCommand mimicEmoteAddCommand = new MimicEmoteAddCommand();
        this.getModuleOrThrow(CommandModule.class).registerCommands(
                this,
                new PurgeCommand(),
                mimicEmoteAddCommand
        );

        this.getModuleOrThrow(EventModule.class).addEventListeners(
                new ReactionRankListener(this),
                new JoinLeaveLogger(this),
                mimicEmoteAddCommand
        );
    }

    public boolean mutePlayer(@NonNull final Guild guild, @NonNull final Member member, @NonNull final String punishmentReason) {
        if (this.config.getMuteRoleId() == -1) {
            return false;
        }

        final Role role = this.getDiscord().getRoleById(this.config.getMuteRoleId());
        if (role == null) {
            return false;
        }

        guild.addRoleToMember(member, role).queue();
        this.addPunishmentLog(member, "Mute", punishmentReason);
        return true;
    }

    public void addPunishmentLog(@NonNull final Member member, final String punishmentType, final String reason) {
        if (this.config.getPunishmentChannelId() == -1) {
            return;
        }

        final TextChannel logChannel = this.getDiscord().getTextChannelById(this.config.getPunishmentChannelId());
        if (logChannel == null) {
            return;
        }

        DiscordMessagesUtilities.sendMessage(
                logChannel,
                DiscordMessagesUtilities.getEmbedBuilder(member.getUser(), member)
                        .setTitle(punishmentType)
                        .appendDescription(
                                "%s got punished for %s",
                                member.getUser().getAsMention(),
                                MarkdownUtil.monospace(reason)
                        )
        );
    }
}
