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
package com.BudgiePanic.rendering.util.noise;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

/**
 * Value noise implementation.
 * 
 * @author BudgiePanic
 */
public class Value {
    private Value() {}

    /**
     * point offset storage
     */
    private static final record Triple(int x, int y, int z) {}

    /**
     * Offsets to reach every corner of an integer cell
     */
    protected static final List<Triple> offsets = List.of(
        new Triple(0, 0, 0),
        new Triple(0,0,1),
        new Triple(1,0,0),
        new Triple(1,0,1),

        new Triple(0,1,0),
        new Triple(0,1,1),
        new Triple(1,1,0),
        new Triple(1,1,1)
    );

    protected static final int _000 = 0;
    protected static final int _001 = 1;
    protected static final int _100 = 2;
    protected static final int _101 = 3;
    protected static final int _010 = 4;
    protected static final int _011 = 5;
    protected static final int _110 = 6;
    protected static final int _111 = 7;

    public static sealed interface Lerper extends Function<Double, Double> permits Identity, SmoothStep {}  

    protected static final class Identity implements Lerper {
        @Override
        public Double apply(Double t) {return t;}
    }

    protected static final class SmoothStep implements Lerper {
        @Override
        public Double apply(Double t) { return smoothStep(t); }
    }

    /**
     * Basic linear interpolation between cell corner noise values.
     */
    public static final Lerper basic = new Identity();

    /**
     * Smooth linear interpolation between cell corner noise values.
     */
    public static final Lerper smooth = new SmoothStep();

    /**
     * Pseudo random value noise.
     *
     * @param x
     *   The x component of the sample point.
     * @param y
     *   The y component of the sample point.
     * @param z
     *   The z component of the sample point.
     * @param lerper
     *   The linear interpolation method between the cell noise values and the sample point.
     * @return
     *   A pseudorandom value between zero and one derived from the sample location.
     */
    public static double noise(double x, double y, double z, Lerper lerper) {
        // figure out what cell we're in
        final int cellX = (int) Math.floor(x);
        final int cellY = (int) Math.floor(y);
        final int cellZ = (int) Math.floor(z);
        
        double[] values = new double[offsets.size()];
        for (int i = 0; i < offsets.size(); i++) {
            final var offset = offsets.get(i);
            values[i] = thrash(cellX + offset.x, cellY + offset.y, cellZ + offset.z);
        }
        // use trilinear interpolation with smooth lerp to proportionally combine the values together based on the sample point location.
        final double u = lerper.apply(x - (double) cellX);
        final double v = lerper.apply(y - (double) cellY);
        final double w = lerper.apply(z - (double) cellZ);
        final double _00 = Perlin.lerp(values[_000], values[_100], u);
        final double _01 = Perlin.lerp(values[_001], values[_101], u); 
        final double _10 = Perlin.lerp(values[_010], values[_110], u); 
        final double _11 = Perlin.lerp(values[_011], values[_111], u); 
        final double y0 = Perlin.lerp(_00, _10,v);
        final double y1 = Perlin.lerp(_01, _11, v);
        final double z0 = Perlin.lerp(y0,y1,w);
        return z0;
    }

    /**
     * Scale a noise value by a power.
     * @param x
     *   The x component of the sample point.
     * @param y
     *   The y component of the sample point.
     * @param z
     *   The z component of the sample point.
     * @param power
     *   The power amount. Leave as zero for standard results. Higher power causes noise features to become smaller and less powerful.
     * @param lerper
     *   The linear interpolation method between the cell noise values and the sample point.
     * @return
     *   A pseudorandom value between zero and one derived from the sample location.
     */
    public static double noise(double x, double y, double z, double power, Lerper lerper) {
        x = Math.pow(2, power) * x;
        y = Math.pow(2, power) * y;
        z = Math.pow(2, power) * z;
        double value = noise(x, y, z, lerper);
        double reduce = Math.pow(2, -power);
        return reduce * value;
    }

    /**
     * Remap a value to a new range.
     *
     * @param value
     *   The value to remap.
     * @param oldMin
     *   The old minimum value of the range.
     * @param oldMax
     *   The old maximum value of the range.
     * @param newMin
     *   The new minimum value of the range.
     * @param newMax
     *   The new maximum value of the range.
     * @return
     *   Value remapped into the new range of values.
     */
    public static final double remap(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return ((value - oldMin) / (oldMax - oldMin)) * (newMax - newMin) + newMin;
    }

    /**
     * Gets the noise value of a point in the integer lattice.
     *
     * @param x
     *   The x coordinate
     * @param y
     *   The y coordinate
     * @param z
     *   The z coordinate
     * @return
     *   A consistent pseudorandom noise value for a point in the integer cell lattice.
     */
    protected static double thrash(int x, int y, int z) {
        final int seed = Voronoi.hash(x, y, z); // piggyback off Voronoi hash function
        final var rng = new Random(seed); // We might need a more light weight random number generator
        return rng.nextDouble();
    }

    protected static double smoothStep(double in) {
        return Perlin.lerp(square(in), quadraticEaseOut(in), in);
    };

    protected static double quadraticEaseOut(double in) {
        return 1.0 - (1.0 - in) * (1.0 - in);
    }

    protected static double square(double in) { return in * in; }

}
