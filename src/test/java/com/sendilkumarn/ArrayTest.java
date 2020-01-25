package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class ArrayTest {
    @Test
    public void arrayTest() {

        GCObject array = new GCObject("array");
        array.add(new GCObject("1"));
        array.add(new GCObject("2"));
        array.add(new GCObject("3"));

        release(someFunction(retain(array)));

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
