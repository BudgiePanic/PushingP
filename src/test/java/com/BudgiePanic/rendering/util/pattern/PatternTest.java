package com.BudgiePanic.rendering.util.pattern;

public class PatternTest {
    // The author of the book suggests these additional tests for checking that the pattern abstraction works
    // I don't think these tests are needed with the currect architecture due to the use of interface and default method.

    // testPattern.colorAt(point) => { return new Color(point.x, point.y, point.z) }

    // testDefaultPatternTransform
    //   assert testPattern.transform == identity matrix

    // testPatternTransformAssignment
    //    testPattern.setTransform(Translate(1,2,3))
    //    assert testPatten.transform == Translate(1,2,3)

    // testPatternObjectTransform
    //   shape sphere.setTransform(scale(2,2,2))
    //   var color = testPattern.colorAt(point(2,3,4), shape)
    //   assert color == color(1, 1.5, 2)

    // testPatternTransform
    //   shape sphere
    //   testPattern(scale(2,2,2))
    //   color = testPattern.colorAt(point(2,3,4), shape)
    //   assert color == color(1, 1.5, 2)

    // testPattenTransformObjectTransform
    //   shape sphere(scale(2,2,2))
    //   testPattern(translate(0.5, 1, 1.5))
    //   color = testPattern.colorAt(point(2.5, 3, 3.5), shape)
    //   assert color == color(0.75, 0.5, 0.25)
}
