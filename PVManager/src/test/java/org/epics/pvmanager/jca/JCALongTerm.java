/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager.jca;

import java.util.ArrayList;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderListener;
import static org.epics.pvmanager.ExpressionLanguage.*;
import static org.epics.pvmanager.data.ExpressionLanguage.*;
import static org.epics.util.time.TimeDuration.*;

/**
 *
 * @author carcassi
 */
public class JCALongTerm {
    public static void main(String[] args) throws Exception {
        JCADataSource jca = new JCADataSource(JCALibrary.CHANNEL_ACCESS_JAVA, Monitor.VALUE | Monitor.ALARM);
        PVManager.setDefaultDataSource(jca);
        
        List<String> names = new ArrayList<String>();
        for (int i = 0; i <= 20; i++) {
            names.add("counter" + i);
            names.add("counter" + i);
        }
        List<PVReader<?>> pvs = new ArrayList<PVReader<?>>(); 
        for (String name : names) {
            pvs.add(null);
        }
        Random rand = new Random(1);
        final AtomicInteger count = new AtomicInteger(-1);
        
        while (true) {
            int index = rand.nextInt(names.size());
            PVReader<?> pv = pvs.get(index);
            if (pv == null) {
                pv = PVManager.read(channel(names.get(index))).maxRate(ofHertz(rand.nextInt(20) + 1));
                pv.addPVReaderListener(new PVReaderListener() {

                    @Override
                    public void pvChanged() {
                        int value = count.incrementAndGet();
                        if (value % 1000 == 0) {
                            System.out.println(System.currentTimeMillis());
                        }
                    }
                });
                pvs.set(index, pv);
            } else {
                pv.close();
                pvs.set(index, null);
            }
            
            Thread.sleep(1000);
        }
    }
}
