package org.openskye.core;

/**
 * An exception that an {@link InformationStore} would throw if the {@link SimpleObject} that
 * was passed to it for materialization is invalid against this information store
 */
public class InvalidSimpleObjectException extends Exception {

    private Class<?> simpleObjectClass;

    public InvalidSimpleObjectException(){
        super();
    }

    public InvalidSimpleObjectException(String s, Class<?> simpleObjectClass) {
        super("Object is supposed to be a(n)" + simpleObjectClass.getCanonicalName());
        this.simpleObjectClass = simpleObjectClass;
    }

    public Class<?> getSimpleObjectClass(){
        return this.simpleObjectClass;
    }


}
