package org.openskye.core.validation;

/**
 * Represents an instance of a validation failure on a field through manual validation.
 */
public class FieldValidationDetail
{
    private String message;
    private String fieldName;
    private Class beanClass;

    public FieldValidationDetail(String message, String fieldName, Class beanClass)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
