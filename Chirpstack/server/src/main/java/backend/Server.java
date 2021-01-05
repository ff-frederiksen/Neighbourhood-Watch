package backend;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;

import communication.ChirpstackRequest;
import communication.Mqtt;
import nwa.database.Database;
import nwa.database.DatabaseArrayList;
import nwa.devices.DeviceUnit;
import nwa.devices.Device;
import nwa.home.Home;
import nwa.home.HomeID;
import nwa.home.HomeIDValue;
import nwa.home.HomeUnit;
import nwa.home.PhoneAddress;
import nwa.home.PhoneAddressImplementation;
import nwa_functionality.NWA;
import representation.CreateUserRequest;
import representation.LoginRequest;
import user.AuthToken;
import user.User;


public class Server {
	
	/** Fields that should be changed to properly configure the connections of this server **/
	
	private static String chirpstack_ip = "192.168.1.74"; 
	private static String mqtt_port = "1883"; 
	private static String chirpstack_port = "8080";
	public static int application_id = 2;
	public static int organization_id = 1;
	public static String device_profile = "1986e4d2-dfd9-4e7d-8961-779101265cf1";
	
	/** Fields that should not be altered **/
	
	private final static String IP_MQTT = "tcp://"+chirpstack_ip+":"+mqtt_port;		// The IP-address and port of MQTT 
	private final static String TOPIC_MQTT = "application/"+application_id+"/device/#";		// Should be on the form "application/{appId}/device/#"
	public final static String REST_URL = "http://"+chirpstack_ip+":"+chirpstack_port;
	
	//Date-time format:
	private final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
	
	private static JSONParser parser = new JSONParser();
	
	
	private static Mqtt mqtt;
	
	public static Database<User> userDB;
	
	public static String api_key;
	
	private static Server server;
	
	
	public Server() throws InterruptedException {
		userDB = new DatabaseArrayList<User>();
		
		String admin_usr = "admin@admin.dk";
		String admin_pw = "admin";
		// log into chirpstack
		api_key = ChirpstackRequest.getJWT(admin_usr, admin_pw);
			
	}
	
	public static Server getInstance() throws InterruptedException {
		if (server == null) {
			server = new Server();
			server.run();
		}
		return server;
	}
	
	public void run() throws InterruptedException {
		// Getting relevant database info from Chirpstack Application Server
		loadDevices(api_key);
		loadUsers(api_key);
			
		//Some cleanup.
		NWA.getInstance().cleanUp();

		try {
			NWA.getInstance().readHomeDatabaseFile();
		} catch (IOException e) {
			return;
		}
			
		//Setup new transmitSms
//		sender = new SMSSenderBash();
			
		// Client handling all received data
		mqtt = new Mqtt(IP_MQTT, TOPIC_MQTT);	
		
		while(true) {
//			System.out.print(".");
			NWA.getInstance().alarmHouses();
			NWA.getInstance().checkDevices();
			//System.out.println("Server up");
			Thread.sleep(1000);		
			
		}	
	}
	
	/************* Setup functionality ***********/
	public static int resetDatabases(String jwt) {
		User user = getUserFromJWT(jwt);
		if (user.isAdmin()) {
			userDB = new DatabaseArrayList<User>();
			return 0;
		}
		return -1;
	}
	
	public static int loadUsers(String jwt) {
		int offset = 0;
		int limit = 10;
		JSONArray response = ChirpstackRequest.getUsers(jwt, offset, limit);
		if (response == null) return -1;
		Iterator<JSONObject> jList = response.iterator();

		
		while(jList.hasNext()) {
			JSONObject o = (JSONObject) jList.next();
			System.out.println(o);
			
			String userID = o.get("id").toString();
			String username = o.get("email").toString();
			boolean isAdmin = o.get("isAdmin").toString().equalsIgnoreCase("true");
			
			JSONObject user = ChirpstackRequest.getUser(jwt, userID);
			try {
				JSONObject notes = (JSONObject) parser.parse(user.get("note").toString());
				if (!notes.toString().equals("") || ! (notes == null)) {
					String homeID = notes.get("HomeID").toString();
					
					userDB.add(new User(userID, username, homeID));
					
					System.out.println("user loaded: "+username);
					
					JSONArray phones = (JSONArray) notes.get("phones");
					
					if (homeID != null) {
						for (int i = 0; i < phones.size(); i++) {
							String number = phones.get(i).toString();
							NWA.getInstance().add(new PhoneAddressImplementation(number, new HomeIDValue(homeID)));
						}
					}
				}
			} catch (NullPointerException |ParseException e) {
				e.printStackTrace();
				return -1;
			}
			
			// Read next page if reached the end of current page
			if (!jList.hasNext()) {
				offset += 1;
				jList = ChirpstackRequest.getUsers(jwt, offset, limit).iterator();
				if (!jList.hasNext())  break;
			}
			
			offset += 1;
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadDevices(String jwt) {
		int offset = 0;
		int limit = 10;
		Iterator<JSONObject> jList = ChirpstackRequest.getDevices(jwt, offset, limit).iterator();
		
		System.out.println("Loading devices...");
		while(jList.hasNext()) {
			JSONObject o = (JSONObject) jList.next();
			
			String devEUI = o.get("devEUI").toString();
			
			Object date = o.get("lastSeenAt");
			
			LocalDateTime lastSeenAt = date != null ? LocalDateTime.parse(date.toString(), dateFormat) : null;
			
			HomeID homeID = loadHomeOfDevice(devEUI);
			
			Device device = new DeviceUnit(devEUI, homeID);
			
			device.updateLastDate(lastSeenAt);
			
			NWA.getInstance().add(device);
			
			
			// Read next page if reached the end of current page
			if (!jList.hasNext()) {
				offset += 1;
				jList = ChirpstackRequest.getDevices(api_key, offset, limit).iterator();
				if (!jList.hasNext())  break;
			}
			
			offset += 1;
		}
		System.out.println("Devices have been loaded");
	}

	private static HomeID loadHomeOfDevice(String devEUI) {
		try {			
			
			String response = ChirpstackRequest.getDevice(api_key, devEUI);
			JSONObject deviceInfo = ((JSONObject) parser.parse(response));
			
			JSONObject vars;
			if (deviceInfo.containsKey("variables")) {
				vars =(JSONObject) deviceInfo.get("variables");
				
				// load houseID
				Object houseIDObject = null;
				if (vars.containsKey("HomeID")) {
					houseIDObject = vars.get("HomeID");
				}
				HomeID homeID = houseIDObject != null ? new HomeIDValue(houseIDObject.toString()) : null;
				
				// load address
				Object addressObject = null;
				if (vars.containsKey("address")) {
					addressObject = vars.get("address");
				}
				String address = addressObject != null ? addressObject.toString() : null;

				
				Home home;
				// if there is no houseID connected to device
				if (homeID == null) {
					home = createHome(address);
					homeID = home.getHomeID();
					// TODO: add houseID to device
				} else {
					// generate pin
					SecureRandom random = new SecureRandom();
					int num = random.nextInt(10000);
					String pin = String.format("%04d", num);
					
					//generate salt
					byte[] salt = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x00, 0x00};
//					byte[] salt = random.generateSeed(16);
					
					// Create house
					home = new HomeUnit(address, homeID, pin, salt);
					NWA.getInstance().add(home);
				}
				
				System.out.println(address+", "+home.getPassword());
				return homeID;
			}
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/************* Device functionality ***********/
	
	public static String createDevice(String jwt, String houseID) {
		Home home = NWA.getInstance().getHome(houseID);
		if (home==null) {
			return null;
		}
		
		JSONObject requestBody = new JSONObject();
		JSONObject deviceInfo = new JSONObject();
		
	    SecureRandom random = new SecureRandom();
	    
		String devEUI = Server.byteStringGenerator(8);
	    
	    deviceInfo.put("applicationID", Server.application_id);
	    deviceInfo.put("description", " ");
		deviceInfo.put("devEUI", devEUI);
		deviceInfo.put("deviceProfileID",device_profile);
		deviceInfo.put("skipFCntCheck", false);
		deviceInfo.put("variables",new JSONObject());
		
		JSONObject variables = new JSONObject();
		
		String address = home.getAddress();
		
		String name = address+": "+(String.format("%03d", random.nextInt(1000)));
		
		deviceInfo.put("name", name);	
		
		variables.put("HomeID", home.getHomeID().getID());
		
		deviceInfo.put("variables", variables);
		
		requestBody.put("device", deviceInfo);
		
		
		String response = ChirpstackRequest.createDevice(jwt, requestBody.toJSONString());
		
		if (response != null) {
			Device device = new DeviceUnit(devEUI, home.getHomeID());
			NWA.getInstance().add(device);
			ChirpstackRequest.giveNetworkKey(jwt, devEUI);
		}
		return devEUI;
	}
	
	public static void deleteDevice(String jwt, String devEUI) {
		Device device = NWA.getInstance().getDevice(devEUI);
		String response = ChirpstackRequest.deleteDevice(jwt, devEUI);
		if (response != null && device != null) {
			NWA.getInstance().delete(device);
		}
	}
	
	public static void transferDevice(String jwt, String devEUI, String transferHouseID) {
		Device device = NWA.getInstance().getDevice(devEUI);
		Home transferHome = NWA.getInstance().getHome(transferHouseID);
		
		// Stop process if not an existing device or house
		if (device == null || transferHome == null) return;
		
		String response = ChirpstackRequest.transferDevice(jwt, device, transferHome);
		
		if (response != null) {
			NWA.getInstance().transferDevice(device, transferHome);
		}	
	}
	
	/************* Home functionality ***********/
	
	public static Home createHome(String address) {
		Home home = generateHome(address);
		NWA.getInstance().add(home);
		
		return home;
	}
	
	public static Home generateHome(String address) {
		HomeID homeID = new HomeIDValue(UUID.randomUUID().toString());
		
		SecureRandom random = new SecureRandom();
		
		int num = random.nextInt(10000);
		String pin = String.format("%04d", num);
		
		byte[] salt = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x00, 0x00};
//		byte[] salt = random.generateSeed(16);
		
		Home home = new HomeUnit(address, homeID, pin, salt);
		
		return home;
	}
	
	public static void deleteHouse(String jwt, String houseID, String transferHouseID) {
		List<Device> devices = NWA.getInstance().getHomeDevices(houseID);
		List<PhoneAddress> phones = NWA.getInstance().getHomePhones(houseID);
		Home home = NWA.getInstance().getHome(houseID);
		
		if (home != null) {
			if (transferHouseID == null && !devices.isEmpty()) {
				for (Device device : devices) {
					deleteDevice(jwt, device.getComponentID().getID());
				}
			} else if (transferHouseID != null && !devices.isEmpty()) {
				Home transferHome = NWA.getInstance().getHome(transferHouseID);
				if (transferHome != null) {
					for (Device device : devices) {
						transferDevice(jwt, device.getComponentID().getID(), transferHome.getHomeID().getID());
					}
					
				}
			}
			
			for (PhoneAddress phone : phones) {
				NWA.getInstance().delete(phone);
			}
			
			NWA.getInstance().delete(home);
		}	
	}
	
	public static void changeHousePin(String houseID, String pin) {
		NWA.getInstance().changeHomePin(houseID, pin);
	}
	
	
	/************* Phone functionality ***********/
	
	public static PhoneAddress createPhone(String houseID, String phone) {
		Home home =NWA.getInstance().getHome(houseID);
		if (home!=null) {
			PhoneAddress phoneAdr = new PhoneAddressImplementation(phone, home.getHomeID());
			NWA.getInstance().add(phoneAdr);
			return phoneAdr;
		}
		return null;
	}
	
	
	public static void deletePhone(PhoneAddress phone) {
		NWA.getInstance().delete(phone);
		
	}
	
	/************* User functionality ***********/
	
	
	public static AuthToken login(LoginRequest request) {
		User user = getUser(request.getUsername());
		if (user != null) {
			String jwt = ChirpstackRequest.chirpstackLogin(request.getUsername(), request.getPassword());
			
			if (jwt != null) {
				AuthToken auth = new AuthToken(user.getUsername(),jwt);
				user.setJwt(jwt);
				return auth;		
			}
		}
		return null;	
	}
	
	public static String createUser(CreateUserRequest request) {
		//userDB.add(new User("1", "username", null));
		try {
			User user = getUserFromJWT(request.jwt);
			if (user != null) {
				if (user.isAdmin()) {
					String homeID = ((JSONObject) parser.parse(request.note)).get("homeID").toString();
					if ((NWA.getInstance().getHome(homeID)) != null) {
						String id = ChirpstackRequest.createUser(request.jwt, request.username, request.password, request.note, request.email, request.isAdmin);
						if (id != null) {
							userDB.add(new User(id,request.username, homeID));
							return id;
						}
					}
				}
			}
		} catch (InterruptedException | ParseException e) {
			return null;
		}
		return null;
	}
	
	public static User getUser(String username) {
		Optional<User> user = userDB.get(u -> (u.getUsername().equals(username)));
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	public static User getUserFromJWT(String jwt) {
		Optional<User> user = userDB.get(u -> (u.getJwt().equals(jwt)));
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	
	/************* Assorted functionality ***********/
	
	public static String byteStringGenerator(int num) {
		SecureRandom random = new SecureRandom();
		byte[] bytes = random.generateSeed(num);
		return javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
	}
	
}
