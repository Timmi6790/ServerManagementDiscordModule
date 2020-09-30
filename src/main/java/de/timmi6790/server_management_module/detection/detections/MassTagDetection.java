package de.timmi6790.server_management_module.detection.detections;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.server_management_module.ServerManagementModule;
import de.timmi6790.server_management_module.detection.AbstractDetection;
import de.timmi6790.server_management_module.utilities.MessageUtilities;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
public class MassTagDetection extends AbstractDetection {
    private final int maxTagsPerMessage;
    private final int maxTagsPerMinute;

    private final LoadingCache<Long, Integer> userTagCountCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(k -> 0);

    public MassTagDetection(final ServerManagementModule module) {
        super(module);

        this.maxTagsPerMessage = module.getConfig().getDetections().getMaxTagsPerMessage();
        this.maxTagsPerMinute = module.getConfig().getDetections().getMaxTagsPerMinute();
    }

    @SubscribeEvent
    public void onChatMessage(final MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getMember() == null) {
            return;
        }

        final long userId = event.getMember().getIdLong();
        final String content = event.getMessage().getContentRaw();

        final List<Long> userIds = MessageUtilities.getTaggedUserIdsInMessage(content);
        userIds.removeIf(id -> id.equals(userId));

        final List<Long> roleIds = MessageUtilities.getTaggedRoleIdsInMessage(content);
        final int tagsInMessage = userIds.size() + roleIds.size();

        final int totalTags = this.userTagCountCache.get(userId) + tagsInMessage;
        this.userTagCountCache.put(userId, totalTags);

        if (tagsInMessage >= this.maxTagsPerMessage) {
            this.mutePlayer(event.getGuild(), event.getMember(), String.format("%s tags in one message", tagsInMessage));
        } else if (totalTags >= this.maxTagsPerMinute) {
            this.mutePlayer(event.getGuild(), event.getMember(), String.format("%s tags in one minute", totalTags));
        }
    }
}
