package uu.toolbox.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UUSqlColumn
{
    enum Type
    {
        TEXT("TEXT"),
        INT_8("INTEGER(1)"),
        INT_16("INTEGER(2)"),
        INT_32("INTEGER(4)"),
        INT_64("INTEGER(8)"),
        INTEGER("INTEGER"),
        REAL("REAL"),
        BLOB("BLOB"),
        INTEGER_PRIMARY_KEY_AUTOINCREMENT("INTEGER PRIMARY KEY AUTOINCREMENT"),
        INFER_FROM_FIELD_TYPE("");

        private String value;

        Type(String val)
        {
            value = val;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }

    String name() default "";
    Type type() default Type.INFER_FROM_FIELD_TYPE;
    boolean nullable() default false;
    boolean nonNull() default false;
    String defaultValue() default "";
    boolean primaryKey() default false;
    int existsInVersion() default 1;
}
