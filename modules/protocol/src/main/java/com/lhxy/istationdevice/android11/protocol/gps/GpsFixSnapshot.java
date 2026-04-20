package com.lhxy.istationdevice.android11.protocol.gps;

/**
 * GPS 纯解析快照
 * <p>
 * 这里先把旧项目里真正会用到的字段收口下来，后面真机串口接入时直接复用。
 */
public final class GpsFixSnapshot {
    private final String sourceSentence;
    private final boolean valid;
    private final String time;
    private final String date;
    private final String latitudeRaw;
    private final String latitudeHemisphere;
    private final String latitudeDecimal;
    private final String longitudeRaw;
    private final String longitudeHemisphere;
    private final String longitudeDecimal;
    private final String speedKnots;
    private final String course;
    private final String altitudeMeters;
    private final int usedSatellites;

    public GpsFixSnapshot(
            String sourceSentence,
            boolean valid,
            String time,
            String date,
            String latitudeRaw,
            String latitudeHemisphere,
            String latitudeDecimal,
            String longitudeRaw,
            String longitudeHemisphere,
            String longitudeDecimal,
            String speedKnots,
            String course,
            String altitudeMeters,
            int usedSatellites
    ) {
        this.sourceSentence = sourceSentence;
        this.valid = valid;
        this.time = time;
        this.date = date;
        this.latitudeRaw = latitudeRaw;
        this.latitudeHemisphere = latitudeHemisphere;
        this.latitudeDecimal = latitudeDecimal;
        this.longitudeRaw = longitudeRaw;
        this.longitudeHemisphere = longitudeHemisphere;
        this.longitudeDecimal = longitudeDecimal;
        this.speedKnots = speedKnots;
        this.course = course;
        this.altitudeMeters = altitudeMeters;
        this.usedSatellites = usedSatellites;
    }

    public String getSourceSentence() {
        return sourceSentence;
    }

    public boolean isValid() {
        return valid;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getLatitudeRaw() {
        return latitudeRaw;
    }

    public String getLatitudeHemisphere() {
        return latitudeHemisphere;
    }

    public String getLatitudeDecimal() {
        return latitudeDecimal;
    }

    public String getLongitudeRaw() {
        return longitudeRaw;
    }

    public String getLongitudeHemisphere() {
        return longitudeHemisphere;
    }

    public String getLongitudeDecimal() {
        return longitudeDecimal;
    }

    public String getSpeedKnots() {
        return speedKnots;
    }

    public String getCourse() {
        return course;
    }

    public String getAltitudeMeters() {
        return altitudeMeters;
    }

    public int getUsedSatellites() {
        return usedSatellites;
    }

    /**
     * 用于调试页和日志的简要文本。
     */
    public String describe() {
        return "GPS:\n- valid=" + valid
                + "\n- time=" + valueOrDash(time)
                + "\n- date=" + valueOrDash(date)
                + "\n- latitude=" + valueOrDash(latitudeDecimal) + " (" + valueOrDash(latitudeRaw) + " " + valueOrDash(latitudeHemisphere) + ")"
                + "\n- longitude=" + valueOrDash(longitudeDecimal) + " (" + valueOrDash(longitudeRaw) + " " + valueOrDash(longitudeHemisphere) + ")"
                + "\n- speedKnots=" + valueOrDash(speedKnots)
                + "\n- course=" + valueOrDash(course)
                + "\n- altitudeMeters=" + valueOrDash(altitudeMeters)
                + "\n- usedSatellites=" + usedSatellites;
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
