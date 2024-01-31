package com.BudgiePanic.rendering.toy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Material;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.intersect.Intersection;
import com.BudgiePanic.rendering.util.intersect.Ray;
import com.BudgiePanic.rendering.util.light.Phong;
import com.BudgiePanic.rendering.util.light.PointLight;
import com.BudgiePanic.rendering.util.shape.Sphere;
import com.BudgiePanic.rendering.util.transform.Transforms;

public class DrawSphere implements Runnable {

    private static final String fileName = "sphere.ppm";
    final int imgHeight = 500, imgWidth = 500;
    final Canvas canvas; 

    public DrawSphere() {
        this.canvas = new ArrayCanvas(imgWidth, imgHeight); 
    }

    @Override
    public void run() {
        var sphere = new Sphere(Transforms.identity().assemble(), Material.color(new Color(1, 0.2f, 1)));
        operate(sphere);
        System.out.println("Finished casting rays 1/3");

        sphere = new Sphere(Transforms.identity().scale(0.8f, 0.6f, 1.25f).rotateY(3f).assemble(), Material.color(Colors.green));
        operate(sphere);
        System.out.println("Finished casting rays 2/3");

        sphere = new Sphere(Transforms.identity().scale(0.25f, 1.66f, 1f).rotateZ(0.5f * 3f).assemble(), Material.color(Colors.red));
        operate(sphere);
        System.out.println("Finished casting rays 3/3");

        var lines = CanvasWriter.canvasToPPMString(canvas);
        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
		System.out.println("done");
    }



    private void operate(Sphere sphere) {
        var light = new PointLight(Tuple.makePoint(-10, 10, -10), Colors.white);
        var rayOrigin = Tuple.makePoint(0, 0, -5);
        var wallDepth = 10f;
        var wallWidth = 10f;
        var pixelSize = wallWidth / imgWidth;
        var halfWallWidth = 0.5f * wallWidth;
        System.out.println("casting rays");
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
                        var point = ray.position(hit.get().a());
                        var normal = hit.get().sphere().normal(point);
                        var eye = ray.direction().negate();
                        var color = Phong.compute(hit.get().sphere().material(), light, point, eye, normal);
                        canvas.writePixel(column, row, color);
                    }
                }
            }
        }
    }
    
}
