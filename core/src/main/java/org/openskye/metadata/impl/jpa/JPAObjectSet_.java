package org.openskye.metadata.impl.jpa;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(JPAObjectSet.class)
public class JPAObjectSet_ {
    public static volatile SingularAttribute<JPAObjectSet, String> id;
    public static volatile SingularAttribute<JPAObjectSet, String> name;
    public static volatile SetAttribute<JPAObjectSet, JPAObjectMetadata> objectMetadataSet;
}