package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShotResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private double x;
    private double y;
    private double r;

    private boolean hit;

    private LocalDateTime checkTime;

    private long execTimeNs;

    private String sessionId;

    private static final DateTimeFormatter CHECK_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ShotResult() {}

    public ShotResult(double x, double y, double r, boolean hit,
                      LocalDateTime checkTime, long execTimeNs, String sessionId) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.checkTime = checkTime;
        this.execTimeNs = execTimeNs;
        this.sessionId = sessionId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public LocalDateTime getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(LocalDateTime checkTime) {
        this.checkTime = checkTime;
    }

    public long getExecTimeNs() {
        return execTimeNs;
    }

    public void setExecTimeNs(long execTimeNs) {
        this.execTimeNs = execTimeNs;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCheckTimeFormatted() {
        return checkTime == null ? "" : checkTime.format(CHECK_TIME_FORMAT);
    }
}