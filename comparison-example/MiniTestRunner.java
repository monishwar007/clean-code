import java.lang.reflect.Method;

public class MiniTestRunner {
    public static void main(String[] args) throws Exception {
        int pass = 0, fail = 0;
        for (String className : args) {
            Class<?> clazz = Class.forName(className);
            var ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object instance = ctor.newInstance();
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                    m.setAccessible(true);
                    try {
                        m.invoke(instance);
                        System.out.println("  PASS  " + className + "#" + m.getName());
                        pass++;
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        System.out.println("  FAIL  " + className + "#" + m.getName() + " -> " + cause);
                        fail++;
                    }
                }
            }
        }
        System.out.println();
        System.out.println("Results: " + pass + " passed, " + fail + " failed");
        if (fail > 0) System.exit(1);
    }
}
