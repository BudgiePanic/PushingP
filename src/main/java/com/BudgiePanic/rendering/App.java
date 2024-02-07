package com.BudgiePanic.rendering;

import com.BudgiePanic.rendering.toy.Artillery;
import com.BudgiePanic.rendering.toy.CameraDemo;
import com.BudgiePanic.rendering.toy.Clock;
import com.BudgiePanic.rendering.toy.DrawSphere;
import com.BudgiePanic.rendering.toy.PatternToy;
import com.BudgiePanic.rendering.toy.PlaneDemo;
import com.BudgiePanic.rendering.toy.Shadow;
import com.BudgiePanic.rendering.toy.ShadowDemo;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Hello world!
 *
 */
public class App 
{
    final static String flagArtillery = "-arty";
    final static String flagClock = "-clock";
    final static String flagShadow = "-shadow";
    final static String flagSphereDraw = "-sphere";
    final static String flagCamera = "-camera";
    final static String flagShadowDemo = "-shadowDemo";
    final static String flagPlaneDemo = "-plane";
    final static String flagPatternDemo = "-pattern";

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        if (args.length > 0) {
            if(args[0].equals(flagArtillery)) {
                System.out.println("Running artillery toy.");
                new Artillery(
                    Tuple.makePoint(0f, 1f, 0f),
                    Tuple.makeVector((0.1f) * 100f,  (0.1f) * 100f, 0f),
                    Tuple.makeVector(0f, (0.1f) * -9.8f, 0f),
                    Tuple.makeVector(1f, 0f, 0f))
                    .run();
            }
            if (args[0].equals(flagClock)) {
                System.out.println("Running clock toy.");
                new Clock().run();
            }
            if (args[0].equals(flagShadow)) {
                System.out.println("Running shadow toy.");
                new Shadow().run();
            }
            if (args[0].equals(flagSphereDraw)) {
                System.out.println("Running phong sphere toy.");
                new DrawSphere().run();
            }
            if (args[0].equals(flagCamera)) {
                System.out.println("Running camera toy.");
                new CameraDemo().run();
            }
            if (args[0].equals(flagShadowDemo)) {
                System.out.println("running shadow demo toy.");
                new ShadowDemo().run();
            }
            if (args[0].equals(flagPlaneDemo)) {
                System.out.println("running plane demo toy.");
                new PlaneDemo().run();
            }
            if (args[0].equals(flagPatternDemo)) {
                System.out.println("running pattern toy.");
                new PatternToy().run();
            }
        }
    }
}
