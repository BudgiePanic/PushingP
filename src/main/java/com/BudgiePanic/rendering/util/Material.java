package com.BudgiePanic.rendering.util;

/**
 * Stores information needed to light an object in a scene.
 * Phong lighting model information container.
 * 
 * @author BudgiePanic
 */
public record Material(Color color, float ambient, float diffuse, float specular, float shininess) {
    /**
     * Create a new default material instance.
     * @return
     *   A new instance of the default material.
     */
    public static Material defaultMaterial() {
        return new Material(Colors.white, 0.1f, 0.9f, 0.9f, 200f);
    }
}
