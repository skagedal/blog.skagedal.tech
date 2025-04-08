package skagedal.blogdans.entry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

public class EntryCollectors {
    private EntryCollectors() {}

    public static Collector<PossibleEntry, ?, Map<String, Object>> nonNullEntriesToMap() {
        return Collector.of(
            () -> new HashMap<String, Object>(),
            (map, entry) -> {
                if (entry.value() != null) {
                    map.put(entry.key(), entry.value());
                }
            },
            (map1, map2) -> {
                map1.putAll(map2);
                return map1;
            },
            Collections::unmodifiableMap
        );
    }
}
