package com.lhxy.istationdevice.android11.protocol.jt808;

public final class Jt808TerminalProfile {
    private final String terminalId;
    private final String terminalModel;
    private final String vehicleId;
    private final String authorityHex;
    private final int provinceId;
    private final int cityId;
    private final int plateColor;

    public Jt808TerminalProfile(
            String terminalId,
            String terminalModel,
            String vehicleId,
            String authorityHex,
            int provinceId,
            int cityId,
            int plateColor
    ) {
        this.terminalId = terminalId;
        this.terminalModel = terminalModel;
        this.vehicleId = vehicleId;
        this.authorityHex = authorityHex;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.plateColor = plateColor;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getTerminalModel() {
        return terminalModel;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getAuthorityHex() {
        return authorityHex;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public int getCityId() {
        return cityId;
    }

    public int getPlateColor() {
        return plateColor;
    }
}
