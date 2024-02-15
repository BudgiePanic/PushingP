package com.BudgiePanic.rendering.util;

/**
 * Two items stored together.
 * Useful for when a method needs to return two things.
 * 
 * @author BudgiePanic
 */
public record Pair<T, C>(T a, C b) {}
