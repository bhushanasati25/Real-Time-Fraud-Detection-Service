package com.fraud.engine.rules;

import com.fraud.common.dto.FraudResult;
import com.fraud.common.dto.TransactionEvent;
import com.fraud.engine.entity.UserProfile;
import com.fraud.engine.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Location Anomaly Rule - Flags transactions from unusual locations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LocationAnomalyRule implements Rule {

    private final UserProfileRepository userProfileRepository;

    @Override
    public String getRuleId() {
        return "RULE_005";
    }

    @Override
    public String getRuleName() {
        return "Location Anomaly Check";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public FraudResult.RuleEvaluation evaluate(TransactionEvent event, FraudResult.FraudResultBuilder resultBuilder) {
        String userId = event.getUserId();
        String currentLocation = event.getLocation();
        String currentIp = event.getIpAddress();
        boolean triggered = false;
        BigDecimal score = BigDecimal.ZERO;
        StringBuilder messageBuilder = new StringBuilder();

        if (userId == null || userId.isBlank()) {
            return buildEvaluation(false, BigDecimal.ZERO, "User ID not provided");
        }

        try {
            Optional<UserProfile> profileOpt = userProfileRepository.findByUserId(userId);

            if (profileOpt.isEmpty()) {
                // New user - no baseline to compare
                messageBuilder.append("New user - establishing baseline location");
                log.debug("Rule {}: New user {}, no baseline for location comparison", getRuleId(), userId);
            } else {
                UserProfile profile = profileOpt.get();

                // Check IP address change
                if (currentIp != null && profile.getLastKnownIp() != null) {
                    if (!currentIp.equals(profile.getLastKnownIp())) {
                        triggered = true;
                        score = new BigDecimal("0.3");
                        messageBuilder.append(String.format("IP address changed: %s -> %s. ",
                                profile.getLastKnownIp(), currentIp));
                        log.info("Rule {} triggered: IP change for user {}", getRuleId(), userId);
                    }
                }

                // Check location change
                if (currentLocation != null && profile.getLastKnownLocation() != null) {
                    if (!locationMatch(currentLocation, profile.getLastKnownLocation())) {
                        triggered = true;
                        score = score.max(new BigDecimal("0.4"));
                        messageBuilder.append(String.format("Location changed: %s -> %s. ",
                                profile.getLastKnownLocation(), currentLocation));
                        log.info("Rule {} triggered: Location change for user {}", getRuleId(), userId);
                    }
                }

                if (!triggered) {
                    messageBuilder.append("Location consistent with user profile");
                }
            }

        } catch (Exception e) {
            log.error("Error evaluating location rule for user {}: {}", userId, e.getMessage());
            messageBuilder.append("Unable to evaluate location - proceeding with caution");
        }

        return buildEvaluation(triggered, score, messageBuilder.toString().trim());
    }

    private boolean locationMatch(String location1, String location2) {
        if (location1 == null || location2 == null) {
            return true; // Can't compare
        }
        // Simple case-insensitive comparison
        // In production, you'd use more sophisticated geo-matching
        return location1.equalsIgnoreCase(location2);
    }

    private FraudResult.RuleEvaluation buildEvaluation(boolean triggered, BigDecimal score, String message) {
        return FraudResult.RuleEvaluation.builder()
                .ruleId(getRuleId())
                .ruleName(getRuleName())
                .triggered(triggered)
                .score(score)
                .message(message)
                .build();
    }
}
