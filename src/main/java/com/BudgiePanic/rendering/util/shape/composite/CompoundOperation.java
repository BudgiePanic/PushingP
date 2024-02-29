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
    public static final CompoundOperation intersect = (ilh, il, ir) -> { return false; };

    /**
     * Difference keeps intersections that are not exclusively inside the right object (left object + not right object)
     */
    public static final CompoundOperation difference = (ilh, il, ir) -> {return false; };
}
