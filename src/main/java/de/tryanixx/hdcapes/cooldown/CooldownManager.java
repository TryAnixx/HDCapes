package de.tryanixx.hdcapes.cooldown;

public class CooldownManager {

    public static final int TIME_DURATION = 30;

    public void update() {
        for (CooldownType type : CooldownType.VALS) {
            if(type.isActive()) {
                type.remove();
            }
        }
    }

    public enum CooldownType {
        UPLOAD, REFRESH, DELETE;

        public static final CooldownType[] VALS = values();

        private int secondsLeft;

        public boolean isActive() {
            return secondsLeft > 0;
        }

        public int getSecondsLeft() {
            return secondsLeft;
        }

        public void start() {
            secondsLeft = TIME_DURATION;
        }
        public void remove() {
            secondsLeft--;
        }
    }
}