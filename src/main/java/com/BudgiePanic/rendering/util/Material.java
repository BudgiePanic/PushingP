package com.BudgiePanic.rendering.util;

import com.BudgiePanic.rendering.util.pattern.Pattern;
import com.BudgiePanic.rendering.util.pattern.SolidColor;

/**
 * Stores information needed to light an object in a scene.
 * Phong lighting model information container.
 * 
 * @see https://en.wikipedia.org/wiki/Phong_reflection_model
 * 
 * @author BudgiePanic
 */
public record Material(Pattern pattern, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, boolean shadow, NormalBump normalBump) {

    /**
     * Functional interface for normal vector modification.
     */
    public static interface NormalBump {
        /**
         * Modify a normal vector.
         * @param in
         *   The normal vector in local space.
         * @param point
         *   The local space point on the shape's surface that was sampled for a normal vector. 
         * @return
         *   The modified normal vector in local space.
         */
        Tuple apply(Tuple in, Tuple point);
        /**
         * The identity, no normal vector modification.
         */
        public static final NormalBump identity = (in, point) -> { return in; };
    }

    /**
     * Default ambient value.
     */
    public static final double defaultAmbient = 0.1;

    /**
     * Default diffuse value.
     */
    public static final double defaultDiffuse = 0.9;

    /**
     * Default specular value.
     */
    public static final double defaultSpecular = 0.9;

    /**
     * Default shininess value.
     */
    public static final double defaultShininess = 200;

    /**
     * Default pattern value.
     */
    public static final Pattern defaultPattern = new SolidColor(Colors.white);

    /**
     * Default reflectivity amount.
     */
    public static final double defaultReflectivity = 0;

    /**
     * Default amount of transparency.
     */
    public static final double defaultTransparency = 0;

    /**
     * The degree to which light bends when entering a transparent material.
     */
    public static final double defaultRefractiveIndex = 1;

    /**
     * Whether shapes with the material cast shadows or not.
     */
    public static final boolean defaultShadowCast = true;

    /**
     * Material surface normal modification.
     */
    public static final NormalBump defaultNormalBump = NormalBump.identity;

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
     * @param normalBump
     */
    public Material(Pattern pattern, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, NormalBump normalBump) {
        this(pattern, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, normalBump);
    }

    /**
     * Convience constructor to create a material who's shapes cast shadows and has default normal bumping.
     * @param pattern
     * @param ambient
     * @param diffuse
     * @param specular
     * @param shininess
     * @param reflectivity
     * @param transparency
     * @param refractiveIndex
     */
    public Material(Pattern pattern, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex) {
        this(pattern, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, defaultNormalBump);
    }

    /**
     * Convience constructor to create a material with default normal bumping.
     * @param pattern
     * @param ambient
     * @param diffuse
     * @param specular
     * @param shininess
     * @param reflectivity
     * @param transparency
     * @param refractiveIndex
     * @param shadow
     */
    public Material(Pattern pattern, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, boolean shadow) {
        this(pattern, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, shadow, defaultNormalBump);
    }

    /**
     * Convienience material constructor for raw color. Auto wraps the color in a solid color pattern. Sets shadow casting flag to true.
     * 
     * @param color
     *     The color of the material
     * @param ambient
     *     The ambient illumination of the material. keep small, 0.1 default.
     * @param diffuse
     *     The material diffuse. 0.9 by default. diffuse represents the portion of non-incident reflected light off the surface.
     * @param specular
     *     The material specular. 0.9 by default. specular represents the portion of incident reflected light off the surface.
     * @param shininess
     *     The material shininess. 200 by default. high shininess causes smaller specular highlights, corresponds to smoother surfaces.
     * @param reflectivity
     *     The material reflectiveness 0 -> nonreflective | 1 -> mirror
     * @param transparency
     *     How see through the material is 0 -> opaque | 1 -> see through
     * @param refractiveIndex
     *     How light bends as it enters/exits the material 1 -> vacuum | 1.52 -> glass 
     * @param normalBump
     *     How the normal vector of shapes using this material are modified.
     */
    public Material(Color color, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, NormalBump normalBump) {
        this(color, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, normalBump);
    } 

    /**
     * Convienience material constructor for raw color. Auto wraps the color in a solid color pattern. Sets shadow casting flag to true. Uses default normal bumping.
     * 
     * @param color
     *     The color of the material
     * @param ambient
     *     The ambient illumination of the material. keep small, 0.1 default.
     * @param diffuse
     *     The material diffuse. 0.9 by default. diffuse represents the portion of non-incident reflected light off the surface.
     * @param specular
     *     The material specular. 0.9 by default. specular represents the portion of incident reflected light off the surface.
     * @param shininess
     *     The material shininess. 200 by default. high shininess causes smaller specular highlights, corresponds to smoother surfaces.
     * @param reflectivity
     *     The material reflectiveness 0 -> nonreflective | 1 -> mirror
     * @param transparency
     *     How see through the material is 0 -> opaque | 1 -> see through
     * @param refractiveIndex
     *     How light bends as it enters/exits the material 1 -> vacuum | 1.52 -> glass 
     */
    public Material(Color color, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex) {
        this(color, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, defaultNormalBump);
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
     * @param normalBump
     *     How the normal vector of shapes using this material are modified.
     */
    public Material(Color color, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, boolean castsShadows, NormalBump normalBump) {
        this(new SolidColor(color), ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, normalBump);
    }

    /**
     * Convienience material constructor for raw color. Auto wraps the color in a solid color pattern. Uses default normal bumping.
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
    public Material(Color color, double ambient, double diffuse, double specular, double shininess, double reflectivity, double transparency, double refractiveIndex, boolean castsShadows) {
        this(color, ambient, diffuse, specular, shininess, reflectivity, transparency, refractiveIndex, defaultShadowCast, defaultNormalBump);
    }

    /**
     * Create a new default material instance.
     * @return
     *   A new instance of the default material.
     */
    public static Material defaultMaterial() {
        return new Material(defaultPattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast, defaultNormalBump);
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
        return new Material(color, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast, defaultNormalBump);
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
        return new Material(pattern, defaultAmbient, defaultDiffuse, defaultSpecular, defaultShininess, defaultReflectivity, defaultTransparency, defaultRefractiveIndex, defaultShadowCast, defaultNormalBump);
    }

    /**
     * Create a copy of this material with the ambient adjusted.
     *
     * @param ambient
     *   The new ambient value
     * @return
     *   A copy of this material with the ambient valued set to ambient.
     */
    public Material setAmbient(double ambient) {
        return new Material(this.pattern(), ambient, this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Create a copy of this material with the diffuse adjusted.
     *
     * @param diffuse
     *   The new diffuse value
     * @return
     *   A copy of this material with the diffuse valued set to diffuse.
     */
    public Material setDiffuse(double diffuse) {
        return new Material(this.pattern(), this.ambient(), diffuse, this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Create a copy of this material with the specular adjusted.
     *
     * @param specular
     *   The new specular value
     * @return
     *   A copy of this material with the specular valued set to specular.
     */
    public Material setSpecular(double specular) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), specular, this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Create a copy of this material with the shininess adjusted.
     *
     * @param shininess
     *   The new shininess value
     * @return
     *   A copy of this material with the shininess valued set to shininess.
     */
    public Material setShininess(double shininess) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), shininess, this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
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
        return new Material(color, this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
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
        return new Material(pattern, this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Change material's reflectivity property.
     * 
     * @param reflectivity
     *   The new reflectivity amount, between 0 and 1.
     * @return
     *   A copy of the material with the reflectivity changed
     */
    public Material setReflectivity(double reflectivity) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), reflectivity, this.transparency(), this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Change a material's transparency
     *
     * @param transparency
     *   The new amount of transparency.
     * @return
     *   A copy of the material with the transparency changed.
     */
    public Material setTransparency(double transparency) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), transparency, this.refractiveIndex(), this.shadow(), this.normalBump());
    }

    /**
     * Change a material's refractive index
     *
     * @param transparency
     *   The new refractive index.
     * @return
     *   A copy of the material with the refractive index changed.
     */
    public Material setRefractiveIndex(double refractiveIndex) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), refractiveIndex, this.shadow(), this.normalBump());
    }

    /**
     * Change whether shapes using this material cast shadows.
     * @param shadow
     *   Whether shapes using this material cast shadows.
     * @return
     *   A copy of this material with the shadow casting property changed.
     */
    public Material setShadow(boolean shadow) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), shadow, this.normalBump());
    }

    /**
     * Change the normal vector bumping function for this material.
     * @param normalBump
     *   The new normal bump function.
     * @return
     *   A copy of this material with the normal bumping function changed.
     */
    public Material setNormalBump(NormalBump normalBump) {
        return new Material(this.pattern(), this.ambient(), this.diffuse(), this.specular(), this.shininess(), this.reflectivity(), this.transparency(), this.refractiveIndex(), this.shadow(), normalBump);
    }
}
