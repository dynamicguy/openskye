package org.skye.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * An instance of an {@link org.skye.core.ArchiveStore} definition
 */
@Entity
@Table(name = "ARCHIVE_STORE_INSTANCE")
public class ArchiveStoreInstance extends AbstractDomainObject {

}
