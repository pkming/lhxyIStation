package org.apache.tools.ant.taskdefs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.apache.tools.ant.types.EnumeratedAttribute;

/* JADX INFO: loaded from: classes3.dex */
public class WaitFor extends ConditionBase {
    public static final long DEFAULT_CHECK_MILLIS = 500;
    public static final long DEFAULT_MAX_WAIT_MILLIS = 180000;
    public static final long ONE_DAY = 86400000;
    public static final long ONE_HOUR = 3600000;
    public static final long ONE_MILLISECOND = 1;
    public static final long ONE_MINUTE = 60000;
    public static final long ONE_SECOND = 1000;
    public static final long ONE_WEEK = 604800000;
    private long checkEvery;
    private long checkEveryMultiplier;
    private long maxWait;
    private long maxWaitMultiplier;
    private String timeoutProperty;

    public WaitFor() {
        super("waitfor");
        this.maxWait = DEFAULT_MAX_WAIT_MILLIS;
        this.maxWaitMultiplier = 1L;
        this.checkEvery = 500L;
        this.checkEveryMultiplier = 1L;
    }

    public WaitFor(String str) {
        super(str);
        this.maxWait = DEFAULT_MAX_WAIT_MILLIS;
        this.maxWaitMultiplier = 1L;
        this.checkEvery = 500L;
        this.checkEveryMultiplier = 1L;
    }

    public void setMaxWait(long j) {
        this.maxWait = j;
    }

    public void setMaxWaitUnit(Unit unit) {
        this.maxWaitMultiplier = unit.getMultiplier();
    }

    public void setCheckEvery(long j) {
        this.checkEvery = j;
    }

    public void setCheckEveryUnit(Unit unit) {
        this.checkEveryMultiplier = unit.getMultiplier();
    }

    public void setTimeoutProperty(String str) {
        this.timeoutProperty = str;
    }

    public void execute() throws BuildException {
        long jCalculateCheckEveryMillis;
        long jCurrentTimeMillis;
        if (countConditions() > 1) {
            throw new BuildException("You must not nest more than one condition into " + getTaskName());
        }
        if (countConditions() < 1) {
            throw new BuildException("You must nest a condition into " + getTaskName());
        }
        Condition condition = (Condition) getConditions().nextElement();
        try {
            long jCalculateMaxWaitMillis = calculateMaxWaitMillis();
            jCalculateCheckEveryMillis = calculateCheckEveryMillis();
            jCurrentTimeMillis = System.currentTimeMillis() + jCalculateMaxWaitMillis;
        } catch (InterruptedException unused) {
            log("Task " + getTaskName() + " interrupted, treating as timed out.");
        }
        while (System.currentTimeMillis() < jCurrentTimeMillis) {
            if (condition.eval()) {
                processSuccess();
                return;
            }
            Thread.sleep(jCalculateCheckEveryMillis);
            processTimeout();
        }
        processTimeout();
    }

    public long calculateCheckEveryMillis() {
        return this.checkEvery * this.checkEveryMultiplier;
    }

    public long calculateMaxWaitMillis() {
        return this.maxWait * this.maxWaitMultiplier;
    }

    protected void processSuccess() {
        log(getTaskName() + ": condition was met", 3);
    }

    protected void processTimeout() {
        log(getTaskName() + ": timeout", 3);
        if (this.timeoutProperty != null) {
            getProject().setNewProperty(this.timeoutProperty, "true");
        }
    }

    public static class Unit extends EnumeratedAttribute {
        private Map timeTable;
        public static final String MILLISECOND = "millisecond";
        public static final String SECOND = "second";
        public static final String MINUTE = "minute";
        public static final String HOUR = "hour";
        public static final String DAY = "day";
        public static final String WEEK = "week";
        private static final String[] UNITS = {MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK};

        public Unit() {
            HashMap map = new HashMap();
            this.timeTable = map;
            map.put(MILLISECOND, new Long(1L));
            this.timeTable.put(SECOND, new Long(1000L));
            this.timeTable.put(MINUTE, new Long(60000L));
            this.timeTable.put(HOUR, new Long(3600000L));
            this.timeTable.put(DAY, new Long(86400000L));
            this.timeTable.put(WEEK, new Long(604800000L));
        }

        public long getMultiplier() {
            return ((Long) this.timeTable.get(getValue().toLowerCase(Locale.ENGLISH))).longValue();
        }

        @Override // org.apache.tools.ant.types.EnumeratedAttribute
        public String[] getValues() {
            return UNITS;
        }
    }
}
