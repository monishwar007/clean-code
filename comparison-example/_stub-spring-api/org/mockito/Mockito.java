package org.mockito;

public class Mockito {
    public static <T> T mock(Class<T> classToMock) { return null; }
    public static <T> OngoingStubbing<T> when(T methodCall) { return new OngoingStubbing<>(); }
    public static <T> T any() { return null; }
    public static <T> T any(Class<T> type) { return null; }
    public static <T> T eq(T value) { return value; }
    public static <T> T verify(T mock) { return mock; }
    public static <T> T verify(T mock, VerificationMode mode) { return mock; }
    public static VerificationMode times(int n) { return new VerificationMode(); }
    public static VerificationMode never() { return new VerificationMode(); }
}
