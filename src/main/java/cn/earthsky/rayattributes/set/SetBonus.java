package cn.earthsky.rayattributes.set;

import cn.earthsky.rayattributes.attribute.AttributeType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SetBonus {

    private final String id;
    private final String name;
    private final Map<AttributeType, Double> twoPieceBonuses = new EnumMap<>(AttributeType.class);
    private String fourPieceTrigger;
    private final Map<String, Double> fourPieceEffects = new HashMap<>();

    public SetBonus(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addTwoPieceBonus(AttributeType type, double value) {
        twoPieceBonuses.put(type, value);
    }

    public void setFourPieceTrigger(String trigger) {
        this.fourPieceTrigger = trigger;
    }

    public void addFourPieceEffect(String key, double value) {
        fourPieceEffects.put(key, value);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Map<AttributeType, Double> getTwoPieceBonuses() { return twoPieceBonuses; }
    public String getFourPieceTrigger() { return fourPieceTrigger; }
    public Map<String, Double> getFourPieceEffects() { return fourPieceEffects; }
}
