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
    
    public static final CompoundOperation intersect = (ilh, il, ir) -> { return false; };
}
