package org.skye.core;

/**
 * {@link EntityException} which indicates that a persistable Entity was found
 * when it should not have existed.
 */
public class EntityFoundException  extends EntityException
{
    public EntityFoundException(String s, String id, Class type)
    {
        super(s, id, type);
    }

    public EntityFoundException(String s, Exception ex, String id, Class type)
    {
        super(s, ex, id, type);
    }
}
