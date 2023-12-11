package com.BudgiePanic.rendering;

import com.BudgiePanic.rendering.toy.Artillery;
import com.BudgiePanic.rendering.util.Tuple;

/**
 * Hello world!
 *
 */
public class App 
{
    final static String flagArtillery = "-arty";

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        if (args.length > 0 && args[0].equals(flagArtillery)) {
            System.out.println("Running artillery toy.");
            new Artillery(
                Tuple.makePoint(0f, 1f, 0f),
                Tuple.makeVector((0.1f) * 100f,  (0.1f) * 100f, 0f),
                Tuple.makeVector(0f, (0.1f) * -9.8f, 0f),
                Tuple.makeVector(1f, 0f, 0f))
                .run();
        }
    }
}
