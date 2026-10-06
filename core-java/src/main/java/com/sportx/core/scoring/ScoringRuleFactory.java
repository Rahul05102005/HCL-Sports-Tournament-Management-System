package com.sportx.core.scoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Factory Pattern for Sport-Specific Scoring Rule Resolution.
 * Demonstrates: Factory Pattern, Open/Closed Principle (OCP), Collections Map.
 */
public class ScoringRuleFactory {

    private static final Map<String, ScoringRule> RULES = new HashMap<>();

    static {
        registerRule(new FootballScoringRule());
        registerRule(new BasketballScoringRule());
        registerRule(new CricketScoringRule());
    }

    public static synchronized void registerRule(ScoringRule rule) {
        if (rule != null) {
            RULES.put(rule.getSportCode().toUpperCase(), rule);
        }
    }

    public static ScoringRule getRule(String sportCode) {
        if (sportCode == null) {
            return RULES.get("FB-11"); // default to football
        }
        return Optional.ofNullable(RULES.get(sportCode.toUpperCase()))
                .orElseGet(FootballScoringRule::new);
    }

    public static Map<String, ScoringRule> getRegisteredRules() {
        return Collections.unmodifiableMap(RULES);
    }
}
