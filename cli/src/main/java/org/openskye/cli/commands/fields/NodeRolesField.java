package org.openskye.cli.commands.fields;

import org.openskye.domain.NodeArchiveStoreInstance;

import java.util.ArrayList;
import java.util.List;

public class NodeRolesField extends Field {
    private final List<NodeArchiveStoreInstance> nodes;
    private final String name;

    public NodeRolesField(String name) {
        this.nodes = new ArrayList<>();
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
