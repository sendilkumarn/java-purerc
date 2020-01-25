package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class Locals {
    @Test
    public void localsTest() {
        release(someFunction(new GCObject("a")));
        collect();
        check();
    }

    private GCObject someFunction(GCObject a) {
        GCObject b = retain(a);
        GCObject c = retain(b);

        try {
            return retain(b);
        } finally {
            release(a);
            release(b);
            release(c);
        }
    }


}
