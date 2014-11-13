/**
 * Copyright (C) 2012-14 graphene developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.graphene;
import org.epics.util.stats.Range;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import org.epics.util.array.ListInt;
import org.epics.util.array.ListMath;
import org.epics.util.array.ListNumber;
import org.epics.util.array.SortedListView;

/**
 * Provides low level drawing operations, at the right granularity to perform
 * them efficiently, but without performing any high level decisions (e.g. formatting,
 * layout)
 * <p>
 * This class takes care of putting the pixels where it is told. It specifically
 * does not concerns itself with the calculation of what/where to draw. It provides
 * the drawing of aggregated data structures, so that the plotting can be 
 * efficient and clean.
 * <p>
 * It also serves as a wrapper around Java2D (<code>Graphics2D</code>) so that
 * the drawing can be re-implemented efficiently on other engines in the future,
 * such as JavaFX.
 * 
 * @author carcassi, sjdallst, asbarber, jkfeng
 */
public class GraphBuffer {
    
    private final BufferedImage image;
    private final Graphics2D g;
    
    /**
     * Represents the pixels of a 2D image. if a the image has a width w, then
     * the point (x, y) is represented by the y*width + x pixel.
     */
    private final byte[] pixels;
    private final boolean hasAlphaChannel;
    private final int width, height;
    
    // Strategy for calculating the axis range
    private AxisRangeInstance xAxisRange = AxisRanges.display().createInstance();
    private AxisRangeInstance yAxisRange = AxisRanges.display().createInstance();
    
    private double xPlotValueStart;
    private double yPlotValueStart;
    private double xPlotValueEnd;
    private double yPlotValueEnd;
 
    private  double xPlotCoordStart;
    private  double yPlotCoordStart;
    private  double yPlotCoordEnd;
    private  double xPlotCoordEnd;
    /**
     * Creates a GraphBuffer with the given image on which to draw a graph.
     * 
     * @param image an image on which we can draw a graph
     */
    private GraphBuffer(BufferedImage image){
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        pixels = ((DataBufferByte)this.image.getRaster().getDataBuffer()).getData();
        hasAlphaChannel = image.getAlphaRaster() != null;
        g = image.createGraphics();
        
        xPlotCoordStart=0; 
        xPlotCoordEnd=width;
        yPlotCoordStart=height; 
        yPlotCoordEnd=0; 
    }
    
    /**
     * Creates a GraphBuffer with the given width and height.
     * 
     * @param width width of the graph
     * @param height height of the graph
     */
    public GraphBuffer(int width, int height) {
        this(new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR));
    }
    
    /**
     * Creates a GraphBuffer suitable for the given renderer. Makes sure
     * all the parameters from the renderer are consistent with the buffer itself.
     * 
     * @param renderer the graph renderer
     */
    public GraphBuffer(Graph2DRenderer<?> renderer) {
        this(renderer.getImageWidth(), renderer.getImageHeight());
    }
    
    /**
     * Changes the pixel at the given coordinates to the given color.
     * TODO: make sure all other plotting functions use this.
     * 
     * @param x	x-coordinate of a pixel
     * @param y	y-coordinate of a pixel
     * @param color color-value of the pixel
     */
    public void setPixel(int x, int y, int color){
        if(hasAlphaChannel){
            pixels[y*image.getWidth()*4 + x*4 + 3] = (byte)(color >> 24 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 0] = (byte)(color >> 0 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 1] = (byte)(color >> 8 & 0xFF);
            pixels[y*image.getWidth()*4 + x*4 + 2] = (byte)(color >> 16 & 0xFF);

        }
        else{
            pixels[y*image.getWidth()*3 + x*3 + 0] = (byte)(color >> 0 & 0xFF);
            pixels[y*image.getWidth()*3 + x*3 + 1] = (byte)(color >> 8 & 0xFF);
            pixels[y*image.getWidth()*3 + x*3 + 2] = (byte)(color >> 16 & 0xFF);
        }
    }
    
    /**
     * 
     * @return the image that is currently drawn on this graph
     */
    public BufferedImage getImage(){
        return image;
    }
    
    /**
     * Temporary method to retrieve the graphics context. Will be removed once
     * this class is finished.
     * 
     * @return 
     */
    public Graphics2D getGraphicsContext(){
        return g;
    }
    
    /**
     * Plots the given data points on this buffer. 
     * @param xStartPoint the starting x-coordinate of the data image
     * @param yStartPoint the starting y-coordinate of the data image
     * @param xPointToDataMap the x-coordinates of data points to plot
     * @param yPointToDataMap the y-coordinates of data points to plot
     * @param data a third-coordinate, z, of data points to plot 
     * this third-coordinate gets represented by a color
     * @param colorMap a mapping of real numbers to colors, so that 
     * we can represent the z-coordinate of data points as a color
     */
    public void drawDataImage(int xStartPoint, int yStartPoint,
			int[] xPointToDataMap, int[] yPointToDataMap,
			Cell2DDataset data, NumberColorMapInstance colorMap) {
	
        int previousYData = -1;
	
	//go through each data point
        for (int yOffset = 0; yOffset < yPointToDataMap.length; yOffset++) {
            int yData = yPointToDataMap[yOffset];
            if (yData != previousYData) {
                for (int xOffset = 0; xOffset < xPointToDataMap.length; xOffset++) {  
                    int xData = xPointToDataMap[xOffset];
		    
		    //determine what color corresponds to the value at the given point
                    int rgb = colorMap.colorFor(data.getValue(xData, yData));
		    
		    //plot the point using the correct color
		    setPixel( xStartPoint + xOffset  , yStartPoint + yOffset , rgb );
                }
		
	    // If the current line is the same as the previous, it's
            // faster to make a copy
            } else {
                if (hasAlphaChannel) {
                    System.arraycopy(pixels, (yStartPoint + yOffset - 1)*width*4 + 4*xStartPoint,
                            pixels, (yStartPoint + yOffset)*width*4 + 4*xStartPoint, xPointToDataMap.length*4);
                } else {
                    System.arraycopy(pixels, (yStartPoint + yOffset - 1)*width*3 + 3*xStartPoint,
                            pixels, (yStartPoint + yOffset)*width*3 + 3*xStartPoint, xPointToDataMap.length*3);
                }
            }
            previousYData = yData;
        }
    }
    
    private double xLeftValue;
    private double xRightValue;
    private double xLeftPixel;
    private double xRightPixel;
    private ValueScale xValueScale;

    /**
     * Sets the scaling data for the x axis assuming values are going
     * to represent cells. The minimum value is going to be positioned at the
     * left of the xMinPixel while the maximum value is going to be position
     * at the right of the xMaxPixel.
     * 
     * @param range the range to be displayed
     * @param xMinPixel the pixel corresponding to the minimum
     * @param xMaxPixel the pixel corresponding to the maximum
     * @param xValueScale the scale used to transform values to pixel
     */
    public void setXScaleAsCell(Range range, int xMinPixel, int xMaxPixel, ValueScale xValueScale) {
        xLeftValue = range.getMinimum().doubleValue();
        xRightValue = range.getMaximum().doubleValue();
        xLeftPixel = xMinPixel;
        xRightPixel = xMaxPixel + 1;
        this.xValueScale = xValueScale;
    }

    /**
     * Sets the scaling data for the x axis assuming values are going
     * to represent points. The minimum value is going to be positioned in the
     * center of the xMinPixel while the maximum value is going to be position
     * in the middle of the xMaxPixel.
     * 
     * @param range the range to be displayed
     * @param xMinPixel the pixel corresponding to the minimum
     * @param xMaxPixel the pixel corresponding to the maximum
     * @param xValueScale the scale used to transform values to pixel
     */
    public void setXScaleAsPoint(Range range, int xMinPixel, int xMaxPixel, ValueScale xValueScale) {
        this.xLeftValue = range.getMinimum().doubleValue();
        this.xRightValue = range.getMaximum().doubleValue();
        this.xLeftPixel = xMinPixel + 0.5;
        this.xRightPixel = xMaxPixel + 0.5;
        this.xValueScale = xValueScale;
    }

    /**
     * Converts the given value to the pixel position.
     * 
     * @param value the value
     * @return the pixel where the value should be mapped
     */
    public int xValueToPixel(double value) {   
        return (int) xValueScale.scaleValue(value, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the left side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelLeftToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the right side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelRightToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue + 1, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }

    /**
     * Converts the center of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double xPixelCenterToValue(int pixelValue) {
        return xValueScale.invScaleValue(pixelValue + 0.5, xLeftValue, xRightValue, xLeftPixel, xRightPixel);
    }
    
    private double yTopValue;
    private double yBottomValue;
    private double yTopPixel;
    private double yBottomPixel;
    private ValueScale yValueScale;


    /**
     * Sets the scaling data for the y axis assuming values are going
     * to represent cells. The minimum value is going to be positioned at the
     * bottom of the yMinPixel while the maximum value is going to be position
     * at the top of the yMaxPixel.
     * 
     * @param range the range to be displayed
     * @param yMinPixel the pixel corresponding to the minimum
     * @param yMaxPixel the pixel corresponding to the maximum
     * @param yValueScale the scale used to transform values to pixel
     */
    public void setYScaleAsCell(Range range, int yMinPixel, int yMaxPixel, ValueScale yValueScale) {  
        yTopValue = range.getMaximum().doubleValue();
        yBottomValue = range.getMinimum().doubleValue();
        yTopPixel = yMaxPixel - 1;
        yBottomPixel = yMinPixel;
        this.yValueScale = yValueScale;
    }

    /**
     * Sets the scaling data for the y axis assuming values are going
     * to represent points. The minimum value is going to be positioned in the
     * center of the yMinPixel while the maximum value is going to be position
     * in the center of the yMaxPixel.
     * 
     * @param range the range to be displayed
     * @param yMinPixel the pixel corresponding to the minimum
     * @param yMaxPixel the pixel corresponding to the maximum
     * @param yValueScale the scale used to transform values to pixel
     */
    public void setYScaleAsPoint(Range range, int yMinPixel, int yMaxPixel, ValueScale yValueScale) {
        yTopValue = range.getMaximum().doubleValue();
        yBottomValue = range.getMinimum().doubleValue();
        yTopPixel = yMaxPixel - 0.5;
        yBottomPixel = yMinPixel - 0.5;
        this.yValueScale = yValueScale;
    }

    /**
     * Converts the given value to the pixel position.
     * 
     * @param value the value
     * @return the pixel where the value should be mapped
     */
    public int yValueToPixel(double value) {
        return (int) Math.ceil(yValueScale.scaleValue(value, yBottomValue, yTopValue, yBottomPixel, yTopPixel));
    }
    
    /**
     * Converts the top side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelTopToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue - 1, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }
    
    /**
     * Converts the center of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelCenterToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue - 0.5, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }
    
    /**
     * Converts the bottom side of given pixel position to the actual value.
     * 
     * @param pixelValue the pixel
     * @return the value at the pixel
     */
    public double yPixelBottomToValue(int pixelValue) {
        return yValueScale.invScaleValue(pixelValue, yBottomValue, yTopValue, yBottomPixel, yTopPixel);
    }

    void drawBackground(Color color) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
    
    private static final int MIN = 0;
    private static final int MAX = 1;
    
    // TODO: methods to draw labels on the top and on the left side of the graph

    /**
     * Draws the given labels a the bottom of the graph area.
     * 
     * @param labels a list of x-axis labels to be drawn
     * @param valuePixelPositions the central x-coordinate of each label
     * @param labelColor color of the label text
     * @param labelFont font style of the label text
     * @param leftPixel the leftmost x-coordinate at which the label may be drawn
     * @param rightPixel the rightmost x-coordinate at which the label may be drawn
     * @param topPixel the y-coordinate of the top of the label
     */
    void drawBottomLabels(List<String> labels, ListInt valuePixelPositions, Color labelColor, Font labelFont, int leftPixel, int rightPixel, int topPixel) {
        // Draw X labels
        if (labels != null && !labels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label to make sure they fit
            int[] drawRange = new int[] {leftPixel, rightPixel};
            drawLineLabel(g, metrics, labels.get(0), valuePixelPositions.getInt(0),
                drawRange, topPixel, true, false);
            drawLineLabel(g, metrics, labels.get(labels.size() - 1),
                    valuePixelPositions.getInt(labels.size() - 1),
                drawRange, topPixel, false, false);
            
            for (int i = 1; i < labels.size() - 1; i++) {
                drawLineLabel(g, metrics, labels.get(i), valuePixelPositions.getInt(i),
                    drawRange, topPixel, true, false);
            }
        }
    }
    
    /**
     * Draws some labels, which are plaintext, on the graph.
     * 
     * @param graphics the graphics object containing the graph
     * @param metrics the font style
     * @param text the text of the label
     * @param xCenter x coordinate of where the label should be centered
     * @param drawRange defines the range of coordinates where this label is allowed to be centered
     * @param yTop define the y-coordinate of the top of the label
     * @param updateMin *MC I do not understand what this parameter is for
     * @param centeredOnly if this label must be centered at the given x center coordinate
     */
    private static void drawLineLabel(Graphics2D graphics, FontMetrics metrics, String text, int xCenter, int[] drawRange, int yTop, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < xCenter || drawRange[MIN] > xCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.TOP;
        int targetX = xCenter;
        int halfWidth = metrics.stringWidth(text) / 2;
        if (xCenter < drawRange[MIN] + halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_LEFT;
            targetX = drawRange[MIN];
        } else if (xCenter > drawRange[MAX] - halfWidth) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetX = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, targetX, yTop, text);
        
	//*MC: why are we updating the x value with the height, a y-value property, of the label?
        if (updateMin) {
            drawRange[MIN] = targetX + metrics.getHeight();
        } else {
            drawRange[MAX] = targetX - metrics.getHeight();
        }
    }

    //*MC: This method is nearly an exact copy of drawBottomLabels()
    //is it possible/useful to abstract this code out?
    void drawLeftLabels(List<String> labels, ListInt valuePixelPositions, Color labelColor, Font labelFont, int bottomPixel, int topPixel, int leftPixel) {
        // Draw Y labels
        if (labels != null && !labels.isEmpty()) {
            //g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setColor(labelColor);
            g.setFont(labelFont);
            FontMetrics metrics = g.getFontMetrics();

            // Draw first and last label
            int[] drawRange = new int[] {topPixel, bottomPixel};
            drawColumnLabel(g, metrics, labels.get(0), valuePixelPositions.getInt(0),
                drawRange, leftPixel, true, false);
            drawColumnLabel(g, metrics, labels.get(labels.size() - 1), valuePixelPositions.getInt(labels.size() - 1),
                drawRange, leftPixel, false, false);
            
            for (int i = 1; i < labels.size() - 1; i++) {
                drawColumnLabel(g, metrics, labels.get(i), valuePixelPositions.getInt(i),
                    drawRange, leftPixel, true, false);
            }
        }
    }
    
    /**
     * *MC: This is almost an exact copy of drawLineLabel(). Is it possible/useful
     * to abstract it out?
     * 
     * @param graphics
     * @param metrics
     * @param text
     * @param yCenter
     * @param drawRange
     * @param xRight
     * @param updateMin
     * @param centeredOnly 
     */
    private static void drawColumnLabel(Graphics2D graphics, FontMetrics metrics, String text, int yCenter, int[] drawRange, int xRight, boolean updateMin, boolean centeredOnly) {
        // If the center is not in the range, don't draw anything
        if (drawRange[MAX] < yCenter || drawRange[MIN] > yCenter)
            return;
        
        // If there is no space, don't draw anything
        if (drawRange[MAX] - drawRange[MIN] < metrics.getHeight())
            return;
        
        Java2DStringUtilities.Alignment alignment = Java2DStringUtilities.Alignment.RIGHT;
        int targetY = yCenter;
        int halfHeight = metrics.getAscent() / 2;
        if (yCenter < drawRange[MIN] + halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.TOP_RIGHT;
            targetY = drawRange[MIN];
        } else if (yCenter > drawRange[MAX] - halfHeight) {
            // Can't be drawn in the center
            if (centeredOnly)
                return;
            alignment = Java2DStringUtilities.Alignment.BOTTOM_RIGHT;
            targetY = drawRange[MAX];
        }

        Java2DStringUtilities.drawString(graphics, alignment, xRight, targetY, text);
        
        if (updateMin) {
            drawRange[MAX] = targetY - metrics.getHeight();
        } else {
            drawRange[MIN] = targetY + metrics.getHeight();
        }
    }
    
    /**
     * Draws horizontal lines on the graph.
     * 
     * @param referencePixels the y-coordinates of the horizontal reference lines to be drawn
     * @param lineColor	the color of the reference lines
     * @param graphLeftPixel the leftmost x pixel coordinate of the graph
     * @param graphRightPixel the rightmost x pixel coordinate on the graph
     */
    void drawHorizontalReferenceLines(ListInt referencePixels, Color lineColor, int graphLeftPixel, int graphRightPixel) {
        g.setColor(lineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	
	//draw a line from (graphLeftPixel, y) to (graphRightPixel, y)
        for (int i = 0; i < referencePixels.size(); i++) {
            g.drawLine(graphLeftPixel, referencePixels.getInt(i), graphRightPixel, referencePixels.getInt(i));
        }
    }
    
    /**
     * *MC: This is nearly identical to drawHorizontalReferenceLines(). Is it possible/useful 
     * to abstract out to one private method, drawLine, that draws a line from any (x1, y1) to any (x2, y2) 
     * and two public methods drawHorizontalReferenceLines, drawVerticalReferenceLines that use drawLine?
     * 
     * @param referencePixels
     * @param lineColor
     * @param graphBottomPixel
     * @param graphTopPixel 
     */
    void drawVerticalReferenceLines(ListInt referencePixels, Color lineColor, int graphBottomPixel, int graphTopPixel) {
        g.setColor(lineColor);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        for (int i = 0; i < referencePixels.size(); i++) {
            g.drawLine(referencePixels.getInt(i), graphTopPixel, referencePixels.getInt(i), graphBottomPixel);
        }
    }
    
     private static class ScaledData {
        private double[] scaledX;
        private double[] scaledY;
        private int start;
        private int end;
    }
    void drawLineGraph(Point2DDataset data, InterpolationScheme interpolation,ReductionScheme reduction){
        
        calculateGraphArea(data);
        SortedListView xValues = org.epics.util.array.ListNumbers.sortedView(data.getXValues());
        ListNumber yValues = org.epics.util.array.ListNumbers.sortedView(data.getYValues(), xValues.getIndexes());
        
        //g.setClip(0, 0, 300, 200);
        g.setColor(Color.BLACK);
        drawValueExplicitLine(xValues, yValues, interpolation, reduction);
    
    }
    private void drawValueExplicitLine(ListNumber xValues, ListNumber yValues, InterpolationScheme interpolation, ReductionScheme reduction) {
        
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        ScaledData scaledData;
        
     
        //Narrow Data
        int start = org.epics.util.array.ListNumbers.binarySearchValueOrLower(xValues, xPlotValueStart);
        int end = org.epics.util.array.ListNumbers.binarySearchValueOrHigher(xValues, xPlotValueEnd);
        
        xValues = ListMath.limit(xValues, start, end + 1);
        yValues = ListMath.limit(yValues, start, end + 1);
        
        switch (reduction) {
            default:
                throw new IllegalArgumentException("Reduction scheme " + reduction + " not supported");
            case NONE:
                scaledData = scaleNoReduction(xValues, yValues, start);
                break;
           // case FIRST_MAX_MIN_LAST:
           //     scaledData = scaleFirstMaxMinLastReduction(xValues, yValues, start);
           //     break;
        }
        
        // create path
        Path2D path;
        switch (interpolation) {
            default:
            case NEAREST_NEIGHBOR:
                path = nearestNeighbour(scaledData);
                break;
            case LINEAR:
                path = linearInterpolation(scaledData);
                break;
            case CUBIC:
                path = cubicInterpolation(scaledData);
                break;
        }

        // Draw the line
        g.draw(path);
    }
    private void calculateGraphArea(Point2DDataset data){
        
        Range xPlotRange=xAxisRange.axisRange(data.getXStatistics(), data.getXDisplayRange()); 
        Range yPlotRange=yAxisRange.axisRange(data.getYStatistics(), data.getYDisplayRange()); 
        xPlotValueStart = xPlotRange.getMinimum().doubleValue();
        xPlotValueEnd = xPlotRange.getMaximum().doubleValue();
        yPlotValueStart = yPlotRange.getMinimum().doubleValue();
        yPlotValueEnd = yPlotRange.getMaximum().doubleValue();
    }
    
    private double scaledX(double value) {
        return xValueScale.scaleValue(value, xPlotValueStart, xPlotValueEnd, xPlotCoordStart, xPlotCoordEnd);
    }

    private double scaledY(double value) {
        return yValueScale.scaleValue(value, yPlotValueStart, yPlotValueEnd,yPlotCoordStart, yPlotCoordEnd);
    }
    private ScaledData scaleNoReduction(ListNumber xValues, ListNumber yValues) {
        return scaleNoReduction(xValues, yValues, 0);
    }

    private ScaledData scaleNoReduction(ListNumber xValues, ListNumber yValues, int dataStart) {
        ScaledData scaledData = new ScaledData();
        int dataCount = xValues.size();
        scaledData.scaledX = new double[dataCount];
        scaledData.scaledY = new double[dataCount];
        for (int i = 0; i < scaledData.scaledY.length; i++) {
            scaledData.scaledX[i] = scaledX(xValues.getDouble(i));
            scaledData.scaledY[i] = scaledY(yValues.getDouble(i));
            //processScaledValue(dataStart + i, xValues.getDouble(i), yValues.getDouble(i), scaledData.scaledX[i], scaledData.scaledY[i]);
        }
        scaledData.end = dataCount;
        return scaledData;
    }
    
    
    
    private static Path2D.Double nearestNeighbour(ScaledData scaledData) {
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double line = new Path2D.Double();
        line.moveTo(scaledX[start], scaledY[start]);
        for (int i = 1; i < end; i++) {
            double halfX = scaledX[i - 1] + (scaledX[i] - scaledX[i - 1]) / 2;
            if (!java.lang.Double.isNaN(scaledY[i-1])) {
                line.lineTo(halfX, scaledY[i - 1]);
                if (!java.lang.Double.isNaN(scaledY[i]))
                    line.lineTo(halfX, scaledY[i]);
            } else {
                line.moveTo(halfX, scaledY[i]);
            }
        }
        line.lineTo(scaledX[end - 1], scaledY[end - 1]);
        return line;
    }
    
    private static Path2D.Double linearInterpolation(ScaledData scaledData){
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double line = new Path2D.Double();
        
        for (int i = start; i < end; i++) {
            // Do I have a current value?
            if (!java.lang.Double.isNaN(scaledY[i])) {
                // Do I have a previous value?
                if (i != start && !java.lang.Double.isNaN(scaledY[i - 1])) {
                    // Here I have both the previous value and the current value
                    line.lineTo(scaledX[i], scaledY[i]);
                } else {
                    // Don't have a previous value
                    // Do I have a next value?
                    if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        // There is no value before, but there is a value after
                        line.moveTo(scaledX[i], scaledY[i]);
                    } else {
                        // There is no value either before or after
                        line.moveTo(scaledX[i] - 1, scaledY[i]);
                        line.lineTo(scaledX[i] + 1, scaledY[i]);
                    }
                }
            } 
        }
        return line;
    }
    
    private static Path2D.Double cubicInterpolation(ScaledData scaledData){
        double[] scaledX = scaledData.scaledX;
        double[] scaledY = scaledData.scaledY;
        int start = scaledData.start;
        int end = scaledData.end;
        Path2D.Double path = new Path2D.Double();
        for (int i = start; i < end; i++) {
            
            double y1;
            double y2;
            double x1;
            double x2;
            double y0;
            double x0;
            double y3;
            double x3;
            
            double bx0;
            double by0;
            double bx3;
            double by3;
            double bdy0;
            double bdy3;
            double bx1;
            double by1;
            double bx2;
            double by2;
            
            //Do I have current value?
            if (!java.lang.Double.isNaN(scaledY[i])){
                //Do I have previous value?
                if (i > start && !java.lang.Double.isNaN(scaledY[i - 1])) {
                    //Do I have value two before?
                    if (i > start + 1 && !java.lang.Double.isNaN(scaledY[i - 2])) {
                        //Do I have next value?
                        if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                            y2 = scaledY[i];
                            x2 = scaledX[i];
                            y0 = scaledY[i - 2];
                            x0 = scaledX[i - 2];
                            y3 = scaledY[i + 1];
                            x3 = scaledX[i + 1];
                            y1 = scaledY[i - 1];
                            x1 = scaledX[i - 1];
                            bx0 = x1;
                            by0 = y1;
                            bx3 = x2;
                            by3 = y2;
                            bdy0 = (y2 - y0) / (x2 - x0);
                            bdy3 = (y3 - y1) / (x3 - x1);
                            bx1 = bx0 + (x2 - x0) / 6.0;
                            by1 = (bx1 - bx0) * bdy0 + by0;
                            bx2 = bx3 - (x3 - x1) / 6.0;
                            by2 = (bx2 - bx3) * bdy3 + by3;
                            path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                        } 
                        else{//Have current, previous, two before, but not value after
                            y2 = scaledY[i];
                            x2 = scaledX[i];
                            y1 = scaledY[i - 1];
                            x1 = scaledX[i - 1];
                            y0 = scaledY[i - 2];
                            x0 = scaledX[i - 2];
                            y3 = y2 + (y2 - y1) / 2;
                            x3 = x2 + (x2 - x1) / 2;
                            bx0 = x1;
                            by0 = y1;
                            bx3 = x2;
                            by3 = y2;
                            bdy0 = (y2 - y0) / (x2 - x0);
                            bdy3 = (y3 - y1) / (x3 - x1);
                            bx1 = bx0 + (x2 - x0) / 6.0;
                            by1 = (bx1 - bx0) * bdy0 + by0;
                            bx2 = bx3 - (x3 - x1) / 6.0;
                            by2 = (bx2 - bx3) * bdy3 + by3;
                            path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                        } 
                    } else if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        //Have current , previous, and next, but not two before
                        path.moveTo(scaledX[i - 1], scaledY[i - 1]);
                        y2 = scaledY[i];
                        x2 = scaledX[i];
                        y1 = scaledY[i - 1];
                        x1 = scaledX[i - 1];
                        y0 = y1 - (y2 - y1) / 2;
                        x0 = x1 - (x2 - x1) / 2;
                        y3 = scaledY[i + 1];
                        x3 = scaledX[i + 1];
                        bx0 = x1;
                        by0 = y1;
                        bx3 = x2;
                        by3 = y2;
                        bdy0 = (y2 - y0) / (x2 - x0);
                        bdy3 = (y3 - y1) / (x3 - x1);
                        bx1 = bx0 + (x2 - x0) / 6.0;
                        by1 = (bx1 - bx0) * bdy0 + by0;
                        bx2 = bx3 - (x3 - x1) / 6.0;
                        by2 = (bx2 - bx3) * bdy3 + by3;
                        path.curveTo(bx1, by1, bx2, by2, bx3, by3);
                    }else{//have current, previous, but not two before or next
                        path.lineTo(scaledX[i], scaledY[i]);
                    }
                //have current, but not previous
                }else{
                    // No previous value
                    if (i != end - 1 && !java.lang.Double.isNaN(scaledY[i + 1])) {
                        // If we have the next value, just move, we'll draw later
                        path.moveTo(scaledX[i], scaledY[i]);
                    } else {
                        // If not, write a small horizontal line
                        path.moveTo(scaledX[i] - 1, scaledY[i]);
                        path.lineTo(scaledX[i] + 1, scaledY[i]);
                    }
                }
            }else{ //do not have current
               // Do nothing
             }
        }
        return path;
    }
}
