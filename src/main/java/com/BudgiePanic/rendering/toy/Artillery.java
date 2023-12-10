package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.util.Tuple;

/**
 * Runs a simple projectile simulation.
 * Writes the projectiles trajectory to a canvas, which is then saved to a PPM
 * file for viewing.
 * 
 * @author BudgiePanic
 */
public final class Artillery implements Runnable{

    private Tuple proj;
    private Tuple vel;
    private Tuple gravity;
    private Tuple wind;

    public Artillery(Tuple projectilePosition, Tuple projectileVelocity, Tuple gravity, Tuple wind) {
        this.proj = projectilePosition;
        this.vel = projectileVelocity;
        this.gravity = gravity;
        this.wind = wind;
    }

    /**
     * Runs the physics simulation for one iteration.
     * 
     * @param delta
     *     The amount of time since the last tick.
     */
    private void tick(float delta) {
        proj = proj.add(vel.multiply(delta));
        vel = vel.add(gravity).add(wind);
    }

    @Override
    public void run() {
        while (proj.y > 0f) {
            tick(0.1f);
        }
    }  



}