package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * The representation of an information store owned by a {@link Domain}
 */
@Entity
@Table(name = "DOMAIN_INFORMATION_STORE")
@Data
public class DomainInformationStore {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Domain domain;

}
