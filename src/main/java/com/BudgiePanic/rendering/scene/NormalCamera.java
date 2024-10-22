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
package com.BudgiePanic.rendering.scene;

import java.util.function.Function;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;

/**
 * The normal camera records the normals of shapes that are struck by rays cast from the camera monitoring.
 * Rays that cause no intersections will write BLACK to the normal map.
 *
 * @author BudgiePanic
 */
public class NormalCamera implements Camera {

    /**
     * Different ways of processing normals before writing to the normal map
     */
    public static sealed interface NormalMode extends Function<Tuple, Tuple> permits RawNormals, ScaledNormals {}

    /**
     * Writes the raw normal data to the normal map.
     * NOTE: the canvas writer will clamp color value to be between 0 and 1
     *       so writing raw normals to an image using this mode will yield unexpected results because normal directionss can be between -1 and 1,
     *       this is expected behaviour.
     */
    private static final class RawNormals implements NormalMode {
        protected static final RawNormals instance = new RawNormals();
        private RawNormals() {}
        @Override
        public Tuple apply(Tuple t) { return t; }
    }

    /**
     * Writes scaled normal data to the normal map. Normals are scaled to be between 0 and 1 so they will appear in the exported image.
     * NOTE: this should be the go to processing mode.
     */
    private static final class ScaledNormals implements NormalMode {
        protected static final ScaledNormals instance = new ScaledNormals();
        private ScaledNormals() {}
        @Override
        public Tuple apply(Tuple normal) {
            final var x = map(normal.x);
            final var y = map(normal.y);
            final var z = map(normal.z);
            return new Tuple(x, y, z, 0f);
        }
        /**
         * Linearly interpolate from range [-1,1] to [0,1]
         * @param value
         *   the value to interpolate
         * @return
         *   the interpolated value
         */
        private final static double map(double value) {
            final double oldMin = -1, oldMax = 1, newMin = 0f, newMax = oldMax;
            return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
        } 
    }

    /**
     * Writes raw normal map data to the canvas buffer. May cause unexpected results when exporting to image as the exporter clamps values between 0 and 1.
     */
    public static final NormalMode raw = RawNormals.instance;

    /**
     * Writes scaled normal map data to the canvas buffer
     */
    public static final NormalMode scaled = ScaledNormals.instance;

    /**
     * The camera being monitored.
     * The normal camera will copy the monitoring camera's settings when taking the normal map image.
     */
    protected final BasePerspectiveCamera cameraMonitoring;

    /**
     * The normal processing mode to use before writing the normal data to the normal map buffer.
     */
    protected final NormalMode mode;

    /**
     * Convenience constructor. Uses the default normal processing mode.
     * @param camera
     *   The camera to monitor.
     */
    public NormalCamera(BasePerspectiveCamera camera) { this(camera, scaled); } 

    /**
     * Canonical constructor. Create a new Normal Camera.
     *
     * @param camera
     *   The camera to monitor.
     * @param mode
     *   The normal data processing mode to use.
     */
    public NormalCamera(BasePerspectiveCamera camera, NormalMode mode) {
        this.cameraMonitoring = camera;
        this.mode = mode;
    }

    @Override
    public int width() { return cameraMonitoring.width; }

    @Override
    public int height() { return cameraMonitoring.height; }

    @Override
    public Ray createRay(double pixelColumn, double pixelRow, double time) {
        // create pinhole camera rays so the normal map image is crisp and in-focus
        // TODO: code smell here, this method is 99% similar to PinHoleCamera::createRay, there can probably be an abstraction
        if (pixelColumn < 0 || pixelColumn > cameraMonitoring.width) throw new IllegalArgumentException("invalid pixel column for camera");
        if (pixelRow < 0 || pixelRow > cameraMonitoring.height) throw new IllegalArgumentException("invalid pixel row for camera");
        final var xOffset = (pixelColumn) * cameraMonitoring.pixelSize;
        final var yOffset = (pixelRow) * cameraMonitoring.pixelSize;
        final var worldX = cameraMonitoring.halfWidth - xOffset;
        final var worldY = cameraMonitoring.halfHeight - yOffset;
        final var worldZ = -cameraMonitoring.focalDistance;
        final var cameraInverse = cameraMonitoring.transform.inverse();
        final var pixel = cameraInverse.multiply(Tuple.makePoint(worldX, worldY, worldZ));
        final var origin = cameraInverse.multiply(Tuple.makePoint());
        final var direction = pixel.subtract(origin).normalize();
        return new Ray(origin, direction, time);
    }

    @Override
    public Color pixelAt(World world, double pixelColumn, double pixelRow, double time) {
        // intersect pixel-ray with the world and record the normal of the shape that was intersected with
        final var ray = createRay(pixelColumn, pixelRow, time);
        final var intersections = world.intersect(ray);
        final Tuple normal = intersections.flatMap(Intersection::Hit).map(i -> i.computeNormal(ray)).map(mode).orElseGet(() -> Colors.black);
        return new Color(normal.x, normal.y, normal.z);
    }
}
