package ru.ifmo.genericsbasics;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashTableTest {
    private static final int NUMBER_OF_INVOCATIONS = 1_000_000;

    private final Random random = new Random();
    private final Supplier<Integer> defaultIntValuesSupplier = () -> random.nextInt(1_000_000);

    @Test
    public void putMostly_fewKeys() {
        int numberOfKeys = 100;

        invoke(
                new HashTable<Integer, Integer>(50, 0.3f), // initialCapacity, loadFactor
                new HashMap<>(),
                new OperationPercentageProfile(90),
                new KeysProvider<>(numberOfKeys, () -> random.nextInt(numberOfKeys)),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    @Test
    public void putMostly_manyKeys() {
        int numberOfKeys = 10_000;

        invoke(
                new HashTable<Integer, Integer>(1000), // initialCapacity
                new HashMap<>(),
                new OperationPercentageProfile(90),
                new KeysProvider<>(numberOfKeys, () -> random.nextInt(numberOfKeys)),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    @Test
    public void putRemoveEqually_fewKeys() {
        int numberOfKeys = 100;

        invoke(
                new HashTable<String, String>(50, 0.3f), // initialCapacity, loadFactor
                new HashMap<>(),
                new OperationPercentageProfile(55),
                new KeysProvider<>(numberOfKeys, this::generateRandomString),
                this::generateRandomString,
                NUMBER_OF_INVOCATIONS
        );
    }

    @Test
    public void putRemoveEqually_ManyKeys() {
        int numberOfKeys = 100_000;

        invoke(
                new HashTable<String, Integer>(1000), // initialCapacity
                new HashMap<>(),
                new OperationPercentageProfile(55),
                new KeysProvider<>(numberOfKeys, this::generateRandomString),
                defaultIntValuesSupplier,
                NUMBER_OF_INVOCATIONS
        );
    }

    private <K, V> void invoke(HashTable<K, V> testInstance,
                        HashMap<K, V> controlInstance,
                        OperationPercentageProfile operationPercentageProfile,
                        KeysProvider<K> keysProvider,
                        Supplier<V> valuesSupplier) {

        for (int i = 0; i < NUMBER_OF_INVOCATIONS; i++) {
            switch (operationPercentageProfile.nextOp()) {
                case PUT: {
                    K key = keysProvider.randomKey();
                    V value = valuesSupplier.get();

                    V expectedPrevValue = controlInstance.put(key, value);
                    V actualPrevValue = testInstance.put(key, value);

                    Assert.assertEquals(expectedPrevValue, actualPrevValue);
                    Assert.assertEquals(controlInstance.size(), testInstance.size());
                    Assert.assertEquals(value, testInstance.get(key));
                }
                case REMOVE: {
                    K key = keysProvider.randomKey();

                    V expectedPrevValue = controlInstance.remove(key);
                    V actualPrevValue = testInstance.remove(key);

                    Assert.assertEquals(expectedPrevValue, actualPrevValue);
                    Assert.assertNull(testInstance.get(key));
                    Assert.assertEquals(controlInstance.size(), testInstance.size());
                }
            }
        }
    }

    private static class OperationPercentageProfile {
        private final float putPercentage;
        private final Random random = new Random();

        public OperationPercentageProfile(int putPercentage) {
            this.putPercentage = putPercentage / 100.0f;
        }

        Operation nextOp() {
            return random.nextDouble() < putPercentage ? Operation.PUT : Operation.REMOVE;
        }
    }

    private static class KeysProvider<K> {
        private final List<K> keys;
        private final Random random = new Random();

        public KeysProvider(int numberOfKeys, Supplier<K> keyGenerator) {
            this.keys = Stream.generate(keyGenerator)
                    .limit(numberOfKeys)
                    .collect(Collectors.toList());
        }

        K randomKey() {
            return keys.get(random.nextInt(keys.size()));
        }
    }

    private enum Operation {
        PUT, REMOVE
    }

    private String generateRandomString() {
        int length = random.nextInt(15);
        return random.ints(length, 0, Character.MAX_VALUE)
                .mapToObj(code -> Character.toString((char) code))
                .collect(Collectors.joining());
    }
}
