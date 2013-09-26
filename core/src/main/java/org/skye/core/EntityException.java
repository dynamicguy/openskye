package org.skye.core;

/**
 * {@link RuntimeException} which indicates that there is a problem with a
 * persistable Entity in the Java Persistence API (JPA).
 */
public class EntityException extends RuntimeException
{
    private String _id;
    private Class _type;

    public EntityException(String s, String id, Class type)
    {
        super(s);

        this.setId(id);
        this.setType(type);
    }

    public EntityException(String s, Exception e, String id, Class type)
    {
        super(s, e);

        this.setId(id);
        this.setType(type);
    }

    public String getId()
    {
        return this._id;
    }

    public Class getType()
    {
        return this._type;
    }

    protected void setId(String id)
    {
        this._id = id;

        return;
    }

    protected void setType(Class type)
    {
        this._type = type;

        return;
    }
}
