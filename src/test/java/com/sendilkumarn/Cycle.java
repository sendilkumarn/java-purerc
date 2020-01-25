package com.sendilkumarn;

import com.sendilkumarn.domain.GCObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static com.sendilkumarn.impl.Concurrent.*;

public class Cycle {
    @Test
    public void cycleTest() {
        GCObject s = new GCObject("S");
        s.add(s);
        retainRelease(s);
        collectAndCheck();
    }

    @Test
    public void testCycle1() {
        GCObject o = new GCObject("self");
        o.add(o);
        retainAndRelease(o);
        collectAndCheck();
    }

    @Test
    public void testCycle2() {
        GCObject s = new GCObject("array");
        GCObject t = new GCObject("element");
        t.add(s);
        s.add(t);
        retainAndRelease(s);
        collectAndCheck();
    }

    @Test
    public void testCycle3() {
        GCObject l1 = new GCObject("level1");
        GCObject l2 = new GCObject("level2");
        GCObject l3 = new GCObject("level3");
        GCObject l4 = new GCObject("level4");

        l1.add(l2);
        l1.add(l3);
        l1.add(l4);
        l1.add(l1);

        retainAndRelease(l1);
        collectAndCheck();
    }

    @Test
    public void testCycle4() {
        GCObject outer = new GCObject("outer");
        GCObject l1 = new GCObject("level1");
        GCObject l2 = new GCObject("level2");
        GCObject l3 = new GCObject("level3");
        GCObject l4 = new GCObject("level4");

        l1.add(l2);
        l1.add(l3);
        l1.add(l4);
        l1.add(l1);
        outer.add(l1);

        retainAndRelease(outer);
        collectAndCheck();
    }

    @Test
    public void testCycle5() {
        GCObject s = new GCObject("level0");
        GCObject t = new GCObject("level1");
        GCObject u = new GCObject("level2");
        GCObject v = new GCObject("level3");

        v.add(t);
        v.add(u);
        u.add(v);
        u.add(u);
        u.add(s);
        t.add(u);
        t.add(v);
        s.add(t);

        retainAndRelease(s);
        collectAndCheck();
    }

    @Test
    public void testCycle6() {
        GCObject outer = new GCObject("outer");
        GCObject l1 = new GCObject("level1");
        GCObject l2 = new GCObject("level2");
        GCObject inner = new GCObject("inner");

        l1.add(l2);
        l1.add(l1);
        l1.add(inner);
        outer.add(l1);

        retainAndRelease(outer);
        collectAndCheck();
    }

    @Test
    public void testCycle7() {

        GCObject s1, s2, t1, t2;

        GCObject cycle1 = new GCObject("a:outer");
        s1 = new GCObject("a:level1");
        t1 = new GCObject("a:level2");
        t1.add(s1);
        GCObject inner1 = new GCObject("a:inner");
        t1.add(inner1);
        s1.add(t1);
        cycle1.add(s1);

        GCObject cycle2 = new GCObject("b:outer");
        s2 = new GCObject("b:level1");
        t2 = new GCObject("b:level2");
        t2.add(s2);
        GCObject inner2 = new GCObject("b:inner");
        t2.add(inner2);
        s2.add(t2);
        cycle2.add(s2);

        GCObject cycle3 = new GCObject("c:level1");
        GCObject l2 = new GCObject("c:level2");
        GCObject l3 = new GCObject("c:level3");
        GCObject l4 = new GCObject("c:level4");

        l4.add(cycle3);
        l3.add(l4);
        l2.add(l3);
        cycle3.add(l2);

        cycle1.add(cycle2);
        t1.add(cycle2);
        cycle1.add(cycle3);
        cycle2.add(cycle1);

        t2.add(cycle1);
        t2.add(cycle3);

        retain(cycle1);
        assert checkAlive(cycle1, new HashMap<>());
        assert checkAlive(cycle2, new HashMap<>());
        assert checkAlive(cycle3, new HashMap<>());
        assert checkAlive(t1, new HashMap<>());
        assert checkAlive(t2, new HashMap<>());

        collect();
        collect();
        collect();


        cycle1.add(cycle2);
        t1.add(cycle2);
        cycle1.add(cycle3);
        cycle2.add(cycle1);
        t2.add(cycle1);
        t2.add(cycle3);
        collect();

        collect();
        cycle3.add(t1);
        collect();
        cycle3.add(t2);
        collect();

        collect();
        collect();
        collect();

        assert checkAlive(cycle1, new HashMap<>());
        assert checkAlive(cycle2, new HashMap<>());
        assert checkAlive(cycle3, new HashMap<>());
        assert checkAlive(t1, new HashMap<>());
        assert checkAlive(t2, new HashMap<>());

        release(cycle1);
    }


    private void retainAndRelease(GCObject o) {
        release(retainRelease(retain(o)));
    }

    private GCObject retainRelease(GCObject o) {
        try {
            return retain(o);
        } finally {
            release(o);
        }
    }

    private void collectAndCheck() {
        collect();
        check();
    }
}
