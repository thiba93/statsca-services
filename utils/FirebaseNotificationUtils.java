package com.carrus.statsca.utils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrus.statsca.admin.entity.FirebaseNotification;
import com.carrus.statsca.admin.entity.NotificationFcmToken;
import com.carrus.statsca.admin.entity.NotificationMetadata;
import com.carrus.statsca.dto.FireBaseNotificationDTO;
import com.carrus.statsca.dto.NotificationFcmTokenDTO;
import com.carrus.statsca.dto.NotificationMetadataDTO;
import com.pmc.club.entity.Race;
import com.pmc.club.event.BigPayOffChange;

/**
 * FirebaseNotificationUtils is a utility class that provides methods for
 * mapping
 * FirebaseNotification and related entities to DTOs, as well as handling JSON
 * conversion and building action data payloads.
 */
public class FirebaseNotificationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseNotificationUtils.class);
    private static Map<String, String> emojiMaps;

    private FirebaseNotificationUtils() {
        // empty private constructor
    }

    static {
    	initEmojiMap();
    }

    private static void initEmojiMap() {
        if (emojiMaps == null) {
            emojiMaps = new HashMap<>();
            emojiMaps.put("partyingFace", "\uD83E\uDD73");
            emojiMaps.put("partyPopper", "\uD83C\uDF89");
        }
    }

    /**
     * Maps a FirebaseNotification to a FireBaseNotificationDTO.
     *
     * @param notification The FirebaseNotification to be mapped.
     * @return The corresponding FireBaseNotificationDTO.
     */
    public static FireBaseNotificationDTO mapNotificationToDTO(FirebaseNotification notification) {
        FireBaseNotificationDTO dto = new FireBaseNotificationDTO();
        dto.setBody(notification.getBody());
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setPriority(notification.getPriority());
        dto.setTopics(notification.getTopics());
        dto.setType(notification.getType());
        dto.setData(FirebaseNotificationUtils.mapNotifMEtadataToMap(notification.getData()));
        dto.setNotificationFcmTokens(
                FirebaseNotificationUtils.mapNotifFcmTokenSetToDTOSet(notification.getNotificationFcmTokens()));
        return dto;
    }

    /**
     * Maps a set of FirebaseNotification objects to a set of
     * FireBaseNotificationDTO objects.
     *
     * @param set The set of FirebaseNotification to be mapped.
     * @return The corresponding set of FireBaseNotificationDTO.
     */
    public static Set<FireBaseNotificationDTO> mapNotificationSetToDTOSet(Set<FirebaseNotification> set) {
        Set<FireBaseNotificationDTO> result = new HashSet<>();
        if (set != null) {
            set.stream().map(FirebaseNotificationUtils::mapNotificationToDTO).forEach(result::add);
        }
        return result;
    }

    /**
     * Maps a NotificationMetadata entity to a NotificationMetadataDTO.
     *
     * @param metadata The NotificationMetadata entity to be mapped.
     * @return The corresponding NotificationMetadataDTO.
     */
    public static NotificationMetadataDTO mapMetaDataToDTO(NotificationMetadata metadata) {
        NotificationMetadataDTO dto = new NotificationMetadataDTO();
        dto.setId(metadata.getId());
        dto.setKey(metadata.getKey());
        dto.setValue(dto.getValue());
        return dto;
    }

    /**
     * Maps a set of NotificationFcmToken entities to a set of
     * NotificationFcmTokenDTO objects.
     *
     * @param set The set of NotificationFcmToken to be mapped.
     * @return The corresponding set of NotificationFcmTokenDTO.
     */
    public static Set<NotificationFcmTokenDTO> mapNotifFcmTokenSetToDTOSet(Set<NotificationFcmToken> set) {
        Set<NotificationFcmTokenDTO> result = new HashSet<>();
        if (set != null) {
            set.stream().map(t -> {
                NotificationFcmTokenDTO target = new NotificationFcmTokenDTO();
                target.setId(t.getId());
                target.setMaxSendingDate(t.getMaxSendingDate());
                target.setSeen(t.isSeen());
                target.setSendDate(t.getSendDate());
                target.setStatut(t.getStatut());
                return target;
            }).forEach(result::add);
        }
        return result;
    }

    /**
     * Maps a set of NotificationMetadata entities to a map of key-value pairs.
     *
     * @param metadataSet The set of NotificationMetadata to be mapped.
     * @return The corresponding map of key-value pairs.
     */
    public static Map<String, String> mapNotifMEtadataToMap(Set<NotificationMetadata> metadataSet) {
        if (metadataSet != null) {
            Map<String, String> result = new HashMap<>();
            metadataSet.forEach(md -> {
                if (md.getKey() != null && md.getValue() != null)
                    result.put(md.getKey(), md.getValue());
            });
            return result;
        }
        return new HashMap<>();
    }

    /**
     * Converts a map of key-value pairs to a JSON-formatted string.
     *
     * @param obj The map of key-value pairs to be converted.
     * @return The JSON-formatted string representation of the map.
     */
    public static String mapToJSonString(Map<String, String> obj) {
        if (obj == null || obj.isEmpty()) {
            return null;
        }
        String json = "{";
        json = json + obj.entrySet().stream().map(t -> {
            return "\"" + t.getKey() + "\"" + ":" + "\"" + t.getValue() + "\"";
        }).collect(Collectors.joining(","));
        json = json + "}";
        return json;
    }

    /**
     * Builds a JSON payload string for race-related actions.
     *
     * @param race The Race object for which the payload is built.
     * @return The JSON-formatted payload string.
     */
    public static String buildRaceNotifActionDataPayload(Race race) {
        Map<String, String> eventPayload = new HashMap<>();
        eventPayload.put("pk", String.valueOf(race.getPk()));
        eventPayload.put("shortName", race.getShortName());
        eventPayload.put("course", race.getCourse());
        eventPayload.put("name", race.getName());
        eventPayload.put("number", String.valueOf(race.getNumber()));
        eventPayload.put("site", String.valueOf(race.getEvent().getRaceTrack().getPk()));
        eventPayload.put("siteName", race.getEvent().getRaceTrack().getName());
        eventPayload.put("eventId", String.valueOf(race.getEvent().getId()));
        return mapToJSonString(eventPayload);
    }

    /**
     * Converts a technical message containing placeholders into a user-friendly
     * message
     * by replacing the placeholders with corresponding values from a message map.
     *
     * @param message            The technical message containing placeholders to be
     *                           replaced.
     * @param messageMap         A map containing key-value pairs for message
     *                           placeholders and their replacements.
     * @param userLanguagePrefix The prefix for keys in the message map based on
     *                           user's language (default is "fr.").
     * @return The user-friendly message with placeholders replaced by corresponding
     * values.
     */
    public static String convertTechnicalMessageToUserMessage(String message, Map<String, String> messageMap,
                                                              String userLanguagePrefix) {
        // Log a warning message to indicate message formatting
        LOGGER.debug("formating message: {}", message);
        // If userLanguagePrefix is null, set it to the default "fr."
        //initEmojiMap();
        if (userLanguagePrefix == null)
            userLanguagePrefix = "fr.";
        else
            userLanguagePrefix = userLanguagePrefix + ".";
        // Compile a regular expression pattern to match placeholders in the message
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(message);
        // Replace placeholders in the message with corresponding values from the
        // message map
        StringBuffer output = new StringBuffer();
        while (matcher.find()) {
            String extractedValue = matcher.group(1);
            String replacedValue = messageMap.get(userLanguagePrefix + extractedValue);
            matcher.appendReplacement(output, replacedValue);
        }
        matcher.appendTail(output);
        // compile a REGEX pattern to match emoji placeholder in the message
        Pattern patternEmoji = Pattern.compile("@emoji\\{([^}]+)\\}");
        // Replace emoji placeholders with corresponding values from the emoji map
        Matcher matcherEmoji = patternEmoji.matcher(output.toString());
        output = new StringBuffer();
        while (matcherEmoji.find()) {
            String extractedEmoji = matcherEmoji.group(1);
            String replacedEmoji = emojiMaps.get(extractedEmoji);
            matcherEmoji.appendReplacement(output, replacedEmoji != null ? replacedEmoji : matcherEmoji.group(0));
        }
        matcherEmoji.appendTail(output);
        // Return the user-friendly message with placeholders replaced
        return output.toString();
    }
    
    /**
     * Builds a JSON payload string for big payoff data.
     * @param bigPayOffChange
     * @return
     */
    public static String buildBigPayOffNotifDataPayload(BigPayOffChange bigPayOffChange) {
    	Map<String, String> bigPayoffPayload = new HashMap<>();
    	bigPayoffPayload.put("partner", bigPayOffChange.getPartner().getName());
    	bigPayoffPayload.put("payOff", formatCurrencyAmount(bigPayOffChange.getPayOff(), 0, 0)); //to format
    	bigPayoffPayload.put("initialAmount", formatCurrencyAmount(bigPayOffChange.getAmount(), 0, 1)); //to format
    	bigPayoffPayload.put("bet", bigPayOffChange.getBet().getLongName());
    	bigPayoffPayload.put("formulaType", bigPayOffChange.getWheel().name());
    	bigPayoffPayload.put("formulaRisk", bigPayOffChange.getRisk().name());
    	bigPayoffPayload.put("replacement", String.valueOf(false) );
    	bigPayoffPayload.put("box",  String.valueOf(false));
    	bigPayoffPayload.put("quickPick", String.valueOf(false));
        return mapToJSonString(bigPayoffPayload);
    }
    
    public static String formatCurrencyAmount(double currencyAmount, int min, int max) {
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
		currencyFormatter.setMaximumFractionDigits(max);
		currencyFormatter.setMinimumFractionDigits(min);	
		return currencyFormatter.format(currencyAmount);
    }	
}