package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.domain.MetadataTemplate;
import org.openskye.domain.PeriodType;
import org.openskye.domain.RetentionPolicy;

import java.util.List;

/**
 * Manage a {@link RetentionPolicy}
 */
@Parameters(commandDescription = "Manage retention policies")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class RetentionPoliciesCommand extends AbstractCrudCommand {

    private final String commandname = "retentionPolicies";

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name", false)).add(new TextField("description", false)).add(new TextField("recordsCode", false)).add(new NumberField("retentionPeriod", false)).add(new NumberField("priority", false)).add(new EnumerationField("periodType", PeriodType.class, false)).add(new ReferenceField(MetadataTemplate.class, false)).add(new TextField("metadataCriteria", false)).build();
    }

    @Override
    public String getCommandName() {
        return commandname;
    }

    @Override
    public Class getClazz() {
        return RetentionPolicy.class;
    }

    @Override
    public String getCollectionPlural() {
        return "retentionPolicies";
    }
}
