package me.levitate.quill.config.toml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Comment {
    /**
     * The comment lines to add above the field
     * @return Array of comment lines
     */
    String[] value();
}