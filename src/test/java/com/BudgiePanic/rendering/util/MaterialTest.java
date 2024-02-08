package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MaterialTest {
    
    @Test
    void testMaterialProperties() {
        // this test feels kind of silly in a compiled language, I get that the book can also apply to interpreted languages where a test like this would be useful.
        assertDoesNotThrow(() -> {
            var material = new Material(new Color(1, 1, 1), 0.1f, 0.9f, 0.9f, 200.0f, 0f);
            assertEquals(Colors.white, material.pattern().colorAt(Tuple.makePoint()));
        });
    }

    @Test
    void testDefaultMaterialProperties() {
        var material = Material.defaultMaterial();
        var expected = new Material(Colors.white, 0.1f, 0.9f, 0.9f, 200f,0f);
        assertEquals(expected, material);
    }
}
