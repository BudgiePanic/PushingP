package com.BudgiePanic.rendering;

import com.BudgiePanic.rendering.toy.Artillery;
import com.BudgiePanic.rendering.toy.Clock;
import com.BudgiePanic.rendering.toy.DrawSphere;
import com.BudgiePanic.rendering.toy.Shadow;
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
        }
    }
}
