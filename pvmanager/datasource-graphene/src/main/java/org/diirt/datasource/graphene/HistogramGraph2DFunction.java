/**
 * Copyright (C) 2010-14 diirt developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.diirt.datasource.graphene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.diirt.graphene.*;
import org.diirt.datasource.QueueCollector;
import org.diirt.datasource.ReadFunction;
import org.diirt.vtype.VImage;
import org.diirt.vtype.VNumber;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueUtil;

/**
 *
 * @author carcassi
 */
class HistogramGraph2DFunction implements ReadFunction<Graph2DResult> {
    
    private ReadFunction<VNumberArray> arrayData;
    
    private AreaGraph2DRenderer renderer = new AreaGraph2DRenderer(300, 200);
    
    private Graph2DResult previousImage;
    private final QueueCollector<AreaGraph2DRendererUpdate> rendererUpdateQueue = new QueueCollector<>(100);

    public HistogramGraph2DFunction(ReadFunction<?> arrayData) {
        this.arrayData = new CheckedReadFunction<VNumberArray>(arrayData, "Data", VNumberArray.class);
    }
    
    public QueueCollector<AreaGraph2DRendererUpdate> getUpdateQueue() {
        return rendererUpdateQueue;
    }

    @Override
    public Graph2DResult readValue() {
        VNumberArray data = arrayData.readValue();
        
        // Data must be available
        if (data == null) {
            return null;
        }
        
        // TODO: check array is one dimensional

        Cell1DDataset dataset = null;
        dataset = DatasetConversions.cell1DDatasetsFromVNumberArray(data);
        
        // Process all renderer updates
        for (AreaGraph2DRendererUpdate rendererUpdate : getUpdateQueue().readValue()) {
            renderer.update(rendererUpdate);
        }
        
        // If no size is set, don't calculate anything
        if (renderer.getImageHeight() == 0 && renderer.getImageWidth() == 0)
            return null;
        
        BufferedImage image = new BufferedImage(renderer.getImageWidth(), renderer.getImageHeight(), BufferedImage.TYPE_3BYTE_BGR);
        renderer.draw(image.createGraphics(), dataset);
        int index = -1;
        if (renderer.getFocusValueIndex() != null) {
            index = renderer.getFocusValueIndex();
        }
        
        return new Graph2DResult(data, ValueUtil.toVImage(image),
                new GraphDataRange(renderer.getXPlotRange(), dataset.getXRange(), dataset.getXRange()),
                new GraphDataRange(renderer.getYPlotRange(), dataset.getStatistics().getRange(), renderer.getYAggregatedRange()),
                index);
    }
    
}
