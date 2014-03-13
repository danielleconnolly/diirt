/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.epics.util.array.ArrayDouble;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * @author carcassi
 */
public class BubbleGraph2DRendererTest {
    
    @Test
    public void test1() throws Exception {
        Random rand = new Random(0);
        int size = 50;
        ArrayDouble x = new ArrayDouble(new double[size], false);
        ArrayDouble y = new ArrayDouble(new double[size], false);
        ArrayDouble z = new ArrayDouble(new double[size], false);
        String[] labelSet = new String[] {"First", "Second", "Third", "Fourth", "Fifth"};
        List<String> labels = new ArrayList<String>(size);
        for (int i = 0; i < size; i++) {
            x.setDouble(i, rand.nextGaussian());
            y.setDouble(i, rand.nextGaussian());
            z.setDouble(i, 5.0 + rand.nextGaussian());
            labels.add(labelSet[rand.nextInt(labelSet.length)]);
        }
        
        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
        BufferedImage image = new BufferedImage(300, 200, BufferedImage.TYPE_3BYTE_BGR);
        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(300, 200);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.1", image);
        
        renderer.update(renderer.newUpdate().highlightFocusValue(true).focusPixel(178,90));
        graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.1a", image);
        assertThat(renderer.getFocusValueIndex(), equalTo(48));
        
        renderer.update(renderer.newUpdate().highlightFocusValue(true).focusPixel(171,88));
        graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.1b", image);
        assertThat(renderer.getFocusValueIndex(), equalTo(6));
    }
    
    @Test
    public void test2() throws Exception {
        ArrayDouble x = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble y = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble z = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        List<String> labels = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth",
                "First", "Second", "Third", "Fourth", "Fifth");
        
        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_3BYTE_BGR);
        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(600, 399);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.2", image);
    }
    
    @Test
    public void test3() throws Exception {
        ArrayDouble x = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble y = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble z = new ArrayDouble(-1,2,-3,4,-5,6,-7,8,-9,10);
        List<String> labels = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth",
                "First", "Second", "Third", "Fourth", "Fifth");
        
        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_3BYTE_BGR);
        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(600, 399);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.3", image);
    }
    
    @Test
    public void test4() throws Exception {
        ArrayDouble x = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble y = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble z = new ArrayDouble(-1,-2,-3,-4,-5,-6,-7,-8,-9,-10);
        List<String> labels = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth",
                "First", "Second", "Third", "Fourth", "Fifth");
        
        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_3BYTE_BGR);
        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(600, 399);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.4", image);
    }
    
    @Test
    public void test5() throws Exception {
        ArrayDouble x = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble y = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
        ArrayDouble z = new ArrayDouble(0,0,0,0,0,0,0,0,0,0);
        List<String> labels = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth",
                "First", "Second", "Third", "Fourth", "Fifth");
        
        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_3BYTE_BGR);
        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(600, 399);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        renderer.draw(graphics, data);
        ImageAssert.compareImages("bubble2D.5", image);
    }
//    
//    @Test
//    public void test3() throws Exception {
//        ArrayDouble x = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
//        ArrayDouble y = new ArrayDouble(1,2,3,4,5,6,7,8,9,10);
//        ArrayDouble z = new ArrayDouble(1,4,9,16,25,36,49,64,81,100);
//        List<String> labels = Arrays.asList("First", "Second", "Third", "Fourth", "Fifth",
//                "First", "Second", "Third", "Fourth", "Fifth");
//        
//        Point3DWithLabelDataset data = Point3DWithLabelDatasets.build(x, y, z, labels);
//        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_3BYTE_BGR);
//        BubbleGraph2DRenderer renderer = new BubbleGraph2DRenderer(600, 400);
//        Graphics2D graphics = (Graphics2D) image.getGraphics();
//        renderer.draw(graphics, data);
//        ImageAssert.compareImages("bubble2D.3", image);
//    }
}
