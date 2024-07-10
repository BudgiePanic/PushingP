/*
 * Copyright 2023-2024 Benjamin Sanson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
