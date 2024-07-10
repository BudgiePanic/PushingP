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
package com.BudgiePanic.rendering.util.shape.composite;

/**
 * Compound operations are ways of combining shapes together.
 * 
 * @author BudgiePanic
 */
public interface CompoundOperation {
 
    /**
     * Decide if an intersection is valid between two shapes given the operation rule.
     * @param isLeftHit
     * @param inLeft
     * @param inRight
     * @return
     */
    boolean isIntersectionValid(boolean isLeftHit, boolean inLeft, boolean inRight);

    /**
     * A union preserves all ray shape intersections on the exteriour of the shapes within the compound shape.
     * Rejects intersections that are inside another object
     */
    public static final CompoundOperation union = (ilh, il, ir) -> { return (ilh && !ir) || (!ilh && !il); };
    
    /**
     * Intersect keeps intersectionss that overlap both shapes
     */
    public static final CompoundOperation intersect = (ilh, il, ir) -> { return (ilh && ir) || (!ilh && il); };

    /**
     * Difference keeps intersections that are not exclusively inside the right object (left object + not right object)
     */
    public static final CompoundOperation difference = (ilh, il, ir) -> {return (ilh && !ir) || (!ilh && il); };
}
