package de.tryanixx.hdcapes.cooldown;

import java.util.Timer;
import java.util.TimerTask;

public class CooldownManager {

    private Timer timerUpload;
    private Timer timerDelete;
    private Timer timerRefresh;

    private boolean cooldownUpload;
    private int remainingTimeUpload;
    private boolean cooldownDelete;
    private int remainingTimeDelete;
    private boolean cooldownRefresh;
    private int remainingTimeRefresh;

    public boolean isCooldownUpload() {
        return cooldownUpload;
    }

    public void startCooldownUpload() {
        if (cooldownUpload) {
            return;
        }
        remainingTimeUpload = 30;
        cooldownUpload = true;
        if (timerUpload == null) {
            timerUpload = new Timer();
        }
        timerUpload.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTimeUpload == 1) {
                    cooldownUpload = false;
                    timerUpload.cancel();
                    timerUpload = null;
                }
                remainingTimeUpload--;
            }
        }, 0, 1000);
    }

    public int getRemainingTimeUpload() {
        return remainingTimeUpload;
    }

    public int getRemainingTimeDelete() {
        return remainingTimeDelete;
    }

    public boolean isCooldownDelete() {
        return cooldownDelete;
    }

    public void startCooldownDelete() {
        if (cooldownDelete) {
            return;
        }
        remainingTimeDelete = 30;
        cooldownDelete = true;
        if (timerDelete == null) {
            timerDelete = new Timer();
        }
        timerDelete.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTimeDelete == 1) {
                    cooldownDelete = false;
                    timerDelete.cancel();
                    timerDelete = null;
                }
                remainingTimeDelete--;
            }
        }, 0, 1000);
    }

    public boolean isCooldownRefresh() {
        return cooldownRefresh;
    }

    public void startCooldownRefresh() {
        if(cooldownRefresh) {
            return;
        }
        remainingTimeRefresh = 30;
        cooldownRefresh = true;
        if (timerRefresh == null) {
            timerRefresh = new Timer();
        }
        timerRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (remainingTimeRefresh == 1) {
                    cooldownRefresh = false;
                    timerRefresh.cancel();
                    timerRefresh = null;
                }
                remainingTimeRefresh--;
            }
        }, 0, 1000);
    }

    public int getRemainingTimeRefresh() {
        return remainingTimeRefresh;
    }

}