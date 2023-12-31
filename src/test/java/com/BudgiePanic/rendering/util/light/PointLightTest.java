package com.BudgiePanic.rendering.util.light;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Color;
import com.BudgiePanic.rendering.util.Colors;
import com.BudgiePanic.rendering.util.Tuple;

public class PointLightTest {
    
    @Test
    void lightPropertiesTest() {
        assertDoesNotThrow(() -> {
            var intensity = new Color(1, 1, 1);
            var position = Tuple.makePoint();
            var light = new PointLight(position, intensity);
            assertEquals(Colors.white, light.color());
            assertEquals(Tuple.makePoint(), light.position());
        });
    }

}
