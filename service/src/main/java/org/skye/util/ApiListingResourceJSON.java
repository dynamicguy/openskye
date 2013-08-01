package org.skye.util;

import com.wordnik.swagger.annotations.Api;

import javax.ws.rs.Path;

@Path("/api-docs")
@Api("/api-docs")
class ApiListingResourceJSON extends com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON {

}
