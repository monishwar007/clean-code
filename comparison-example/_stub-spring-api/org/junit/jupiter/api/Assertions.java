package org.junit.jupiter.api;
import org.junit.jupiter.api.function.Executable;
import java.util.Objects;

public class Assertions {
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "expected <" + expected + "> but was <" + actual + ">");
    }
    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message);
        }
    }
    public static void assertNotNull(Object actual) {
        if (actual == null) throw new AssertionError("expected non-null value");
    }
    public static void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError("expected condition to be true");
    }
    public static void assertFalse(boolean condition) {
        if (condition) throw new AssertionError("expected condition to be false");
    }
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                return expectedType.cast(t);
            }
            throw new AssertionError("expected " + expectedType.getName() + " but got " + t.getClass().getName(), t);
        }
        throw new AssertionError("expected " + expectedType.getName() + " to be thrown, but nothing was thrown");
    }
}
