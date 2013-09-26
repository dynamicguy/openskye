package org.skye.core;

public class EntityNotFoundException extends EntityException
{
    public EntityNotFoundException(String s, String id, Class type)
    {
        super(s, id, type);
    }

    public EntityNotFoundException(String s, Exception ex, String id, Class type)
    {
        super(s, ex, id, type);
    }
}
