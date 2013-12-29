package org.openskye.resource;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.persist.Transactional;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.openskye.domain.Node;
import org.openskye.domain.dao.AbstractPaginatingDAO;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.PaginatedResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The REST endpoint for {@link org.openskye.domain.Node}
 */
@Api(value = "/api/1/nodes", description = "Manage nodes")
@Path("/api/1/nodes")
public class NodeResource extends AbstractUpdatableDomainResource<Node> {

    private NodeDAO nodeDAO;


    @Inject
    public NodeResource(NodeDAO dao) {
        this.nodeDAO = dao;
    }

    @ApiOperation(value = "Create new node", notes = "Create a new node and return with its unique id", response = Node.class)
    @POST
    @Transactional
    @Timed
    public Node create(Node newInstance) {
        return super.create(newInstance);
    }

    @ApiOperation(value = "Update node", notes = "Enter the id of the node to update, return the updated node", response = Node.class)
    @Path("/{id}")
    @PUT
    @Transactional
    @Timed
    @Override
    public Node update(@PathParam("id") String id, Node newInstance) {
        return super.update(id, newInstance);
    }

    @ApiOperation(value = "Find node by id", notes = "Return a node by its unique id", response = Node.class)
    @Path("/{id}")
    @GET
    @Transactional
    @Timed
    @Override
    public Node get(@PathParam("id") String id) {
        return super.get(id);
    }

    @ApiOperation(value = "List all nodes", notes = "Returns all nodes in a paginated structure", responseContainer = "List", response = Node.class)
    @GET
    @Transactional
    @Timed
    @Override
    public PaginatedResult<Node> getAll() {
        return super.getAll();
    }

    @ApiOperation(value = "Delete node", notes = "Deletes the node(found by unique id)")
    @Path("/{id}")
    @DELETE
    @Transactional
    @Timed
    @Override
    public Response delete(@PathParam("id") String id) {
        return super.delete(id);
    }

    @Override
    protected AbstractPaginatingDAO<Node> getDAO() {
        return nodeDAO;
    }

    @Override
    protected String getPermissionDomain() {
        return "node";
    }

}
