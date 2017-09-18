package dk.developer.alpha.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

@Documented
@Constraint(validatedBy = FacebookTokenConstraintValidator.class)
@Target({TYPE, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FacebookToken {
    String message() default "Invalid facebook token";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
