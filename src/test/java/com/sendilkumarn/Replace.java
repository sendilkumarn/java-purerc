package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class Replace {

    @Test
    public void replaceTest() {
        release(someFunction(new GCObject("a")));
        collect();
        check();
    }

    private GCObject someFunction(GCObject a) {
        retain(new GCObject("b"));
        GCObject b = retain(a);
        release(b);
        b = a;
        try {
            return retain(b);
        } finally {
            release(a);
            release(b);
        }
    }
}
