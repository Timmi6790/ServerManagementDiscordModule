package de.timmi6790.server_management_module.utilities;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class MessageUtilities {
    private final Pattern ROLE_MENTION = Pattern.compile("<@&(\\d{17,20})>");
    private final Pattern USER_MENTION = Pattern.compile("<@!?(\\d{17,20})>");

    private List<Long> getRegexMatches(final Matcher matcher) {
        final List<Long> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(Long.parseLong(matcher.group(1)));
        }
        return matches;
    }

    public List<Long> getTaggedRoleIdsInMessage(final String contentRaw) {
        final Matcher rolesTagMatcher = ROLE_MENTION.matcher(contentRaw);
        return getRegexMatches(rolesTagMatcher);
    }

    public List<Long> getTaggedUserIdsInMessage(final String contentRaw) {
        final Matcher userTagMatcher = USER_MENTION.matcher(contentRaw);
        return getRegexMatches(userTagMatcher);
    }
}
