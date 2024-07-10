/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.reporting.ProgressWrapper;
import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;

/**
 * Common functionality in most ray tracer demos.
 * 
 * @author BudgiePanic
 */
public abstract class BaseDemo implements Runnable {
    
    protected final String fileName;
    protected final Camera camera;

    public BaseDemo() {
        this.fileName = getName();
        this.camera = getCamera();
    }

    /**
     * Get the name of the image file produced by this demo.
     *
     * @return
     *     The name of the image file produced by this demo.
     */
    protected abstract String getName();

    /**
     * Get the camera used to image this demo scene.
     *
     * @return
     *   The camera used to image this demo scene.
     */
    protected abstract Camera getCamera();

    /**
     * Assemble the world that contains the content highlighted by this demo.
     *
     * @return
     *     The world to take an image of.
     */
    protected abstract World createWorld();

    protected Canvas imageWorld() {
        System.out.println("INFO: taking picture");
        var world = createWorld();
        var canvas = camera.takePicture(world, new ProgressWrapper(new ArrayCanvas(camera.width(), camera.height()), 20));
        return canvas;
    }

    protected static void saveImageToFile(Canvas canvas, String fileName) {
        CanvasWriter.saveImageToFile(canvas, fileName);
    }

    @Override
    public void run() {
        var pixels = imageWorld();
        saveImageToFile(pixels, fileName);
    }
    
}
