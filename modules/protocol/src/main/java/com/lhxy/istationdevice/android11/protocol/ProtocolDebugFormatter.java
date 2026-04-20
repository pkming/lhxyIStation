package com.lhxy.istationdevice.android11.protocol;

import com.lhxy.istationdevice.android11.core.Hexs;

public final class ProtocolDebugFormatter {
    private ProtocolDebugFormatter() {
    }

    public static String describe(ProtocolEnvelope envelope) {
        if (envelope == null) {
            return "empty envelope";
        }
        return envelope.getProtocolName()
                + " via "
                + envelope.getChannelName()
                + " -> "
                + Hexs.toHex(envelope.getPayload())
                + "\n- "
                + ProtocolPayloadExplainer.compactExplain(envelope.getProtocolName(), envelope.getPayload());
    }
}

