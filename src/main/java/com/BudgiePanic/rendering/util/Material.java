package com.BudgiePanic.rendering.util;

import com.BudgiePanic.rendering.util.light.PointLight;

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


    /**
     * Uses the Phong lighting model to compute the color of a point using the material properties.
     * 
     * This function is in the Material record because another lighting system may wish to use 
     * a lighting system other than Phong, which may neeed new material properties. 
     * 
     * NOTE: it may be beneficial to add a lightmodel interface abstraction, which material can implement
     *       to allow other lighting models to be hot swapped into a scene.
     * 
     * @param light
     *   The scene light
     * @param position
     *   The point being lit
     * @param eye
     *   The camera look vector
     * @param normal
     *   The normal of the surface at the position
     * @return
     *   The color at point 'position'
     */
    public Color compute(PointLight light, Tuple position, Tuple eye, Tuple normal) {
        var effective = this.color.colorMul(light.color());
        var directionToLight = light.position().subtract(position).normalize();
        var ambient = effective.multiply(this.ambient);
        var lightNormalAngle = directionToLight.dot(normal);
        if (lightNormalAngle < 0f) {
            return ambient;
        }
        Color diffuse = effective.multiply(this.diffuse).multiply(lightNormalAngle);
        var reflection = directionToLight.negate().reflect(normal);
        var eyeReflectAngle = reflection.dot(eye);
        if (eyeReflectAngle < 0f) {
            return diffuse.add(ambient);
        }
        var factor = (float)Math.pow(eyeReflectAngle, shininess);
        var specular = light.color().multiply(this.specular).multiply(factor);
        return ambient.add(diffuse).add(specular);
    }
}
