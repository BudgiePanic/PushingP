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
package com.BudgiePanic.rendering.util;

/**
 * Static methods that help with float imprecision.
 * 
 * @author BudgiePanic
 */
public final class FloatHelp {
    
    private FloatHelp() {}
    
    /**
     * The maximum difference in floats for them to be considered the same.
     */
    public final static double epsilon = 0.0001;
    
    /**
     * checks if two floating point numbers are similar enough to each other.
     * Used instead of == to cope with floating point imprecision.
     * @param a 
     *   The first float to compare.
     * @param b
     *   The second float.
     * @return
     *    0 if the two numbers are within 0.0001 of each other.
     *    1 if a is larger than b.
     *   -1 if b is larger than a.
     */
    public static int compareFloat(double a, double b){

        final boolean aIsInfinite = Double.isInfinite(a), bIsInfinite = Double.isInfinite(b);
        if (aIsInfinite || bIsInfinite) {
            if (a < b) {
                return -1;
            } else if (a > b) {
                return 1;
            } else {
                return 0;
            }
        }

        double delta = Math.abs(a - b);
        if (delta < epsilon) return 0;
        if (a < b) return -1;
        return 1;
    }
}
