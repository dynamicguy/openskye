package org.openskye.validator;

import com.google.inject.Provider;
import org.openskye.core.SkyeException;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class UniqueValidator implements ConstraintValidator<Unique, Object>
{
    private Unique uniqueConstraint;

    private Class entityClass;
    private String fieldName;

    @Inject
    private Provider<EntityManager> emf;

    private EntityManager getEntityManager()
    {
        if(emf == null)
            throw new NullPointerException("EntityManager provider is null");

        EntityManager entityManager = emf.get();

        if(entityManager == null)
            throw new NullPointerException("EntityManager is null");

        return entityManager;
    }

    private CriteriaBuilder getCriteriaBuilder()
    {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public void initialize(Unique unique)
    {
        uniqueConstraint = unique;
        entityClass = uniqueConstraint.entity();
        fieldName = uniqueConstraint.fieldName();

        /**
         * Ensure that the constraint is used on an Entity class.
         *
         * TODO: This should eventually become a compile time check.
         *
         * TODO: The entity field should eventually be replaced with some reflection trick which automatically obtains the class containing the annotation.
         */
        Annotation entityAnnotation = entityClass.getAnnotation(Entity.class);

        if(entityAnnotation == null)
        {
            String message = entityClass.getCanonicalName();

            message += " is not an Entity class";

            throw new SkyeException(message);
        }

        /**
         * Check to ensure that the field name given is a valid field on the entityClass.
         *
         * TODO: Attempt to infer the field name based on the Unique annotation.
         */
        try
        {
            entityClass.getDeclaredField(fieldName);
        }
        catch(NoSuchFieldException ex)
        {
            String message = entityClass.getCanonicalName();

            message += " does not contain a field named ";
            message += fieldName;

            throw new SkyeException(message);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext)
    {
        // We will obtain the number of entities in the database for which the specified field is equal to the value.
        // If the result of this query is 0, then the field will be unique once added to the database.
        // If it is not, then false is returned, and the default ConstraintValidation should already know
        // the appropriate path to the annotated field, as well as the message set by the annotation.
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root root = query.from(entityClass);

        query.select(builder.count(root));
        query.where(builder.equal(root.get(fieldName), value));

        Long result = getEntityManager().createQuery(query).getSingleResult();

        return (result == 0);
    }
}
