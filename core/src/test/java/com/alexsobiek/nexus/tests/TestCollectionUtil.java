package com.alexsobiek.nexus.tests;

import com.alexsobiek.nexus.util.CollectionUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Test Collection Util")
public class TestCollectionUtil {

    /**
     * Tests {@link CollectionUtil#findByKey(Map, Predicate)}.
     * Also tests {@link CollectionUtil#find(Collection, Predicate)}
     */
    @Test
    public void testFindByKey() {
        Map<SomeClass, Integer> map = new HashMap<>();
        map.put(new SomeClass(1.0D, "test"), 5);

        Optional<Integer> opt = CollectionUtil.findByKey(map, k -> k.val1 == 1.0D && k.val2.equals("test"));
        assertTrue(opt.isPresent());
        assertEquals(opt.get(), 5);
    }

    @Test
    public void testFindByValue() {
        Map<Integer, SomeClass> map = new HashMap<>();
        map.put(5, new SomeClass(1.0D, "test"));

        Optional<SomeClass> opt = CollectionUtil.findByValue(map, v -> v.val1 == 1.0D && v.val2.equals("test"));
        assertTrue(opt.isPresent());
        assertEquals(opt.get().val1, 1.0D);
        assertEquals(opt.get().val2, "test");
    }

    static class SomeClass {
        private final double val1;
        private final String val2;

        public SomeClass(double val1, String val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }
}
