package com.BudgiePanic.rendering.util.shape.composite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.union;
import static com.BudgiePanic.rendering.util.shape.composite.CompoundOperation.intersect;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Pair;

public class CompoundOperationTest {

    @Test // TODO refactor, extract common test boiler plate. help wanted.
    void testCompoundOperations(Pair<CompoundOperation, List<List<Boolean>>> testParameters) {
        var tests = List.of(
            List.of(true, true, true, false),
            List.of(true, true, false, true),
            List.of(true, false, true, false),
            List.of(true, false, false, true),
            List.of(false, true, true, false),
            List.of(false, true, false, false),
            List.of(false, false, true, true),
            List.of(false, false, false, true)
        );
        tests.forEach(test -> {
            var result = union.isIntersectionValid(test.get(0), test.get(1), test.get(2));
            var expected = test.get(3);
            assertEquals(expected, result, test.toString());
        });
    }


}
