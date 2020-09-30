package de.timmi6790.server_management_module.detection;

import de.timmi6790.server_management_module.ServerManagementModule;
import lombok.Data;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@Data
public abstract class AbstractDetection {
    private final ServerManagementModule module;

    public boolean mutePlayer(@NonNull final Guild guild, @NonNull final Member member, @NonNull final String punishmentReason) {
        return this.module.mutePlayer(guild, member, punishmentReason);
    }
}

