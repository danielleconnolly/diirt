/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.pvmanager;

import org.epics.pvmanager.expression.DesiredRateExpression;
import org.epics.pvmanager.expression.WriteExpression;

/**
 *
 * @author carcassi
 */
public class WriteExpressionTester {

    private WriteExpression<?> expression;
    private WriteRecipe recipe;
    private QueueCollector<Exception> exceptionCollector = new QueueCollector<>(10);
    private ConnectionCollector connCollector = new ConnectionCollector();
    private PVWriterDirector<?> pvWriterDirector = new PVWriterDirector<Object>(null, null, null, null, null, null, null, null);

    public WriteExpressionTester(WriteExpression<?> expression) {
        this.expression = expression;
        WriteRecipeBuilder builder = new WriteRecipeBuilder();
        expression.fillWriteRecipe(pvWriterDirector, builder);
        pvWriterDirector.connectExpression(expression);
        this.recipe = builder.build(exceptionCollector, connCollector);
    }

    public Object readValue(String name) {
        for (ChannelWriteRecipe channelBuffer : pvWriterDirector.getCurrentWriteRecipe().getChannelWriteBuffers()) {
            if (channelBuffer.getChannelName().equals(name)) {
                @SuppressWarnings("unchecked")
                WriteCache<Object> cache = (WriteCache<Object>) channelBuffer.getWriteSubscription().getWriteCache();
                return cache.getValue();
            }
        }
        throw new IllegalStateException("Can't find buffer for channel '" + name + "'");
    }
    
    public ChannelWriteRecipe recipeFor(String channelName) {
        for (ChannelWriteRecipe channelBuffer : pvWriterDirector.getCurrentWriteRecipe().getChannelWriteBuffers()) {
            if (channelBuffer.getChannelName().equals(channelName)) {
                return channelBuffer;
            }
        }
        return null;
    }
    
    public WriteRecipe getWriteBuffer() {
        return recipe;
    }
    
    @SuppressWarnings("unchecked")
    public WriteFunction<Object> getWriteFunction() {
        return (WriteFunction<Object>) expression.getWriteFunction();
    }
    
    public WriteExpression<?> getExpression() {
        return expression;
    }
    
    public void setValue(Object value) {
        getWriteFunction().setValue(value);
    }
}
