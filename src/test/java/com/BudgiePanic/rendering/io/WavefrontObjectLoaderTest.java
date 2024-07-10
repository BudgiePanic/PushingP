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
package com.BudgiePanic.rendering.io;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.shape.SmoothTriangle;
import com.BudgiePanic.rendering.util.shape.Triangle;

/**
 * Tests for verifying the functionality of the object file importer.
 */
public class WavefrontObjectLoaderTest {

    @Test
    void testParseJunkObject() {
        var lines = List.of(
            "gee well this clearly shows",
            "visually something that is not what we would",
            "expect to find in a wave",
            "front object file now",
            "is it?"
        );
        var result = WavefrontObjectLoader.parseObj(lines);
        assertEquals(5, result.linesSkipped());
    }

    @Test
    void testParseVertexData() {
        // Should? note that the lists are 1 indexed, position 0 contains a dummy?
        var lines = List.of(
            "v -1 1 0",
            "v -1.0000 0.5000 0.0000",
            "v 1 0 0",
            "v 1 1 0"
        );
        var result = WavefrontObjectLoader.parseObj(lines);
        var verts = result.vertices();
        assertEquals(5, verts.size());
        assertEquals(null, verts.getFirst());
        assertEquals(makePoint(-1, 1, 0), verts.get(1));
        assertEquals(makePoint(-1, 0.5f, 0), verts.get(2));
        assertEquals(makePoint(1, 0, 0), verts.get(3));
        assertEquals(makePoint(1, 1, 0), verts.get(4));
    }

    @Test
    void testParseTriangleData() {
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "",
            "f 1 2 3",
            "f 1 3 4"
        );
        var result = WavefrontObjectLoader.parseObj(lines);
        var verts = result.vertices();
        var triangles = result.triangles();
        assertEquals(1, result.linesSkipped());
        assertEquals(2, triangles.size());
        assertEquals(verts.get(1), triangles.get(0).p1());
        assertEquals(verts.get(2), triangles.get(0).p2());
        assertEquals(verts.get(3), triangles.get(0).p3());
        assertEquals(verts.get(1), triangles.get(1).p1());
        assertEquals(verts.get(3), triangles.get(1).p2());
        assertEquals(verts.get(4), triangles.get(1).p3());
    }

    @Test
    void testPolygonTriangulation() {
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "v 0 2 0",
            "",
            "f 1 2 3 4 5"
        );
        var result = WavefrontObjectLoader.parseObj(lines);
        var verts = result.vertices();
        var triangles = result.triangles();
        assertTrue(result.linesSkipped() == 1);
        assertEquals(3, triangles.size());

        assertEquals(verts.get(1), triangles.get(0).p1());
        assertEquals(verts.get(2), triangles.get(0).p2());
        assertEquals(verts.get(3), triangles.get(0).p3());

        assertEquals(verts.get(1), triangles.get(1).p1());
        assertEquals(verts.get(3), triangles.get(1).p2());
        assertEquals(verts.get(4), triangles.get(1).p3());
        
        assertEquals(verts.get(1), triangles.get(2).p1());
        assertEquals(verts.get(4), triangles.get(2).p2());
        assertEquals(verts.get(5), triangles.get(2).p3());
    }

    @Test
    void testParseNamesGroups() {
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "",
            "g FirstGroup",
            "f 1 2 3",
            "g SecondGroup",
            "f 1 3 4"
        );
        var result = WavefrontObjectLoader.parseObj(lines);
        var verts = result.vertices();
        assertEquals(2, result.groups().size());
        var group1 = result.groups().get(0);
        var group2 = result.groups().get(1);
        assertEquals("FirstGroup", group1.a());
        assertEquals("SecondGroup", group2.a());

        var group1Shapes = group1.b().children();
        assertEquals(1, group1Shapes.size());
        var triangle1 = (Triangle) group1Shapes.get(0);

        var group2Shapes = group2.b().children();
        assertEquals(1, group2Shapes.size());
        var triangle2 = (Triangle) group2Shapes.get(0);

        assertEquals(verts.get(1), triangle1.p1());
        assertEquals(verts.get(2), triangle1.p2());
        assertEquals(verts.get(3), triangle1.p3());

        assertEquals(verts.get(1), triangle2.p1());
        assertEquals(verts.get(3), triangle2.p2());
        assertEquals(verts.get(4), triangle2.p3());
    }

    @Test
    void testUberGroupGeneration() {
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "",
            "g FirstGroup",
            "f 1 2 3",
            "g SecondGroup",
            "f 1 3 4"
        );
        var a = WavefrontObjectLoader.parseObj(lines);
        var result = WavefrontObjectLoader.objectToGroup(a);
        assertEquals(2, a.groups().size());
        assertEquals(2, result.children().size());
        assertTrue(result.children().contains(a.groups().get(0).b()));
        assertTrue(result.children().contains(a.groups().get(1).b()));
    }    

    @Test
    void testUberGroupGenerationNoSubGroup() {
        // loader had undefined behaviour when making uber group from obj with no subgroups
        // this test describes the desired behaviour of the loader in that situation
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "",
            "f 1 2 3",
            "f 1 3 4"
        );
        var a = WavefrontObjectLoader.parseObj(lines);
        var result = WavefrontObjectLoader.objectToGroup(a);
        assertEquals(2, result.children().size());
    }

    @Test
    void testVertexNormalParsing() {
        var lines = List.of(
            "vn 0 0 1",
            "vn 0.707 0 -0.707",
            "vn 1 2 3"
        );
        var data = WavefrontObjectLoader.parseObj(lines);
        assertEquals(4, data.normals().size());
        assertEquals(null, data.normals().get(0)); // wf obj files are 1 indexed, so the first element should be blank
        assertEquals(makeVector(0, 0, 1), data.normals().get(1));
        assertEquals(makeVector(0.707f, 0, -0.707f), data.normals().get(2));
        assertEquals(makeVector(1, 2, 3), data.normals().get(3));
    }

    @Test
    void testVertexNormalApplication() {
        // are smooth faces created when vertex normals are present in face parsing?
        var lines = List.of(
            "v 0 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "",
            "vn -1 0 0",
            "vn 1 0 0",
            "vn 0 1 0",
            "",
            "f 1//3 2//1 3//2",
            "f 1/0/3 2/102/1 3/14/2" // face (vertex index)/(texture vertex)/(vertex normal index)
        );
        var data = WavefrontObjectLoader.parseObj(lines);
        var verts = data.vertices();
        var normals = data.normals();
        assertEquals(2, data.linesSkipped());
        assertEquals(2, data.triangles().size());

        data.triangles().forEach(triangle -> assertTrue(triangle instanceof SmoothTriangle));

        var t1 = (SmoothTriangle) data.triangles().get(0);
        var t2 = data.triangles().get(1);

        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));

        assertEquals(verts.get(1), t1.p1());
        assertEquals(verts.get(2), t1.p2());
        assertEquals(verts.get(3), t1.p3());

        assertEquals(normals.get(3), t1.normal1());
        assertEquals(normals.get(1), t1.normal2());
        assertEquals(normals.get(2), t1.normal3());
    }

    @Test
    void testSmoothFacePatternMatchTest() {
        String[] passCases = {
            "1/2/3",
            "4321654/6541231/98471",
            "1//2",
            "87//654",
        };
        String[] failCases = {
            "1//",
            "2154//",
            "1/2/",
            "4/65/",
            "45/85/68/",
            "7/89/5/1",
            "1",
            "98741",
            "1/",
            "987/"
        };
        for (String test : passCases) {
            assertTrue(WavefrontObjectLoader.FaceParser.smoothFace.matcher(test).matches());
        }
        for (String test : failCases) {
            assertFalse(WavefrontObjectLoader.FaceParser.smoothFace.matcher(test).matches());
        }
    }

    @Test
    void testFlatFacePatternMatchTest() {
        String[] passCases = {
            "1",
            "123",
            "741698"
        };
        String[] failCases = {
            "02154/",
            "4.654",
            "657\\5454",
            "57641//654",
            "89541//"
        }; 
        for (String test : passCases) {
            assertTrue(WavefrontObjectLoader.FaceParser.simpleFace.matcher(test).matches());
        }
        for (String test : failCases) {
            assertFalse(WavefrontObjectLoader.FaceParser.simpleFace.matcher(test).matches());
        }
    }

    @Test
    void testSmoothTriangleTriangulation() {
        // are smooth faces created when vertex normals are present in face parsing?
        var lines = List.of(
            "v -1 1 0",
            "v -1 0 0",
            "v 1 0 0",
            "v 1 1 0",
            "v 0 2 0",
            "",
            "vn -1 0 0",
            "vn 1 0 0",
            "vn 0 1 0",
            "",
            "f 1//3 2//1 3//2 4//2 5//1"
        );
        var data = WavefrontObjectLoader.parseObj(lines);
        var verts = data.vertices();
        var normals = data.normals();
        var triangles = data.triangles();
        assertEquals(2, data.linesSkipped());

        triangles.forEach(triangle -> assertTrue(triangle instanceof SmoothTriangle));

        assertEquals(3, triangles.size());

        assertEquals(verts.get(1), triangles.get(0).p1());
        assertEquals(verts.get(2), triangles.get(0).p2());
        assertEquals(verts.get(3), triangles.get(0).p3());

        assertEquals(verts.get(1), triangles.get(1).p1());
        assertEquals(verts.get(3), triangles.get(1).p2());
        assertEquals(verts.get(4), triangles.get(1).p3());
        
        assertEquals(verts.get(1), triangles.get(2).p1());
        assertEquals(verts.get(4), triangles.get(2).p2());
        assertEquals(verts.get(5), triangles.get(2).p3());

        assertEquals(normals.get(3), ((SmoothTriangle) triangles.get(0)).normal1());
        assertEquals(normals.get(1), ((SmoothTriangle) triangles.get(0)).normal2());
        assertEquals(normals.get(2), ((SmoothTriangle) triangles.get(0)).normal3());

        assertEquals(normals.get(3), ((SmoothTriangle) triangles.get(1)).normal1());
        assertEquals(normals.get(2), ((SmoothTriangle) triangles.get(1)).normal2());
        assertEquals(normals.get(2), ((SmoothTriangle) triangles.get(1)).normal3());

        assertEquals(normals.get(3), ((SmoothTriangle) triangles.get(2)).normal1());
        assertEquals(normals.get(2), ((SmoothTriangle) triangles.get(2)).normal2());
        assertEquals(normals.get(1), ((SmoothTriangle) triangles.get(2)).normal3());
    }
}
