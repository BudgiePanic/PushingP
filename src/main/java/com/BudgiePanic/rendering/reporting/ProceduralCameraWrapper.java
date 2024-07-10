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
package com.BudgiePanic.rendering.reporting;

import java.util.List;

import com.BudgiePanic.rendering.scene.Camera;
import com.BudgiePanic.rendering.scene.World;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Pair;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * A non-multithreaded camera. Used for end to end debugging.
 *
 * @author BudgiePanic
 */
public class ProceduralCameraWrapper implements Camera {

    protected final Camera camera;

    public ProceduralCameraWrapper(Camera camera) {
        this.camera = camera;
    }

    @Override
    public int width() { return camera.width(); }

    @Override
    public int height() { return camera.height(); }

    @Override
    public Ray createRay(double pixelColumn, double pixelRow, double time) { return camera.createRay(pixelColumn, pixelRow, time); }

    @Override
    public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) { return camera.pixelAt(world, pixelColumn, pixelRow, time); }

    @Override
    public Color pixelExposureAt(World world, double pixelColumn, double pixelRow) { return camera.pixelExposureAt(world, pixelColumn, pixelRow); }

    @Override 
    public Canvas takePicture(World world, Canvas canvas) {
        if (canvas == null || canvas.getHeight() < this.height() || canvas.getWidth() < this.width()) throw new IllegalArgumentException();
        List<Pair<Integer, Integer>> jobs = generateJobs();
        for (final var pixel : jobs) {
            final int column = pixel.a();
            final int row = pixel.b();
            final var color = pixelExposureAt(world, column, row);
            canvas.writePixel(column, row, color);
        }
        return canvas;
    }
    
}
