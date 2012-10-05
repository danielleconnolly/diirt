/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.data;

import java.util.List;

import org.epics.pvmanager.ExpressionTester;
import org.epics.util.time.TimeDuration;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ValueFactory.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.epics.util.time.Timestamp;

/**
 *
 * @author carcassi
 */
public class TimedCacheCollectorTest {

    public TimedCacheCollectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Force type support loading
        DataTypeSupport.install();
    }

//    @Test
//    public void correctNumberOfValuesInCache() throws InterruptedException {
//        ExpressionTester exp = new ExpressionTester(timedCacheOf(channel("x"), TimeDuration.ofMillis(100)));
//        
//        Timestamp reference = Timestamp.now();
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(0)))));
//        assertThat(((List) exp.getValue()).size(), equalTo(1));
//
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(10)))));
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(20)))));
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(30)))));
//        assertThat(((List) exp.getValue()).size(), equalTo(4));
//
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(40)))));
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(50)))));
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(60)))));
//        assertThat(((List) exp.getValue()).size(), equalTo(7));
//
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(115)))));
//        assertThat(((List) exp.getValue()).size(), equalTo(6));
//
//        exp.writeValue("x", newVDouble(0.0, newTime(reference.plus(TimeDuration.ofMillis(155)))));
//        assertThat(((List) exp.getValue()).size(), equalTo(3));
//    }

}