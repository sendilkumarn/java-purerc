package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class Scope {
    @Test
    public void scopeTest() {
        release(someFunction(retain(new GCObject("1"))));
        collect();
        check();
    }

    private GCObject someFunction(GCObject a) {
        GCObject c;
        GCObject b = null;

        try {
            b = retain(a);
            c = retain(b);
        } finally {
            release(b);
        }

        try {
            return retain(c);
        } finally {
            release(a);
            release(c);
        }
    }
}
