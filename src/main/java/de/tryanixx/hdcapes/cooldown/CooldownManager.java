package de.tryanixx.hdcapes.cooldown;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CooldownManager {

    private boolean cooldown;

    private int remainingTime;

    public boolean isCooldown() {
        return cooldown;
    }
    public void startCooldown() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        cooldown = true;
        remainingTime = 30;
        executorService.scheduleAtFixedRate(() -> {
            if(remainingTime == 1) {
                cooldown = false;
                executorService.shutdown();
                return;
            }
            remainingTime--;
        }, 0, 1, TimeUnit.SECONDS);
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
