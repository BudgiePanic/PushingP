package com.BudgiePanic.rendering.util;

import static org.junit.jupiter.api.Assertions.*;
import static com.BudgiePanic.rendering.util.FloatHelp.compareFloat;

import org.junit.jupiter.api.Test;

public class ColorTest {
    
    @Test
    void colorTest() {
        Color color = new Color(-0.5f, 0.4f, 1.7f);
        assertTrue(compareFloat(-0.5f, color.getRed()) == 0);
        assertTrue(compareFloat(0.4f, color.getGreen()) == 0);
        assertTrue(compareFloat(1.7f, color.getBlue()) == 0);
    }

}
