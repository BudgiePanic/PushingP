package com.BudgiePanic.rendering.toy;

import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Cast rays at a sphere and see which ones intersect. Rays that intersect will have their pixel colored.
 * Rays that miss the sphere will be colored black.
 * 
 * @author BudgiePanic
 */
public class Shadow implements Runnable {

    private static final String fileName = "shadow.ppm";
    final int imgHeight = 500, imgWidth = 500;
    final Canvas canvas; 

    public Shadow() {
        this.canvas = new ArrayCanvas(imgWidth, imgHeight); 
    }

    /**
     * Iterate over the canvas with a sphere.
     * @param canvas
     * @param sphere
     * @param color
     */
    private void operate(Canvas canvas, Sphere sphere, Color color) {
        var rayOrigin = Tuple.makePoint(0, 0, -5);
        var wallDepth = 10f;
        var wallWidth = 10f;
        var pixelSize = wallWidth / imgWidth;
        var halfWallWidth = 0.5f * wallWidth;
        for (int row = 0; row < imgHeight; row++) {
            var pixelYInWorldSpace = halfWallWidth - pixelSize * row;
            for (int column = 0; column < imgWidth; column++) {
                var pixelXInWorldSpace = -halfWallWidth + pixelSize * column;
                var pixelInWorldSpace = Tuple.makePoint(pixelXInWorldSpace, pixelYInWorldSpace, wallDepth);
                var ray = new Ray(rayOrigin, pixelInWorldSpace.subtract(rayOrigin).normalize());
                var intersections = sphere.intersect(ray);
                if (intersections.isPresent()) {
                    var hit = Intersection.Hit(intersections.get());
                    if (hit.isPresent()) {
                        canvas.writePixel(column, row, color);
                    }
                }
            }
        }
    }

	@Override
	public void run() {
        System.out.println("Running shadow toy.");
        operate(canvas, Sphere.defaultSphere(), Colors.green);
        System.out.println("Processed default sphere.");
        operate(canvas, new Sphere(Transforms.identity().scale(0.9f, 0.5f, 0.9f).assemble()), Colors.blue);
        System.out.println("Processed scaled sphere.");
        operate(canvas, new Sphere(Transforms.identity().scale(0.5f, 1.2f, 0.2f).shear(1f,0f,0f,0f,0f,0f).assemble()), Colors.white);
        System.out.println("Processed sheared sphere.");
        operate(canvas, new Sphere(Transforms.identity().scale(0.5f, 0.8f, 0.8f).rotateZ(0.785398f/*(0.25 * PI)*/).assemble()), Colors.red);
        System.out.println("Processed rotated sphere.");
        BaseDemo.saveImageToFile(canvas, fileName);
	}
    
}
