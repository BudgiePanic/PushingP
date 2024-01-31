package com.BudgiePanic.rendering.util;

import com.BudgiePanic.rendering.util.pattern.Pattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;

/**
 * Stores information needed to light an object in a scene.
 * Phong lighting model information container.
 * 
 * @author BudgiePanic
 */
public record Material(Pattern pattern, float ambient, float diffuse, float specular, float shininess) {

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
     * Default pattern value.
     */
    public static final Pattern defaultPattern = new SolidColor(Colors.white);

    /**
     * Convienience material constructor for raw color. Auto wraps the color in a solid color pattern.
     *
     * @param color
     *   The color of the material
     * @param ambient
     *   The ambient illumination of the material
     * @param diffuse
     *   The material diffuse
     * @param specular
     *   The material specular
     * @param shininess
     *   The material shininess
     */
    public Material(Color color, float ambient, float diffuse, float specular, float shininess) {
        this(new SolidColor(color), ambient, diffuse, specular, shininess);
    }

    /**
     * Create a new default material instance.
     * @return
     *   A new instance of the default material.
     */
    public static Material defaultMaterial() {
        return new Material(defaultPattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess);
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
     * A base material with a pattern.
     * 
     * @param pattern
     *   The pattern to associate with this material
     * @return
     *   A new material with default properties and a pattern override.
     */
    public static Material pattern(Pattern pattern) {
        return new Material(pattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess);
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
        return new Material(this.pattern(), ambient, this.diffuse(), this.specular(), this.shininess());
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
        return new Material(this.pattern(), this.ambient(), diffuse, this.specular(), this.shininess());
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
        return new Material(this.pattern(), this.ambient(), this.diffuse(), specular, this.shininess());
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
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), shininess);
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

    /**
     * Change a material's pattern by making a copy with a new pattern.
     * 
     * @param pattern
     *   The new pattern to apply to the material
     * @return
     *   A copy of the material with the pattern changed
     */
    public Material setPattern(Pattern pattern) {
        return new Material(pattern, this.ambient(), this.diffuse(), this.specular(), this.shininess());
    }
}
