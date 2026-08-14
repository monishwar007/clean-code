package jakarta.persistence;
import java.lang.annotation.*;
@Target(ElementType.FIELD) @Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    String mappedBy() default "";
    CascadeType[] cascade() default {};
    FetchType fetch() default FetchType.LAZY;
    boolean orphanRemoval() default false;
}
