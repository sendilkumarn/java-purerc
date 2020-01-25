package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class SimpleTest {
    @Test
    public void simpleTest() {
        release(someFunction(retain(new GCObject("1"))));
        collect();
        check();
    }

    private static GCObject someFunction(GCObject a) {
        try {
            return retain(a);
        } finally {
            release(a);
        }
    }
}
