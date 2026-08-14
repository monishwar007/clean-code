package org.mockito;
import org.mockito.stubbing.Answer;
public class OngoingStubbing<T> {
    public OngoingStubbing<T> thenReturn(T value) { return this; }
    public OngoingStubbing<T> thenThrow(RuntimeException e) { return this; }
    public OngoingStubbing<T> thenAnswer(Answer<?> answer) { return this; }
}
