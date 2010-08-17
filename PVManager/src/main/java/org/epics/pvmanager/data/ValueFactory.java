/*
 * Copyright 2010 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */

package org.epics.pvmanager.data;

import org.epics.pvmanager.TimeStamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;

/**
 * Factory class for all concrete implementation of the types.
 * <p>
 * The factory methods do not do anything in terms of defensive copy and
 * immutability to the objects, which they are passed as they are. It's the
 * client responsibility to prepare them appropriately, which is automatically
 * done anyway for all objects except collections.
 *
 * @author carcassi
 */
public class ValueFactory {

    public static VMultiDouble newVMultiDouble(List<VDouble> values, AlarmSeverity alarmSeverity,
            Set<String> alarmStatus, List<String> possibleAlarms,
            TimeStamp timeStamp, Integer timeUserTag, Double lowerDisplayLimit,
            Double lowerCtrlLimit, Double lowerAlarmLimit, Double lowerWarningLimit,
            String units, NumberFormat format, Double upperWarningLimit, Double upperAlarmLimit,
            Double upperCtrlLimit, Double upperDisplayLimit) {
        return new IVMultiDouble(values, alarmSeverity, alarmStatus, possibleAlarms,
                timeStamp, timeUserTag, lowerDisplayLimit, lowerCtrlLimit, lowerAlarmLimit, lowerWarningLimit,
                units, format, upperWarningLimit, upperAlarmLimit, upperCtrlLimit, upperDisplayLimit);
    }

    /**
     * Creates new immutable VDouble.
     */
    public static VDouble newVDouble(final Double value, final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final List<String> possibleAlarms, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new VDouble() {

            @Override
            public Double getLowerCtrlLimit() {
                return lowerCtrlLimit;
            }

            @Override
            public Double getUpperCtrlLimit() {
                return upperCtrlLimit;
            }

            @Override
            public Double getLowerDisplayLimit() {
                return lowerDisplayLimit;
            }

            @Override
            public Double getLowerAlarmLimit() {
                return lowerAlarmLimit;
            }

            @Override
            public Double getLowerWarningLimit() {
                return lowerWarningLimit;
            }

            @Override
            public String getUnits() {
                return units;
            }

            @Override
            public NumberFormat getFormat() {
                return numberFormat;
            }

            @Override
            public Double getUpperWarningLimit() {
                return upperWarningLimit;
            }

            @Override
            public Double getUpperAlarmLimit() {
                return upperAlarmLimit;
            }

            @Override
            public Double getUpperDisplayLimit() {
                return upperDisplayLimit;
            }

            @Override
            public Integer getTimeUserTag() {
                return timeUserTag;
            }

            @Override
            public TimeStamp getTimeStamp() {
                return timeStamp;
            }

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public Set<String> getAlarmStatus() {
                return alarmStatus;
            }

            @Override
            public List<String> getPossibleAlarms() {
                return possibleAlarms;
            }

            @Override
            public Double getValue() {
                return value;
            }
        };
    }

    /**
     * Creates new immutable new VDouble by using the metadata from the old value.
     */
    public static VDouble newVDouble(final Double value, final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            VDouble oldValue) {
        return newVDouble(value, alarmSeverity, alarmStatus, oldValue.getPossibleAlarms(),
                timeStamp,
                timeUserTag,
                oldValue.getLowerDisplayLimit(), oldValue.getLowerAlarmLimit(),
                oldValue.getLowerWarningLimit(), oldValue.getUnits(),
                oldValue.getFormat(), oldValue.getUpperWarningLimit(),
                oldValue.getUpperAlarmLimit(), oldValue.getUpperDisplayLimit(),
                oldValue.getLowerCtrlLimit(), oldValue.getUpperCtrlLimit());
    }

    /**
     * Creates a new immutable VStatistics.
     */
    public static VStatistics newVStatistics(final double average, final double stdDev,
            final double min, final double max, final int nSamples,
            final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final List<String> possibleAlarms, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Double lowerDisplayLimit, final Double lowerAlarmLimit, final Double lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Double upperWarningLimit,
            final Double upperAlarmLimit, final Double upperDisplayLimit,
            final Double lowerCtrlLimit, final Double upperCtrlLimit) {
        return new VStatistics() {

            @Override
            public Double getAverage() {
                return average;
            }

            @Override
            public Double getStdDev() {
                return stdDev;
            }

            @Override
            public Double getMin() {
                return min;
            }

            @Override
            public Double getMax() {
                return max;
            }

            @Override
            public Integer getNSamples() {
                return nSamples;
            }

            @Override
            public Double getLowerCtrlLimit() {
                return lowerCtrlLimit;
            }

            @Override
            public Double getUpperCtrlLimit() {
                return upperCtrlLimit;
            }

            @Override
            public Double getLowerDisplayLimit() {
                return lowerDisplayLimit;
            }

            @Override
            public Double getLowerAlarmLimit() {
                return lowerAlarmLimit;
            }

            @Override
            public Double getLowerWarningLimit() {
                return lowerWarningLimit;
            }

            @Override
            public String getUnits() {
                return units;
            }

            @Override
            public NumberFormat getFormat() {
                return numberFormat;
            }

            @Override
            public Double getUpperWarningLimit() {
                return upperWarningLimit;
            }

            @Override
            public Double getUpperAlarmLimit() {
                return upperAlarmLimit;
            }

            @Override
            public Double getUpperDisplayLimit() {
                return upperDisplayLimit;
            }

            @Override
            public Integer getTimeUserTag() {
                return timeUserTag;
            }

            @Override
            public TimeStamp getTimeStamp() {
                return timeStamp;
            }

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public Set<String> getAlarmStatus() {
                return alarmStatus;
            }

            @Override
            public List<String> getPossibleAlarms() {
                return possibleAlarms;
            }
        };
    }

    /**
     * Creates a new VStatistics by taking the metadata from a VDouble.
     */
    public static VStatistics newVStatistics(final double average, final double stdDev,
            final double min, final double max, final int nSamples, final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            VDouble aValue) {
        return newVStatistics(average, stdDev, min, max, nSamples,
                alarmSeverity, alarmStatus, aValue.getPossibleAlarms(),
                timeStamp,
                timeUserTag,
                aValue.getLowerDisplayLimit(), aValue.getLowerAlarmLimit(),
                aValue.getLowerWarningLimit(), aValue.getUnits(),
                aValue.getFormat(), aValue.getUpperWarningLimit(),
                aValue.getUpperAlarmLimit(), aValue.getUpperDisplayLimit(),
                aValue.getLowerCtrlLimit(), aValue.getUpperCtrlLimit());
    }

    /**
     * Creates new immutable VInt.
     */
    public static VInt newEInt(final Integer value, final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final List<String> possibleAlarms, final TimeStamp timeStamp,
            final Integer timeUserTag,
            final Integer lowerDisplayLimit, final Integer lowerAlarmLimit, final Integer lowerWarningLimit,
            final String units, final NumberFormat numberFormat, final Integer upperWarningLimit,
            final Integer upperAlarmLimit, final Integer upperDisplayLimit,
            final Integer lowerCtrlLimit, final Integer upperCtrlLimit) {
        return new VInt() {

            @Override
            public Integer getLowerCtrlLimit() {
                return lowerCtrlLimit;
            }

            @Override
            public Integer getUpperCtrlLimit() {
                return upperCtrlLimit;
            }

            @Override
            public Integer getLowerDisplayLimit() {
                return lowerDisplayLimit;
            }

            @Override
            public Integer getLowerAlarmLimit() {
                return lowerAlarmLimit;
            }

            @Override
            public Integer getLowerWarningLimit() {
                return lowerWarningLimit;
            }

            @Override
            public String getUnits() {
                return units;
            }

            @Override
            public NumberFormat getFormat() {
                return numberFormat;
            }

            @Override
            public Integer getUpperWarningLimit() {
                return upperWarningLimit;
            }

            @Override
            public Integer getUpperAlarmLimit() {
                return upperAlarmLimit;
            }

            @Override
            public Integer getUpperDisplayLimit() {
                return upperDisplayLimit;
            }

            @Override
            public Integer getTimeUserTag() {
                return timeUserTag;
            }

            @Override
            public TimeStamp getTimeStamp() {
                return timeStamp;
            }

            @Override
            public AlarmSeverity getAlarmSeverity() {
                return alarmSeverity;
            }

            @Override
            public Set<String> getAlarmStatus() {
                return alarmStatus;
            }

            @Override
            public List<String> getPossibleAlarms() {
                return possibleAlarms;
            }

            @Override
            public Integer getValue() {
                return value;
            }
        };
    }

    /**
     * Creates new immutable newDbrCtrlInt by using the metadata from the old value.
     */
    public static VInt newEInt(final Integer value, final AlarmSeverity alarmSeverity,
            final Set<String> alarmStatus, final Integer timeUserTag, final TimeStamp timeStamp,
            VInt oldValue) {
        return newEInt(value, alarmSeverity, alarmStatus, oldValue.getPossibleAlarms(),
                timeStamp,
                timeUserTag,
                oldValue.getLowerDisplayLimit(), oldValue.getLowerAlarmLimit(),
                oldValue.getLowerWarningLimit(), oldValue.getUnits(),
                oldValue.getFormat(), oldValue.getUpperWarningLimit(),
                oldValue.getUpperAlarmLimit(), oldValue.getUpperDisplayLimit(),
                oldValue.getLowerCtrlLimit(), oldValue.getUpperCtrlLimit());
    }

}
