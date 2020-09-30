package de.timmi6790.server_management_module.utilities;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.MessageReaction;

@UtilityClass
public class EmoteUtilities {
    public String getEmoteName(final MessageReaction.ReactionEmote emote) {
        return emote.isEmoji() ? emote.getAsCodepoints() : emote.getAsReactionCode();
    }
}
