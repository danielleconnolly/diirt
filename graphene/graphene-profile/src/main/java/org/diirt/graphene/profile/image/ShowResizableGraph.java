/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.diirt.graphene.profile.image;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import org.diirt.graphene.AreaGraph2DRenderer;
import org.diirt.graphene.AreaGraph2DRendererUpdate;
import org.diirt.graphene.Cell1DDataset;
import org.diirt.graphene.LineGraph2DRenderer;
import org.diirt.graphene.LineGraph2DRendererUpdate;
import org.diirt.graphene.Point2DDataset;

/**
 *
 * @author carcassi
 */
public class ShowResizableGraph extends javax.swing.JFrame {
    
    public abstract static class Renderer {
        public abstract void redrawHistogram(BufferedImage image);
    }

    private final Renderer renderer;
    
    /**
     * Creates new form ShowImage
     */
    public ShowResizableGraph(final Renderer renderer) {
        initComponents();
        this.renderer = renderer;
        imagePanel.setImage(new BufferedImage(getRootPane().getWidth(), getRootPane().getHeight(), BufferedImage.TYPE_3BYTE_BGR));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                redrawGraph();
            }
            
        });
        redrawGraph();
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagePanel = new ImagePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        imagePanel.setLayout(new java.awt.FlowLayout());
        getContentPane().add(imagePanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void redrawGraph() {
        BufferedImage newImage = new BufferedImage(getRootPane().getWidth(), getRootPane().getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.redrawHistogram(newImage);
        imagePanel.setImage(newImage);
    }

    public static void showGraph(final Renderer renderer) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ShowResizableGraph frame = new ShowResizableGraph(renderer);
                frame.setSize(600, 400);
                frame.setVisible(true);
            }
        });
    }
    
    public static void showHistogram(final Cell1DDataset hist) {
        final AreaGraph2DRenderer renderer = new AreaGraph2DRenderer(200, 300);
        showGraph(new Renderer() {

            @Override
            public void redrawHistogram(BufferedImage image) {
                renderer.update(new AreaGraph2DRendererUpdate()
                        .imageHeight(image.getHeight())
                        .imageWidth(image.getWidth()));
                renderer.draw(image.createGraphics(), hist);
            }
        });
    }
    
    public static void showLineGraph(final Point2DDataset dataset) {
        final LineGraph2DRenderer renderer = new LineGraph2DRenderer(200, 300);
        showGraph(new Renderer() {

            @Override
            public void redrawHistogram(BufferedImage image) {
                renderer.update(new LineGraph2DRendererUpdate()
                        .imageHeight(image.getHeight())
                        .imageWidth(image.getWidth()));
                renderer.draw(image.createGraphics(), dataset);
            }
        });
    }

    private ImagePanel imagePanel;
}
