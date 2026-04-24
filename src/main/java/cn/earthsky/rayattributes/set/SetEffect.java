package cn.earthsky.rayattributes.set;

public class SetEffect {

    private long lastTriggerTime;
    private double buffValue;
    private long buffExpireTime;
    private double shieldAmount;

    public boolean canTrigger(long cooldownTicks) {
        long now = System.currentTimeMillis();
        return now - lastTriggerTime >= cooldownTicks * 50;
    }

    public void markTriggered() {
        this.lastTriggerTime = System.currentTimeMillis();
    }

    public void setBuff(double value, long durationTicks) {
        this.buffValue = value;
        this.buffExpireTime = System.currentTimeMillis() + durationTicks * 50;
    }

    public double getActiveBuff() {
        if (System.currentTimeMillis() < buffExpireTime) {
            return buffValue;
        }
        return 0;
    }

    public double getShieldAmount() { return shieldAmount; }
    public void setShieldAmount(double amount) { this.shieldAmount = Math.max(0, amount); }
}
