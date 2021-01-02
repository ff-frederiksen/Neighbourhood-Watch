package Administration;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import backend.Server;
import communication.ChirpstackRequest;
import nwa.exampleFile.SetupExample;
import nwa.home.Home;
import nwa.home.PhoneAddress;
import nwa_functionality.NWA;
import representation.LoginRequest;
import user.AuthToken;



public class MainTest {
	private static JSONParser parser = new JSONParser();

	
	@Test
	public void testHomeFile() {
		String[] args = new String[1];
		args[0]= "testHome";
		try {
			SetupExample.testing(args);
		} catch (IOException |InterruptedException e) {
			assertTrue(false);
		}
		
		try {
			NWA.getInstance().readHomeDatabaseFile();
		} catch (IOException e) {
			assertTrue(false);
		}
		assertTrue(NWA.getInstance().getHome("testHome")!=null);
	}
	
	@Test
	public void testGenerateHouse() {
		Home testHome = Server.generateHome("Villa Kulla Vej 7");
		assertEquals(testHome.getAddress(), "Villa Kulla Vej 7");
		assertTrue(testHome.getSalt().length==16);
	}
	
	@Test
	public void testHouse() {
		try {
			Server server = new Server();
			
			String jwt = ChirpstackRequest.chirpstackLogin("admin@admin.dk", "admin");
			int initialHouseNumber =NWA.getInstance().getHouseDBsize();
			
			List<JSONObject> jList = ChirpstackRequest.getDevices(jwt, 0, 100);
			int initialDeviceNumberChirp = jList.size();
			
			// Test: create 2 houses
			Home testHome = server.createHome("Paa Taget 5");
			assertNotNull(NWA.getInstance().getHome(testHome.getHomeID().getID()));
			
			Home transferHome = server.createHome("Paa Taget 6");
			assertNotNull(NWA.getInstance().getHome(transferHome.getHomeID().getID()));
			
			// Add device to first house
			String devEUI = null;
			try {
				devEUI = server.createDevice(jwt, testHome.getHomeID().getID());
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false);
			}
			
			int afterCreation = NWA.getInstance().getHouseDBsize();
			assertEquals(afterCreation, (initialHouseNumber+2));
			
			
			// Test: delete house (transfer devices to transferHouse)
			server.deleteHouse(jwt, testHome.getHomeID().getID(), transferHome.getHomeID().getID());
			
			int afterDeletingOne = NWA.getInstance().getHouseDBsize();
			assertEquals(afterCreation-1, afterDeletingOne);
			
			
			assertEquals(initialDeviceNumberChirp+1, ChirpstackRequest.getDevices(jwt, 0, 100).size());
			try {
				JSONObject chirpDev =(JSONObject) parser.parse(ChirpstackRequest.getDevice(jwt, devEUI));
				JSONObject tags = (JSONObject) chirpDev.get("tags");
				String devHouseID = tags.get("HomeID").toString();
				assertEquals(transferHome.getHomeID().getID(), devHouseID);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Test: delete house (no transfer of devices)
			server.deleteHouse(jwt, transferHome.getHomeID().getID(), null);
			
			int afterDeletingTwo = NWA.getInstance().getHouseDBsize();
			assertEquals(initialHouseNumber, afterDeletingTwo);
			
			assertEquals(initialDeviceNumberChirp, ChirpstackRequest.getDevices(jwt, 0, 100).size());
		} catch (InterruptedException e1) {
			assertTrue(false);
		}
		
		
		
	}
	
	@Test
	public void testDevice() {
		try {
			Server server = new Server();
			
			// Logging in as admin
			String jwt = ChirpstackRequest.chirpstackLogin("admin@admin.dk", "admin");
			
			Home testHome = server.createHome("Halfdans Alle 14");
			
			// Baseline-reference
			int initialDeviceNumber =NWA.getInstance().getDeviceDBsize();
			
			
			// Test: Create Device with invalid houseID
			String invalid = server.createDevice(jwt, "YipYip");
			assertNull(invalid);
			
			// Test: Create device
			String devEUI = null;
			devEUI = server.createDevice(jwt, testHome.getHomeID().getID());

			int afterCreationNumber =NWA.getInstance().getDeviceDBsize();
			assertEquals(afterCreationNumber, initialDeviceNumber+1);
			
			// Test: delete Device
			server.deleteDevice(jwt, devEUI);
			int afterDeletionNumber = NWA.getInstance().getDeviceDBsize();
			assertEquals(afterDeletionNumber,initialDeviceNumber);
		} catch (InterruptedException e) {
			assertTrue(false);
			
		}
	}
	
	@Test
	public void testPhone() {
		try {
			Server server = new Server();
			
			// Test: create phone
			int initialNumberOfPhones = NWA.getInstance().getPhoneAddrDBsize();
			Home testHome = server.createHome("Paa Taget 5");
			PhoneAddress phone = server.createPhone(testHome.getHomeID().getID(), "+4512345678");
			assertNotNull(phone);
			
			int afterCreationNumber = NWA.getInstance().getPhoneAddrDBsize();
			
			assertEquals(initialNumberOfPhones+1, afterCreationNumber);
			
			// Test: delete phone
			server.deletePhone(phone);
			int afterDeletionNumber = NWA.getInstance().getPhoneAddrDBsize();
			assertEquals(initialNumberOfPhones, afterDeletionNumber);	
		} catch (InterruptedException e) {
			assertTrue(false);
		}
		
		
		
	}
	
	@Test
	public void testJWT() {
		// Test: Invalid login
		assertNull(ChirpstackRequest.chirpstackLogin("notAUser", "password"));
		
		// Test: Valid login
		assertNotNull(ChirpstackRequest.chirpstackLogin("admin@admin.dk", "admin"));
		
		// Test: Obtaining glabal, admin api_key
		assertNotNull(ChirpstackRequest.getJWT("admin@admin.dk", "admin"));
	}
	
	@Test
	public void testUser() throws InterruptedException, IOException {
		
		// logging in as admin
		String jwt = ChirpstackRequest.chirpstackLogin("admin@admin.dk", "admin");
		
		// Test: creating user
		String id = ChirpstackRequest.createUser(jwt, "Rasmussen", "yolo123", null, "test@test.dk", false);
		assertNotNull(id);
		// Test: login/get token as user
		String user_jwt = ChirpstackRequest.chirpstackLogin("test@test.dk", "yolo123");
		assertNotNull(user_jwt);
		
		// Test: login/get token as user
		// Test: change password
		assertNotNull(ChirpstackRequest.changeUserPassword(user_jwt, id, "yolo321"));
		
		// Test: login/get token as user
		// Test: deleting same user
		assertNotNull(ChirpstackRequest.deleteUser(jwt, id));
		
		// Test: login/get token as user
		
		String json = "{\"username\":\"admin@admin.dk\",\"password\":\"admin\"}";
	    ObjectMapper mapper = new ObjectMapper();
	 
	    LoginRequest loginrequest = mapper.reader().forType(LoginRequest.class).readValue(json);
	    assertEquals("admin@admin.dk", loginrequest.getUsername());
		
		// Test: login/get token as user
	}
	
	
}
