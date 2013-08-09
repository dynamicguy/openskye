package org.skye.resource;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.yammer.dropwizard.testing.ResourceTest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.Test;
import org.skye.domain.Channel;
import org.skye.domain.Domain;
import org.skye.resource.dao.ChannelDAO;
import org.skye.util.PaginatedResult;

import javax.ws.rs.core.MediaType;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.fail;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ChannelResourceTest extends ResourceTest {
    private final Channel channel = new Channel();
    private final ChannelDAO dao = mock(ChannelDAO.class);
    private final Subject subject = mock(Subject.class);
    private PaginatedResult<Channel> expectedResult = new PaginatedResult<>();

    @Override
    protected void setUpResources() {
        when(dao.list()).thenReturn(expectedResult);
        when(dao.delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(true);
        when(dao.get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9")).thenReturn(channel);
        when(dao.persist(channel)).thenReturn(channel);
        ChannelResource channelResource = new ChannelResource();
        channelResource.channelDAO = dao;
        addResource(channelResource);
    }

    @Test
    public void testAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:update")).thenReturn(true);
        assertThat(client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(Channel.class, channel)).isEqualTo(channel);
    }

    @Test
    public void testUnAuthorizedPut() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:update")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").type(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class, channel).getStatus());
    }

    @Test
    public void testAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:create")).thenReturn(true);
        assertThat(client().resource("/api/1/channels").type(MediaType.APPLICATION_JSON_TYPE).post(Channel.class, channel)).isEqualTo(channel);
    }

    @Test
    public void testUnAuthorizedPost() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:create")).thenReturn(false);
        assertEquals(401,client().resource("/api/1/channels").type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, channel).getStatus());
    }

    @Test
    public void testAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:list")).thenReturn(true);
        assertThat(client().resource("/api/1/channels").get(PaginatedResult.class)).isEqualTo(expectedResult);
    }

    @Test
    public void testUnAuthorizedGetAll() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:list")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/channels").get(PaginatedResult.class)).isEqualTo(expectedResult);
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }

    @Test
    public void testAuthorizedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:delete")).thenReturn(true);
        ClientResponse response = client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(response.getStatus(),200);

        verify(dao).delete("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }
    @Test
    public void testUnAuthorisedDelete() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:delete")).thenReturn(false);
        ClientResponse response = client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").delete(ClientResponse.class);
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testAuthorizedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:get")).thenReturn(true);
        assertThat(client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Channel.class))
                .isEqualTo(channel);
        verify(dao).get("59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9");
    }

    @Test
    public void testUnAuthorisedGet() throws Exception {
        ThreadContext.bind(subject);
        when(subject.isPermitted("channel:get")).thenReturn(false);
        try {
            assertThat(client().resource("/api/1/channels/59ae3dfe-15ce-4e0d-b0fd-f1582fe699a9").get(Channel.class));
            fail("Should be unauthorized");
        } catch (UniformInterfaceException e) {
            assertThat(e).hasMessage("Client response status: 401");
        }
    }
}