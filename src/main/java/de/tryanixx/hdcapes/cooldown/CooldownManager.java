package de.tryanixx.hdcapes.cooldown;

import java.util.Timer;
import java.util.TimerTask;

public class CooldownManager {

    private Timer timer;

    private boolean cooldown;
    private int remainingTime;

    public boolean isCooldown() {
        return cooldown;
    }

    public void startCooldown() {
        if(cooldown) {
            return;
        }
        remainingTime = 30;
        cooldown = true;
        if (timer == null) {
            timer = new Timer();
        }
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTime == 1) {
                    cooldown = false;
                    timer.cancel();
                    timer = null;
                }
                remainingTime--;
            }
        }, 0, 1000);
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}