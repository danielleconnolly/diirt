/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.util.Objects;
import org.epics.util.stats.Range;
import org.epics.util.stats.Ranges;

/**
 * Standard implementation for the logic to calculate the data range to
 * be displayed in a graph.
 * <p>
 * There are four cases:
 * <ul>
 *   <li><b>Display</b> (default): it’s the range that comes directly with the
 * data. If it’s not set, or if it’s invalid, then it’s calculated from
 * the data the first time and kept forever.</li>
 *   <li><b>Data</b>: it’s the range of the current data being displayed. To determine
 * the current range, all values may be scanned, and it can be costly if the dataset is large. If the
 * data changes in time, the range displayed will grow and shrink, which may
 * make the plot confusing.</li>
 *   <li><b>Fixed</b>: use defined minimum and maximum.</li>
 *   <li><b>Auto</b>: keeps growing the range so that incoming data always fits.
 * In most cases, as data comes in, the Auto range will become stable.
 * To deal with outliers you can specify the the minimum percentage of the range
 * that must be used to display data. So, if you set a 80% threshold, the
 * range will  shrinks if less than 80% of the range contains actual data.
 * This option may have the same performance issues than Data.</li>
 * </ul>
 *
 * @author carcassi
 */
public class AxisRanges {
    
    private AxisRanges() {
    }
    
    public static AxisRange fixed(final double min, final double max) {
        final Range fixedRange = Ranges.range(min, max);
        return new Fixed(fixedRange);
    }
    
    public static class Fixed implements AxisRange {
        
        private final AxisRange axisRange = this;
        private final Range absoluteRange;

        private Fixed(Range absoluteRange) {
            this.absoluteRange = absoluteRange;
        }

        @Override
        public AxisRangeInstance createInstance() {
            return new AxisRangeInstance() {

                @Override
                public Range axisRange(Range dataRange, Range displayRange) {
                        return absoluteRange;
                }

                @Override
                public AxisRange getAxisRange() {
                    return axisRange;
                }
            };
        }

        @Override
        public String toString() {
            return "fixed(" + absoluteRange.getMinimum() + ", " + absoluteRange.getMaximum() + ")";
        }

        public Range getAbsoluteRange() {
            return absoluteRange;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Fixed) {
                return Ranges.equals(getAbsoluteRange(), ((Fixed) obj).getAbsoluteRange());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.absoluteRange);
            return hash;
        }
        
    }
    
    public static AxisRange data() {
        return DATA;
    }
    
    private static Data DATA = new Data();
    
    public static class Data implements AxisRange {
        
        private final AxisRange axisRange = this;

        private Data() {
        }

        @Override
        public AxisRangeInstance createInstance() {
            return new AxisRangeInstance() {

                @Override
                public Range axisRange(Range dataRange, Range displayRange) {
                    return dataRange;
                }

                @Override
                public AxisRange getAxisRange() {
                    return axisRange;
                }
            };
        }

        @Override
        public String toString() {
            return "data";
        }
    }
    
    public static AxisRange auto() {
        return AUTO;
    }
    
    public static AxisRange auto(double minUsage) {
        return new Auto(minUsage);
    }
    
    private static final Auto AUTO = new Auto(0.8);
    
    public static class Auto implements AxisRange {

        private final AxisRange axisRange = this;
        private final double minUsage;

        private Auto(double minUsage) {
            this.minUsage = minUsage;
        }

        @Override
        public AxisRangeInstance createInstance() {
            return new AxisRangeInstance() {

                private Range aggregatedRange;

                @Override
                public Range axisRange(Range dataRange, Range displayRange) {
                    aggregatedRange = Ranges.aggregateRange(dataRange, aggregatedRange);
                    if (Ranges.overlap(aggregatedRange, dataRange) < minUsage) {
                        aggregatedRange = dataRange;
                    }
                    return aggregatedRange;
                }

                @Override
                public AxisRange getAxisRange() {
                    return axisRange;
                }
            };
        }

        @Override
        public String toString() {
            return "auto(" + (int) (minUsage * 100) + "%)";
        }

        public double getMinUsage() {
            return minUsage;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Auto) {
                return getMinUsage() == ((Auto) obj).getMinUsage();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.getMinUsage());
            return hash;
        }
        
    }
    
    public static AxisRange display() {
        return DISPLAY;
    }
    
    private static final Display DISPLAY = new Display();
    
    public static class Display implements AxisRange {
            
        private final AxisRange axisRange = this;

        private Display() {
        }

        @Override
        public AxisRangeInstance createInstance() {
            return new AxisRangeInstance() {
                
                private Range previousDataRange;

                @Override
                public Range axisRange(Range dataRange, Range displayRange) {
                    if (Ranges.isValid(displayRange)) {
                        return displayRange;
                    } else if (previousDataRange == null) {
                        previousDataRange = dataRange;
                        return previousDataRange;
                    } else {
                        return previousDataRange;
                    }
                }

                @Override
                public AxisRange getAxisRange() {
                    return axisRange;
                }
            };
        }

        @Override
        public String toString() {
            return "display";
        }
    }
}
