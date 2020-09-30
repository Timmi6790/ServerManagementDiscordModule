package de.timmi6790.server_management_module;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Config {
    private long logChannelId = -1;
    private long joinChannelId = -1;
    private long muteRoleId = -1;
    private long punishmentChannelId = -1;

    private Detections detections = new Detections();
    private Map<Long, Map<String, Long>> reactionRoles = new HashMap<>();

    @Data
    public static class Detections {
        private String[] illegalCharacters = {"卐"};
        private int maxTagsPerMessage = 6;
        private int maxTagsPerMinute = 10;
        private SpamDetection spamDetection = new SpamDetection();

        @Data
        public static class SpamDetection {
            private String messageCleanupPattern = "[,\\.!\\? ]";
            private int messageKeepCount = 10;
            private int muteAfter = 5;
        }
    }
}
