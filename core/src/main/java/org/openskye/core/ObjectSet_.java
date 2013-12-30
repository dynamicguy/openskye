package org.openskye.core;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(ObjectSet.class)
public class ObjectSet_ {
    public static volatile SingularAttribute<ObjectSet, String> id;
    public static volatile SingularAttribute<ObjectSet, String> name;
    public static volatile SingularAttribute<ObjectSet, Boolean> onHold;
    public static volatile SetAttribute<ObjectSet, ObjectMetadata> objectMetadataSet;
}
