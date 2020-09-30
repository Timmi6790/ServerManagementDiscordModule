package de.timmi6790.server_management_module.detection.detections;

import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.server_management_module.ServerManagementModule;
import de.timmi6790.server_management_module.detection.AbstractDetection;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@EqualsAndHashCode(callSuper = true)
public class IllegalCharacterDetection extends AbstractDetection {
    private final String[] illegalCharacters;

    public IllegalCharacterDetection(final ServerManagementModule module) {
        super(module);

        this.illegalCharacters = module.getConfig().getDetections().getIllegalCharacters();
    }

    @SubscribeEvent
    public void onChatMessage(final MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getMember() == null) {
            return;
        }

        final String contentRaw = event.getMessage().getContentRaw();
        for (final String illegalCharacter : this.illegalCharacters) {
            if (contentRaw.contains(illegalCharacter)) {
                this.mutePlayer(event.getGuild(), event.getMember(), String.format("Sending illegal character %s.", illegalCharacter));
                break;
            }
        }
    }
}
