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
 * Helper class with methods to convert between radians and degrees.
 * 
 * @author BudgiePanic
 */
public final class AngleHelp {
    
    private AngleHelp() {}

    /**
     * Convert angle in degrees to angle in radians.
     *
     * @param degrees
     *   The angle.
     * @return
     *   The angle in radians.
     */
    public final static double toRadians(double degrees) {
        // return (float) Math.toRadians(degrees);
        return (degrees / 180.0) * Math.PI;
    }

    /**
     * Convert angle in radians to angle in degrees.
     *
     * @param radians
     *   The angle.
     * @return
     *   The angle in degrees.
     */
    public final static double toDegrees(double radians) {
        // return Math.toDegrees(radians);
        return radians * (180.0 / Math.PI);
    }
}
