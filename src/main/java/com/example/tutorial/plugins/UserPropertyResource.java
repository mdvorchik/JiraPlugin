package com.example.tutorial.plugins;


import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/property")
public class UserPropertyResource {

    private final UserPropertyService userPropertyService;

    public UserPropertyResource(UserPropertyService userPropertyService) {
        this.userPropertyService = userPropertyService;
    }

    @POST
    @Path("/add")
    @Produces({MediaType.APPLICATION_JSON})
    public Response addProperty(@QueryParam("user") String user, @QueryParam("name") String name, @QueryParam("value") String value) {
        userPropertyService.addProperty(user, name, value);
        return Response.ok().build();
    }
}
