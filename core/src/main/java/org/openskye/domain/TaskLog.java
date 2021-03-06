package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.openskye.core.SkyeException;

import javax.persistence.*;
import java.io.IOException;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "id")
public class TaskLog implements Identifiable {
    @Transient
    private final static ObjectMapper MAPPER = new ObjectMapper();
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @Column(name = "TASK_ID")
    private String taskId;
    @Column(name = "LOG_TIME")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime logTime = LocalDateTime.now();
    @Column(name = "STATUS")
    private TaskStatus status;
    @Column(name = "MESSAGE")
    private String message;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "EXCEPTION_JSON",length=100000)
    @JsonIgnore
    private String exceptionJson;
    @Transient
    private Exception exception;

    @PostLoad
    protected void deserialize() {
        if (getExceptionJson() == null) {
            setException(null);
        }
        else {
            try {
                Exception exception = MAPPER.readValue(getExceptionJson(), Exception.class);
                setException(exception);
            }
            catch (JsonProcessingException e) {
                setException(new Exception(getExceptionJson()));
            }
            catch (ClassCastException | IOException e) {
                throw new SkyeException("Unable to deserialize exception in task log", e);
            }
        }
    }

    @PrePersist
    @PreUpdate
    protected void serialize() {
        if (getException() == null) {
            setExceptionJson(null);
        } else {
            try {
                setExceptionJson(MAPPER.writeValueAsString(getException()));
            }
            catch (JsonProcessingException e) {
                setExceptionJson(e.getMessage());
            }
        }
    }
}
