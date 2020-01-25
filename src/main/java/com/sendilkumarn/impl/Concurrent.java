package com.sendilkumarn.impl;

import com.sendilkumarn.domain.Color;
import com.sendilkumarn.domain.GCObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Concurrent {

    public static ArrayList<GCObject> root = new ArrayList<>();
    public static ArrayList<ArrayList<GCObject>> cycleBuffer = new ArrayList<>();
    public static int count = 0;

    public static void increment(GCObject s) {
        System.out.println("Incrementing " + s.toString());
        int rc = s.getRc();
        s.setRc(rc+1);
        scanBlack(s);
    }

    public static void decrement(GCObject s) {
        System.out.println("Decrementing " + s.toString());
        int rc = s.getRc();
        s.setRc(rc-1);
        if(s.getRc() == 0) {
            releaseAll(s);
        } else if (!s.isAcyclic()) {
            possibleRoot(s);
        }
    }

    public static void possibleRoot(GCObject o) {
        System.out.println("Possible Root");
        scanBlack(o);

        System.out.println("Color Change to PURPLE");
        o.setColor(Color.PURPLE);

        if (!o.isBuffered()) {
            o.setBuffered(true);
            root.add(o);
        }
    }

    public static void releaseAll(GCObject o) {
        System.out.println("Releasing  " + o.toString());

        if (o.getChildren() != null) {
            o.getChildren().forEach(Concurrent::decrement);
        }
        o.setColor(Color.BLACK);

        if (!o.isBuffered()) {
            free(o);
        }
    }

    public static void free(GCObject o) {
        System.out.println("Freeing "+ o.toString());
        count = count - 1;
        o.setFreed(true);

    }

    public static void scanBlack(GCObject o) {
        System.out.println("Scanning Black");
        if(o.getColor() != Color.BLACK) {
            o.setColor(Color.BLACK);
            if(o.getChildren() != null && o.getChildren().size() != 0) {
                for(int ti = 0, tk = o.getChildren().size(); ti < tk; ti++) {
                    GCObject t = o.getChildren().get(ti);
                    scanBlack(t);
                }
            }

        }
    }

    public static void sigmaPreparation() {
        System.out.println("Sigma preparation");

        cycleBuffer.forEach(o -> {
            o.forEach(c -> {
                c.setColor(Color.RED);
                c.setCrc(c.getRc());
            });

            o.forEach(n -> n.getChildren().forEach(m -> {
                if (m.getColor() == Color.RED && m.getCrc() > 0) {
                    m.setCrc(m.getCrc() - 1);
                }
            }));

            o.forEach(n -> n.setColor(Color.ORANGE));
        });
    }

    public static void freeCycles() {
        System.out.println("Freeing cycles");
        int last = cycleBuffer.size() - 1;
        for (int i = last; i >= 0; i--) {
            ArrayList<GCObject> cList = cycleBuffer.get(i);
            if (sigmaDeltaTest(cList)) {
                freeCycle(cList);
            } else {
                refurbish(cList);
            }
        }
        cycleBuffer = new ArrayList<>();
    }

    public static void refurbish(ArrayList<GCObject> cList) {
        System.out.println("Refurbishing");
        boolean first = true;
        int ni = 0;
        int nk = cList.size();

        while(ni < nk) {
            GCObject n = cList.get(ni);
            if ((first && n.getColor() == Color.ORANGE) || n.getColor() == Color.PURPLE) {
                n.setColor(Color.PURPLE);
                root.add(n);
            } else {
                n.setColor(Color.BLACK);
                n.setBuffered(false);
            }

            first = false;
            ni++;
        }
    }

    public static void freeCycle(ArrayList<GCObject> cList) {
        System.out.println("Free Cycle "+ cList.toString());
        for (GCObject n: cList) {
            n.setColor(Color.RED);
            System.out.println("changing color " + n.getColor());
        }
        cList.forEach(Concurrent::free);
    }

    public static boolean sigmaDeltaTest(ArrayList<GCObject> cList) {
        int externRC = 0;
        for (GCObject n: cList) {

            if (n.getColor() != Color.ORANGE) {
                return false;
            }
            externRC = externRC + n.getCrc();
        }
        return externRC == 0;
    }

    public static void findCycles() {
        System.out.println("find Cycles");
        markRoots();
        scanRoots();
        collectRoots();
    }

    public static void collectRoots() {
        System.out.println("Collect Roots");
        int si = 0;
        int sk = root.size();

        while(si < sk) {
            GCObject s = root.get(si);
            if (s.getColor() == Color.WHITE) {
                ArrayList<GCObject> currentCycle = new ArrayList<>();
                ArrayList<GCObject> gc = collectWhite(s, currentCycle);
                cycleBuffer.add(gc);
            } else {
                s.setBuffered(false);
            }
            si++;
        }
        root = new ArrayList<>();
    }

    public static ArrayList<GCObject> collectWhite(GCObject s, ArrayList<GCObject> currentCycle) {
        System.out.println("Collecting white");
        if (s.getColor() == Color.WHITE) {
            s.setColor(Color.ORANGE);
            s.setBuffered(true);
            currentCycle.add(s);

            s.getChildren().forEach(c -> collectWhite(c, currentCycle));
        }
        return currentCycle;
    }

    public static void scanRoots() {
        System.out.println("Scanning Roots");
        int si = 0;
        int sk = root.size();
        while(si < sk) {
            GCObject s = root.get(si);
            scan(s);
            si++;
        }
    }

    public static void scan(GCObject s) {
        System.out.println("Scanning");
        if (s.getColor()  == Color.GRAY) {
            if(s.getCrc() > 0) {
                scanBlack(s);
            } else {
                s.setColor(Color.WHITE);
                s.getChildren().forEach(Concurrent::scan);
            }
        }

    }

    public static void markRoots() {
        System.out.println("marking roots");
        int sn = 0;

        for(int si = 0;  si < root.size(); si++) {
            GCObject s = root.get(si);
            if (s.getColor() == Color.PURPLE && s.getRc() > 0) {
                markGray(s);
                root.set(sn++, s);
            } else {
                s.setBuffered(false);
                if(s.getRc() == 0) {
                    free(s);
                }
            }
        }

        ArrayList<GCObject> someList = new ArrayList<>();
        for(int i = 0; i < sn; i++) {
            someList.add(root.get(i));
        }
        root = someList;
    }

    public static void markGray(GCObject s) {
        System.out.println("Marking Gray");
        if (s.getColor() != Color.GRAY) {
            s.setColor(Color.GRAY);
            System.out.println("Color Change to Gray");
            s.setCrc(s.getRc());

            s.getChildren().forEach(o -> {
                markGray(o);
                if (o.getCrc() > 0) {
                    o.setCrc(o.getCrc() - 1);
                }
            });
        }
    }

    public static GCObject retain(GCObject s) {
        if (s != null) {
            increment(s);
        }
        return s;
    }

    public static GCObject release(GCObject s) {
        if (s != null) {
            decrement(s);
        }
        return s;
    }

    public static void collect() {
        collectCycles();
        collectCycles();
    }

    public static void check() {
        if(count != 0) { throw new Error("leaking "+ count + " error"); }
    }

    private static void collectCycles() {
        System.out.println("collectCycles");
        freeCycles();
        findCycles();
        sigmaPreparation();
    }


    public static boolean checkAlive(GCObject gcObject, HashMap<GCObject, Boolean> map) {
        if(map.get(gcObject) == Boolean.TRUE) {
            return false;
        }
        map.put(gcObject, Boolean.TRUE);
        if (gcObject.isFreed() || Concurrent.count == 0) {
            throw new Error("This is wrong, the object should be alive");
        }

        gcObject.getChildren().forEach(o -> checkAlive(o, map));
        return true;
    }

    /*public static boolean checkDead(GCObject gcObject, HashMap<GCObject, Boolean> map) {
        if(map.get(gcObject) == Boolean.TRUE) {
            return false;
        }
        map.put(gcObject, Boolean.TRUE);
        if (!gcObject.isFreed()) {
            throw new Error("This is wrong, the object should be died");
        }

        gcObject.getChildren().forEach(o -> checkDead(o, map));
        return true;
    }*/
}
