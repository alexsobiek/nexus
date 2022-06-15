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
        Map<Key, Integer> map = new HashMap<>();
        map.put(new Key(1.0D, "test"), 5);

        Optional<Integer> opt = CollectionUtil.findByKey(map, k -> k.val1 == 1.0D && k.val2.equals("test"));
        assertTrue(opt.isPresent());
        assertEquals(opt.get(), 5);
    }

    static class Key {
        private final double val1;
        private final String val2;

        public Key(double val1, String val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }
}
