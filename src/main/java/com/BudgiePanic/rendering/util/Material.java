package com.BudgiePanic.rendering.util;

import com.BudgiePanic.rendering.util.pattern.Pattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;

/**
 * Stores information needed to light an object in a scene.
 * Phong lighting model information container.
 * 
 * @author BudgiePanic
 */
public record Material(Pattern pattern, float ambient, float diffuse, float specular, float shininess, float reflectivity, float transparency, float refractiveIndex, boolean shadow) {

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
     * Default reflectivity amount.
     */
    public static final float defaultReflectivity = 0f;

    /**
     * Default amount of transparency.
     */
    public static final float defaultTransparency = 0f;

    /**
     * The degree to which light bends when entering a transparent material.
     */
    public static final float defaultRefractiveIndex = 1f;

    /**
     * Whether shapes with the material cast shadows or not.
     */
    public static final boolean defaultShadowCast = true;

    /**
     * Convience constructor to create a material who's shapes cast shadows.
     * @param pattern
     * @param ambient
     * @param diffuse
     * @param specular
     * @param shininess
     * @param reflectivity
     * @param transparency
     * @param refractiveIndex
     */
    public Material(Pattern pattern, float ambient, float diffuse, float specular, float shininess, float reflectivity, float transparency, float refractiveIndex) {
        this(pattern, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast);
    }

    /**
     * Convienience material constructor for raw color. Auto wraps the color in a solid color pattern. Sets shadow casting flag to true.
     * 
     * TODO give these parameters better descriptions... what sorts of values are typical...
     * @param color
     *     The color of the material
     * @param ambient
     *     The ambient illumination of the material
     * @param diffuse
     *     The material diffuse
     * @param specular
     *     The material specular
     * @param shininess
     *     The material shininess
     * @param reflectivity
     *     The material reflectiveness 0 -> nonreflective | 1 -> mirror
     * @param transparency
     *     How see through the material is 0 -> opaque | 1 -> see through
     * @param refractiveIndex
     *     How light bends as it enters/exits the material 1 -> vacuum | 1.52 -> glass 
     */
    public Material(Color color, float ambient, float diffuse, float specular, float shininess, float reflectivity, float transparency, float refractiveIndex) {
        this(color, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast);
    } 

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
     * @param reflectivity
     *   The material reflectiveness 0 -> nonreflective | 1 -> mirror
     * @param transparency
     *   How see through the material is 0 -> opaque | 1 -> see through
     * @param refractiveIndex
     *   How light bends as it enters/exits the material 1 -> vacuum | 1.52 -> glass 
     * @param castsShadows
     *     Whether the shape using this material can cast shadows on other shapes.
     */
    public Material(Color color, float ambient, float diffuse, float specular, float shininess, float reflectivity, float transparency, float refractiveIndex, boolean castsShadows) {
        this(new SolidColor(color), ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast);
    }

    /**
     * Create a new default material instance.
     * @return
     *   A new instance of the default material.
     */
    public static Material defaultMaterial() {
        return new Material(defaultPattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast);
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
        return new Material(color, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast);
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
        return new Material(pattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast);
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
        return new Material(this.pattern(), ambient, this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
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
        return new Material(this.pattern(), this.ambient(), diffuse, this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
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
        return new Material(this.pattern(), this.ambient(), this.diffuse(), specular, this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
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
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), shininess, this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
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
        return new Material(color, this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
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
        return new Material(pattern, this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow());
    }

    /**
     * Change material's reflectivity property.
     * 
     * @param reflectivity
     *   The new reflectivity amount, between 0 and 1.
     * @return
     *   A copy of the material with the reflectivity changed
     */
    public Material setReflectivity(float reflectivity) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), reflectivity, this.transparency(), this.refractiveIndex(), this.shadow());
    }

    /**
     * Change a material's transparency
     *
     * @param transparency
     *   The new amount of transparency.
     * @return
     *   A copy of the material with the transparency changed.
     */
    public Material setTransparency(float transparency) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), transparency, this.refractiveIndex(), this.shadow());
    }

    /**
     * Change a material's refractive index
     *
     * @param transparency
     *   The new refractive index.
     * @return
     *   A copy of the material with the refractive index changed.
     */
    public Material setRefractiveIndex(float refractiveIndex) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), refractiveIndex, this.shadow());
    }

    /**
     * Change whether shapes using this material cast shadows.
     * @param shadow
     *   Whether shapes using this material cast shadows.
     * @return
     *   A copy of this material with the shadow casting property changed.
     */
    public Material setShadow(boolean shadow) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), shadow);
    }
}
