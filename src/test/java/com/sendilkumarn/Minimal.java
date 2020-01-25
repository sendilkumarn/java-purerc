package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import static com.sendilkumarn.impl.Concurrent.*;

public class Minimal {
    @Test
    public void minimalTest() {
        release(
                retain(
                        new GCObject("1")
                )
        );

        collect();
        check();
    }
}
