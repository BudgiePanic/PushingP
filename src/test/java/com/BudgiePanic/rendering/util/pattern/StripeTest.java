package com.BudgiePanic.rendering.util.pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.BudgiePanic.rendering.util.Tuple.makePoint;

import org.junit.jupiter.api.Test;

import com.BudgiePanic.rendering.util.Colors;

public class StripeTest {
    
    @Test
    void testStripeYConstant() {
        var stripe = new Stripe(Colors.white, Colors.black);
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 0, 0)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 1, 0)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 2, 0)));
    }

    @Test
    void testStripeZConstant() {
        var stripe = new Stripe(Colors.white, Colors.black);
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 0, 0)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 0, 1)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(0, 0, 2)));
    }

    @Test
    void testStripeXChanging() {
        var stripe = new Stripe(Colors.white, Colors.black);
        assertEquals(Colors.white, stripe.colorAt(makePoint(0f, 0f, 0f)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(0.9f, 0f, 0f)));
        assertEquals(Colors.white, stripe.colorAt(makePoint(-1.1f, 0f, 0f)));
        assertEquals(Colors.black, stripe.colorAt(makePoint(1f, 0f, 0f)));
        assertEquals(Colors.black, stripe.colorAt(makePoint(-0.1f, 0f, 0f)));
        assertEquals(Colors.black, stripe.colorAt(makePoint(-1f, 0f, 0f)));
    }
}
