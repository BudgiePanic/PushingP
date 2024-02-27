package com.BudgiePanic.rendering.io;

import static com.BudgiePanic.rendering.util.Tuple.makePoint;
import static com.BudgiePanic.rendering.util.Tuple.makeVector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.shape.SmoothTriangle;
import com.BudgiePanic.rendering.util.shape.Triangle;

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
        var verts = result.verticies();
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
        var verts = result.verticies();
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
        var verts = result.verticies();
        var triangles = result.triangles();
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
        var verts = result.verticies();
        assertEquals(2, result.groups().size());
        var group1 = result.groups().get(0);
        var group2 = result.groups().get(1);
        assertEquals("FirstGroup", group1.a());
        assertEquals("SecondGroup", group2.a());

        var group1Shapes = group1.b().getShapes();
        assertEquals(1, group1Shapes.size());
        var triangle1 = (Triangle) group1Shapes.get(0);

        var group2Shapes = group2.b().getShapes();
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
        assertEquals(2, result.getShapes().size());
        assertTrue(result.getShapes().contains(a.groups().get(0).b()));
        assertTrue(result.getShapes().contains(a.groups().get(1).b()));
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
        assertEquals(2, result.getShapes().size());
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
        var verts = data.verticies();
        var normals = data.normals();
        assertEquals(2, data.triangles().size());

        data.triangles().forEach(triangle -> assertTrue(triangle instanceof SmoothTriangle));

        var t1 = (SmoothTriangle) data.triangles().get(0);
        var t2 = data.triangles().get(1);

        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));

        assertEquals(verts.get(1), t1.p1());
        assertEquals(verts.get(2), t1.p2());
        assertEquals(verts.get(3), t1.p3());

        assertEquals(normals.get(1), t1.normal1());
        assertEquals(normals.get(2), t1.normal2());
        assertEquals(normals.get(3), t1.normal3());
    }
}
