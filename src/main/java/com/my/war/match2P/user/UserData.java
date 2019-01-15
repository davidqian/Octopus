package com.my.war.match2P.user;

import com.my.Octopus.dataconfig.DataConfigManager;
import com.my.war.match2P.config.CohRange;

public class UserData {
    private String uid;
    private String sec;
    private int score;
    private long addTime;
    private int index;
    private int status = 0;
    private int added = 0;
    private long useTime = 0;

    private UserData matched = null;

    public UserData() {
        this.addTime = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setUseTime(long time) {
        this.useTime = time - addTime;
    }

    public long getUseTime() {
        return useTime;
    }

    public int[] getScoreRange(long nowTime) {
        int diffSecs = (int) ((nowTime - addTime) / 1000);
        CohRange cohRange = DataConfigManager.getSettingById(CohRange.class, diffSecs);
        if (cohRange != null) {
            added = cohRange.range;
        }

        int min = score, max = score;
        min = min - added;
        if (min < 0) min = 0;
        max = max + added;

        int[] range = {min, max};
        return range;
    }

    public int getStatus() {
        return status;
    }

    public boolean setStatus(int status) {
        synchronized (uid) {
            if (status == 0 && this.status == 1) {
                this.status = status;
                return true;
            }
            if (this.status != 0) {
                return false;
            } else {
                this.status = status;
                return true;
            }
        }
    }

    public UserData getMatched() {
        return matched;
    }

    public void setMatched(UserData matched) {
        this.matched = matched;
    }
}