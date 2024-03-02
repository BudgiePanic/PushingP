package com.BudgiePanic.rendering.util.shape.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.intersect;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.difference;

import java.util.List;

import org.junit.jupiter.api.Test;

public class CompoundOperationTest {

    static final List<List<Boolean>> inputOptions = List.of(
        List.of(true, true, true),
        List.of(true, true, false),
        List.of(true, false, true),
        List.of(true, false, false),
        List.of(false, true, true),
        List.of(false, true, false),
        List.of(false, false, true),
        List.of(false, false, false)
    );

    @Test // TODO refactor, extract common test boiler plate. help wanted.
    void testUnionCompoundOperation() {
        var expectedResults = List.of(false, true, false, true, false, false, true, true );
        var iterTest = inputOptions.iterator();
        var iterResult = expectedResults.iterator();
        while (iterTest.hasNext() && iterResult.hasNext()) {
            var test = iterTest.next();
            var expected = iterResult.next();
            var result = union.isIntersectionValid(test.get(0), test.get(1), test.get(2));
            assertEquals(expected, result, test.toString());
        }
    }

    @Test
    void testIntersectCompoundOperation() {
        var expectedResults = List.of(true, false, true, false, true, true, false, false);
        var iterTest = inputOptions.iterator();
        var iterResult = expectedResults.iterator();
        while (iterTest.hasNext() && iterResult.hasNext()) {
            var test = iterTest.next();
            var expected = iterResult.next();
            var result = intersect.isIntersectionValid(test.get(0), test.get(1), test.get(2));
            assertEquals(expected, result, test.toString());
        }
    }

    @Test
    void testDifferenceCompoundOperation() {
        var expectedResults = List.of(false, true, false, true, true, true, false, false);
        var iterTest = inputOptions.iterator();
        var iterResult = expectedResults.iterator();
        while (iterTest.hasNext() && iterResult.hasNext()) {
            var test = iterTest.next();
            var expected = iterResult.next();
            var result = difference.isIntersectionValid(test.get(0), test.get(1), test.get(2));
            assertEquals(expected, result, test.toString());
        }
    }

}
