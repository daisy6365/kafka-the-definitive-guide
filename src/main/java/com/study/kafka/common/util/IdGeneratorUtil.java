package com.study.kafka.common.util;

import com.study.kafka.common.Snowflake;

public class IdGeneratorUtil {
    private static final Snowflake EVENT_ID_GEN = new Snowflake(1, 1);
    private static final Snowflake TRX_ID_GEN = new Snowflake(2, 1);

    public static String generateEventId() {
        return String.valueOf(EVENT_ID_GEN.nextId());
    }

    public static String generateTrxId() {
        return String.valueOf(TRX_ID_GEN.nextId());
    }
}