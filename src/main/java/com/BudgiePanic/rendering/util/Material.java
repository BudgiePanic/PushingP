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
     * Default ambient value.
     */
    public static final float defaultAmbient = 0.1f;

    /**
     * Default diffuse value.
     */
    public static final float defaultDiffuse = 0.9f;

    /**
     * Default specular value.
     */
    public static final float defaultSpecular = 0.9f;

    /**
     * Default shininess value.
     */
    public static final float defaultShininess = 200f;


    /**
     * Create a new default material instance.
     * @return
     *   A new instance of the default material.
     */
    public static Material defaultMaterial() {
        return new Material(Colors.white, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess);
    }

    /**
     * Create a new material with default properties and override color.
     *
     * @param color
     *   The material color.
     * @return
     *   A new material with default properties and overwritten color.
     */
    public static Material color(Color color) {
        return new Material(color, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess);
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
        return compute(light, position, eye, normal, false);
    }

    /**
     * Uses the Phong lighting model to compute the color of a point using the material properties.
     * 
     * This function is in the Material record because another lighting system may wish to use 
     * a lighting system other than Phong, which may neeed new material properties. 
     * 
     * @param light
     *   The light that is illuminating the position
     * @param position
     *   The point being lit
     * @param eye
     *   the position of the observer
     * @param normal
     *   surface normal at 'position'
     * @param shadow
     *   is the surface under shadow?
     * @return
     *   The color at point 'position'
     */
    public Color compute(PointLight light, Tuple position, Tuple eye, Tuple normal, boolean shadow) {
        var effective = this.color.colorMul(light.color());
        var directionToLight = light.position().subtract(position).normalize();
        var ambient = effective.multiply(this.ambient);
        var lightNormalAngle = directionToLight.dot(normal);
        if (shadow || lightNormalAngle < 0f) {
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

    /**
     * Create a copy of this material with the ambient adjusted.
     *
     * @param ambient
     *   The new ambient value
     * @return
     *   A copy of this material with the ambient valued set to ambient.
     */
    public Material setAmbient(float ambient) {
        return new Material(this.color(), ambient, this.diffuse(), this.specular(), this.shininess());
    }

    /**
     * Create a copy of this material with the diffuse adjusted.
     *
     * @param diffuse
     *   The new diffuse value
     * @return
     *   A copy of this material with the diffuse valued set to diffuse.
     */
    public Material setDiffuse(float diffuse) {
        return new Material(this.color(), this.ambient(), diffuse, this.specular(), this.shininess());
    }

    /**
     * Create a copy of this material with the specular adjusted.
     *
     * @param specular
     *   The new specular value
     * @return
     *   A copy of this material with the specular valued set to specular.
     */
    public Material setSpecular(float specular) {
        return new Material(this.color(), this.ambient(), this.diffuse(), specular, this.shininess());
    }

    /**
     * Create a copy of this material with the shininess adjusted.
     *
     * @param shininess
     *   The new shininess value
     * @return
     *   A copy of this material with the shininess valued set to shininess.
     */
    public Material setShininess(float shininess) {
        return new Material(this.color(), this.ambient(), this.diffuse(), this.specular(), shininess);
    }

    /**
     * Create a copy of this material with the color adjusted.
     *
     * @param color
     *   The new color value
     * @return
     *   A copy of this material with the color valued set to color.
     */
    public Material setColor(Color color) {
        return new Material(color, this.ambient(), this.diffuse(), this.specular(), this.shininess());
    }
}
