package cn.earthsky.rayattributes.attribute;

import java.util.EnumMap;
import java.util.Map;

public class PlayerAttributes {

    private final Map<AttributeType, Double> flatValues = new EnumMap<>(AttributeType.class);
    private final Map<AttributeType, Double> percentValues = new EnumMap<>(AttributeType.class);
    private final Map<AttributeType, Double> finalValues = new EnumMap<>(AttributeType.class);

    public void reset() {
        flatValues.clear();
        percentValues.clear();
        finalValues.clear();
    }

    public void addFlat(AttributeType type, double value) {
        flatValues.merge(type, value, Double::sum);
    }

    public void addPercent(AttributeType type, double value) {
        percentValues.merge(type, value, Double::sum);
    }

    public void setFinal(AttributeType type, double value) {
        finalValues.put(type, value);
    }

    public double getFlat(AttributeType type) {
        return flatValues.getOrDefault(type, 0.0);
    }

    public double getPercent(AttributeType type) {
        return percentValues.getOrDefault(type, 0.0);
    }

    public double getFinal(AttributeType type) {
        return finalValues.getOrDefault(type, 0.0);
    }

    public Map<AttributeType, Double> getFinalValues() {
        return finalValues;
    }
}
