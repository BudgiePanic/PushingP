package com.BudgiePanic.rendering.scene;

import java.util.ArrayList;
import java.util.List;

import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;

/**
 * A world is a collection of objects and light sources that rays can intersect with.
 * 
 * NOTE: world construct could be improved in the future with a world builder fluent API to easily add new lights and shapes to a world.
 * 
 * @author BudgiePanic
 */
public class World {
    
    /**
     * The shapes in the scene.
     * 
     * NOTE: in the future, sphere may need to be abstracted behind a Shape interface
     */
    protected List<Sphere> shapes;
    /**
     * The lights in the scene.
     * 
     * NOTE: in the future, PointLight may need to be abtracted behind a Light interface.
     */
    protected List<PointLight> lights;

    public World() {
        this.shapes = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    /**
     * Get the shapes in this world that rays can interect with.
     *
     * @return
     *   A list of shapes in the world.
     */
    public List<Sphere> getShapes() {
        return this.shapes;
    }

    /**
     * Get the lights in the world.
     * 
     * @return
     *   A list of point lights in the world.
     */
    public List<PointLight> getLights() {
        return this.lights;
    }

    /**
     * Add a new shape to the world.
     * 
     * @param shape
     *   The shape to add. Cannot be null.
     */
    public void addShape(Sphere shape) {
        // precondition check, don't add null shapes
        if (shape == null) throw new IllegalArgumentException("shape cannot be null.");
        this.shapes.add(shape);
    }

    /**
     * Add a new light to the world.
     *
     * @param light
     *   The light to add. Cannot be null.
     */
    public void addLight(PointLight light) {
        // precondition checks, don't add null lights
        if (light == null) throw new IllegalArgumentException("light cannot be null.");
        this.lights.add(light);
    }

}
