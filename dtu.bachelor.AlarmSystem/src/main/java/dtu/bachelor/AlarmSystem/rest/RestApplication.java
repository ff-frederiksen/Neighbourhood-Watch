package dtu.bachelor.AlarmSystem.rest;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import backend.Server;

@ApplicationPath("/alarmsystem")
public class RestApplication extends Application {
	public static Server server;
	
	@PostConstruct
	public static void main(String[] args) throws InterruptedException {
		server = Server.getInstance();
		server.run();
	}
}
