package dtu.bachelor.AlarmSystem.rest;


import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import backend.Server;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;


@Path("/hello")
public class HelloWorldEndpoint {
    @GET
    @Produces("text/plain")
    public Response doGet() throws InterruptedException {
        return Response.ok("Hello from Thorntail!: "+Server.getInstance().api_key).build();
    }
}
