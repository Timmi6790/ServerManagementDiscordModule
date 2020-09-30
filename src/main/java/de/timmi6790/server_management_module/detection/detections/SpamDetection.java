package de.timmi6790.server_management_module.detection.detections;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.EvictingQueue;
import de.timmi6790.discord_framework.modules.event.SubscribeEvent;
import de.timmi6790.server_management_module.Config;
import de.timmi6790.server_management_module.ServerManagementModule;
import de.timmi6790.server_management_module.detection.AbstractDetection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@EqualsAndHashCode(callSuper = true)
public class SpamDetection extends AbstractDetection {
    private final Pattern cleanupPattern;

    private final int messageKeepCount;
    private final int muteAfter;

    private final LoadingCache<Long, MessageHistory> userTagCountCache = Caffeine.newBuilder()
            .expireAfterAccess(6, TimeUnit.MINUTES)
            .build(MessageHistory::new);

    public SpamDetection(final ServerManagementModule module) {
        super(module);

        final Config.Detections.SpamDetection config = module.getConfig().getDetections().getSpamDetection();
        this.cleanupPattern = Pattern.compile(config.getMessageCleanupPattern());
        this.messageKeepCount = config.getMessageKeepCount();
        this.muteAfter = config.getMuteAfter();
    }

    private String getCleanedString(final String rawString) {
        return this.cleanupPattern.matcher(rawString).replaceAll("");
    }

    @SubscribeEvent
    public void onChatMessage(final MessageReceivedEvent event) {
        if (!event.isFromGuild() || event.getMember() == null) {
            return;
        }

        final String contentRaw = event.getMessage().getContentRaw();
        final MessageHistory messageHistory = this.userTagCountCache.get(event.getMember().getIdLong());
        messageHistory.getHistory().add(contentRaw);

        final String cleanString = this.getCleanedString(contentRaw);
        int count = 0;
        for (final String message : messageHistory.getHistory()) {
            if (this.getCleanedString(message).equalsIgnoreCase(cleanString)) {
                count++;
            }
        }

        if (count >= this.muteAfter) {
            this.mutePlayer(
                    event.getGuild(),
                    event.getMember(),
                    String.format("Sending the same message %s times over the last %s messages. [%s]", count, this.messageKeepCount, contentRaw)
            );
        }
    }

    @Data
    public class MessageHistory {
        private final long discordUserId;
        @SuppressWarnings("UnstableApiUsage")
        private final Queue<String> history = EvictingQueue.create(SpamDetection.this.messageKeepCount);
    }
}
