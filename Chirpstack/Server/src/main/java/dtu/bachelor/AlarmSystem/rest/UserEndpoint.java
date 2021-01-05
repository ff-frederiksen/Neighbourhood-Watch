package dtu.bachelor.AlarmSystem.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import backend.Server;

import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import representation.CreateUserRequest;
import representation.GenericRequest;
import representation.LoginRequest;
import user.AuthToken;

@Path("/user")
@Api(value = "/user", tags = "user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class UserEndpoint {
	
	@POST
	@Path("/login")
	@ApiOperation(value = "Get login-token",
    	notes = "Returns authentification-token",
    	response = String.class
    )
	public Response login(String data) throws InterruptedException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			LoginRequest loginrequest;
			loginrequest = mapper.reader().forType(LoginRequest.class).readValue(data);
			
			AuthToken result = Server.getInstance().login(loginrequest);
			
			if (result != null) {
				return Response.status(Response.Status.OK).entity(result).build();
			} else {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	
	}
	
	@POST
	@Path("/user")
	@ApiOperation(value = "Create a new user",
    	notes = "Returns user ID",
    	response = String.class
    )
	public Response createUser(String data) throws InterruptedException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			CreateUserRequest createUserRequest;
			createUserRequest = mapper.reader().forType(CreateUserRequest.class).readValue(data);
			
			String result = Server.getInstance().createUser(createUserRequest);
			
			if (result != null) {
				return Response.status(Response.Status.OK).entity(result).build();
			} else {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	
	}
	
	@POST
	@Path("/data")
	@ApiOperation(value = "Reload data into the server",
    	notes = "Returns nothing",
    	response = String.class
    )
	public Response reload(String data) throws InterruptedException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			GenericRequest genericRequest;
			genericRequest = mapper.reader().forType(GenericRequest.class).readValue(data);
			
			if (Server.resetDatabases(genericRequest.jwt)==0) {
				Server.getInstance().loadUsers(genericRequest.jwt);
				Server.loadDevices(genericRequest.jwt);
				return Response.status(Response.Status.OK).build();
			} else {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	
	}
	
	
}
