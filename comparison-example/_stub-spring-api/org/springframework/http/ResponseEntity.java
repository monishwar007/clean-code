package org.springframework.http;

public class ResponseEntity<T> {
    private final T body;
    private final int statusCode;

    private ResponseEntity(T body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public static <T> ResponseEntity<T> ok(T body) {
        return new ResponseEntity<>(body, 200);
    }

    public static BodyBuilder status(int code) {
        return new BodyBuilder(code);
    }

    public static BodyBuilder status(HttpStatus status) {
        return new BodyBuilder(status.value());
    }

    public T getBody() { return body; }
    public int getStatusCodeValue() { return statusCode; }

    public static class BodyBuilder {
        private final int code;
        BodyBuilder(int code) { this.code = code; }
        public <T> ResponseEntity<T> body(T body) { return new ResponseEntity<>(body, code); }
        public ResponseEntity<Void> build() { return new ResponseEntity<>(null, code); }
    }
}
