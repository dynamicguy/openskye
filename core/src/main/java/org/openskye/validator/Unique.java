package org.openskye.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueValidator.class)
public @interface Unique
{
    /**
     * A message describing the error in the event that a validation fails.
     *
     * @return A fitting error message.
     */
    public String message() default "Field must be unique";

    /**
     * A String which indicates the name of the field to be checked.
     *
     * @return The name of the field to be checked.
     */
    public String fieldName() default "";

    /**
     * The Class of the domain object which contains the Unique constraint.
     *
     * @return The Class of the domain object.
     */
    public Class entity() default Object.class;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
