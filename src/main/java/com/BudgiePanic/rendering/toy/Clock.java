package com.BudgiePanic.rendering.toy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.BudgiePanic.rendering.io.CanvasWriter;
import com.BudgiePanic.rendering.util.ArrayCanvas;
import com.BudgiePanic.rendering.util.Canvas;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;
import com.BudgiePanic.rendering.util.transform.Rotation;
import com.BudgiePanic.rendering.util.transform.Transforms;

/**
 * Applies matrix transforms to points to create 12 points around an origin, like an analogue clock.
 */
public class Clock implements Runnable {

    final int imgHeight = 500, imgWidth = 500;
    final Canvas canvas; 
    final String fileName = "clock.ppm";

    public Clock() {
        this.canvas = new ArrayCanvas(imgWidth, imgHeight);
    }

    @Override
    public void run() {
        final var origin = Tuple.makePoint();
        // The clock is facing towards the +ve y axis
        var twelveOclock = origin.add(0, 0, 1);
        List<Tuple> pointsToDraw = new ArrayList<>();
        pointsToDraw.add(twelveOclock);

        final int numbPoints = 12;
        final float radiansInCircle = (float) (Math.PI * 2.0);
        final float rotateAmountRadians = radiansInCircle / (float) numbPoints;
        final var rotate = Rotation.buildYRotationMatrix(rotateAmountRadians);

        // 12 points rotated about the origin
        var point = twelveOclock;
        for (int i = 0; i < numbPoints; i++) {
            point = rotate.multiply(point);
            pointsToDraw.add(point);
        }

        // Move and scale the points to fit inside the canvas
        final var scaleAndMove = Transforms.identity().
            scale(((3f / 8f) * imgWidth), 1f, ((3f / 8f) * imgHeight)).
                translate((imgWidth / 2f), 0, (imgHeight / 2f)).
                    assemble();
        var scaledPoints = pointsToDraw.stream().map((Tuple p)->{
            return scaleAndMove.multiply(p);
        }).collect(Collectors.toList());

        // Write the points to the canvas
        scaledPoints.forEach((Tuple p) ->  {
            this.canvas.writePixel((int) p.x, (int) p.z, Colors.white);
        });

        // Write the canvas to file.
        var lines = CanvasWriter.canvasToPPMString(canvas);
        File file = new File(System.getProperty("user.dir"), fileName);
        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println(e);
        }
        System.out.println("done");
    }
    
}
