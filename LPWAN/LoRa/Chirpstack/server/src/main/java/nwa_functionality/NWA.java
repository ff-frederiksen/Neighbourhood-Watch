package nwa_functionality;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import au.com.forward.sipHash.SipHash_2_4;
import communication.ChirpstackRequest;
import nwa.alarmSystemBackend.Setup;
import nwa.database.Database;
import nwa.database.DatabaseArrayList;
import nwa.devices.DeviceID;
import nwa.devices.Device;
import nwa.home.Home;
import nwa.home.HomeID;
import nwa.home.PhoneAddress;
import nwa.smsComm.SMSSender;
import nwa.smsComm.SMSSenderBash;

public class NWA {
	private static NWA instance = null;
	private SipHash_2_4 hash;
	private SMSSender sender;
//	private Gson gsosn;
	
	private static Database<Home> homeDB;
	private static Database<Device> deviceDB;
	private static Database<PhoneAddress> phoneAddrDB;
	private static HashSet<Home> warningHomes;
	
	// Delay between an alarm being triggered, and until the alarm goes off
	private final static int ALARM_DELAY = 45;
	// Limit for how long a device can go by without transmitting, before a warning is sent
	private final static long LAST_TRANSMIT_LIMIT = 60 * 1000;
	//How much time between cooldown periods of SMS
	//If this value is set to -1, then only 1 sms can be sent.
	private long timeBetweenSMS = 1 * 60 * 1000;
	
	private NWA() {
		hash = new SipHash_2_4();
		sender = new SMSSenderBash();
		
		phoneAddrDB = new DatabaseArrayList<PhoneAddress>();
		homeDB = new DatabaseArrayList<Home>();
		deviceDB = new DatabaseArrayList<Device>();
		warningHomes = new HashSet<Home>();
	}
	
	
	public synchronized static NWA getInstance() {
		if (instance == null) {
			instance = new NWA();
		}
		return instance;
	}
	
	public void cleanUp() {
		Setup setup = new Setup();
		setup.resetHomeArm(homeDB);
		setup.resetAlarmLastSeen(deviceDB);
		
	}
	
	/************* Adding to databases **************/
	
	public void add(Home home) {
		homeDB.add(home);
	}
	
	public void add(PhoneAddress phone) {
		if (!phoneExists(phone)) {
			phoneAddrDB.add(phone);
		}	
	}
	
	public void add(Device device) {
		deviceDB.add(device);
	}
	
	/**
	 * Reads the databasefiles database_house.txt, database_phone.txt and database_component.txt.
	 * @throws IOException If the files are unavailable it throws an error.
	 */
	@SuppressWarnings("unchecked")
	public void readHomeDatabaseFile() throws IOException
	{	    
		FileInputStream fileInput1 = new FileInputStream(new File("database_home.txt"));
		ObjectInputStream inputStream1 = new ObjectInputStream(fileInput1);

		// Read objects
		try {
			homeDB = (DatabaseArrayList<Home>) inputStream1.readObject();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		fileInput1.close();
		inputStream1.close();
	}
	
	/************* Getting from databases **************/
	
	public Home getHome(String houseID) {
		Optional<Home> home = homeDB.get(h -> (h.getHomeID().getID().equals(houseID)));
		if (home.isPresent()) {
			return home.get();
		}
		return null;
	}
	
	public Device getDevice(String devEUI) {
		Optional<Device> device = deviceDB.get(d -> (d.getComponentID().getID().equals(devEUI)));
		if (device.isPresent()) {
			return device.get();
		}
		return null;
	}
	
	public List<Device> getHomeDevices(String houseID) {
		List<Device> devices = deviceDB.filter(dev -> dev.getHomeID().getID().equals(houseID));
		return devices;
	}
	
	public List<PhoneAddress> getHomePhones(String houseID) {
		List<PhoneAddress> phones = phoneAddrDB.filter(ph -> ph.getHomeID().getID().equals(houseID));
		return phones;
	}
	
	public int getDeviceDBsize() {
		return this.deviceDB.size();
	}
	
	public int getHouseDBsize() {
		return this.homeDB.size();
	}
	
	public int getPhoneAddrDBsize() {
		return this.phoneAddrDB.size();
	}
	
	/************* Perform changes on items in databases **************/
	
	public void transferDevice(Device device, Home home) {
		device.changeHomeID(home.getHomeID());
		
	}
	
	public void changeDeviceHomeID(Device device, HomeID homeID) {
		device.changeHomeID(homeID);
		
	}
	
	public void changeHomePin(String houseID, String pin) {
		Home home = getHome(houseID);
		if (home != null) {
			home.changePassword(pin);
		}
	}
	
	public void clearDatabases() {
		phoneAddrDB = new DatabaseArrayList<PhoneAddress>();
		deviceDB = new DatabaseArrayList<Device>();
		warningHomes = new HashSet<Home>();
		
	}
	
	/************* Database checks **************/
	
	private boolean phoneExists(PhoneAddress pa) {
		Optional<PhoneAddress> phone = phoneAddrDB.get(ph -> (ph.getNumber() == pa.getNumber() && ph.getHomeID().getID() == pa.getHomeID().getID()));
		if (phone.isPresent()) {
			return true;
		}
		return false;
	}
	
	
	/************* Delete items in databases **************/
	
	public void delete(Home home) {
		if (getHome(home.getHomeID().getID()) != null) {
			homeDB.remove(home);
		}
	}
	
	public void delete(Device device) {
		if (getDevice(device.getComponentID().getID()) != null) {
			deviceDB.remove(device);
		}
	}
	
	public void delete(PhoneAddress phone) {
		if (this.phoneExists(phone)) {
			phoneAddrDB.remove(phone);
		}		
	}


	
	/******************* Modified or orginal methods from NWA *************************/
	
	
	public void alarmHouses()
	{
		for (Home home : warningHomes)
		{
			home.modifyWarningTime(-1);
			System.out.println("Alarm countdown: " + home.getWarningTime());
			System.out.println("Is house still armed?:"+home.getArmStatus());
			if (home.getWarningTime() <= 0 && home.getArmStatus())
			{	
				System.out.println("test2");
				home.modifyWarningTime(-1);
				home.setSMSsentTimestamp(LocalDateTime.now());
				alarm(home);					
			}
		}
		warningHomes.removeIf(house -> house.getWarningTime() <= 0 || !house.getArmStatus());
	}
	
	
	/**
	 * Check if any devices have been seen in certain period of times, and if not, combine them for each house.	
	 */
	public void checkDevices() 
	{
		LocalDateTime now = LocalDateTime.now();
		//Filter if a device exceeds the limits set for its last seen time
		List<Device> devices = deviceDB.filter(device ->  device.getLastSignalDate() != null
					&& Duration.between(device.getLastSignalDate(),now).toMillis() > LAST_TRANSMIT_LIMIT);
	

        List<Device> testDevices = deviceDB.filter(device ->  device.getLastSignalDate() != null);
		System.out.println(testDevices);
		for (Device device : testDevices) {
			System.out.println("Time since last seen: "+ Duration.between(device.getLastSignalDate(), now).toMillis());
		}
		//Combine those message if such exist, we do not want 2 messages about the same house for different devices
		Hashtable<HomeID, List<DeviceID>> hashtable = new Hashtable<HomeID, List<DeviceID>>();
		for (Device device : devices)
		{ 
			checkDevicesHashtable(device.getComponentID(), device.getHomeID(), hashtable);
		}
		//For each of them that exist, check if they dont have a backoff period
		List<Home> homes = homeDB.filter(house -> hashtable.containsKey(house.getHomeID()) && homeNoBackoffPeriodExists(house));
		for (Home home : homes)
		{
			home.setSMSsentTimestamp(LocalDateTime.now());
			handleHomeFailureDeviceMsg(home, hashtable);
		}		
	}
	
	/**
	 * Add the house to a hashtable.
	 * @param deviceID
	 * @param homeID
	 * @param hashtable
	 */
	private static void checkDevicesHashtable(DeviceID deviceID, HomeID homeID,
			Hashtable<HomeID, List<DeviceID>> hashtable) {
		if (hashtable.containsKey(homeID))
		{
			List<DeviceID> list = hashtable.get(homeID);
			list.add(deviceID);
			hashtable.put(homeID, list);
		}
		else
		{
			ArrayList<DeviceID> list = new ArrayList<DeviceID>();
			list.add(deviceID);
			hashtable.put(homeID, list);
		}		
	}
	
	/**
	 * Transmit the failure depending on the house arming status
	 * @param home
	 * @param hashtable
	 */
	private void handleHomeFailureDeviceMsg(Home home, Hashtable<HomeID, List<DeviceID>> hashtable) {
		if (home.getArmStatus()) {
			home.setWarningTime(ALARM_DELAY);
			alarm(home);
			
		} else {
			List<PhoneAddress> numbers = phoneAddrDB.filter(number -> number.getHomeID().equals(home.getHomeID()));
			StringBuilder sb = new StringBuilder();
			sb.append("Device failure on component: ");

			
			for (DeviceID id : hashtable.get(home.getHomeID())) {
				sb.append(id.getID());
				sb.append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append(".");
			home.setWarningTime(ALARM_DELAY);
			sendMsg(numbers, sb.toString());
		}
		
	}
	
	
	/**
	 * Check if there is a backoff period or not
	 * @param home
	 * @return
	 */
	private boolean homeNoBackoffPeriodExists(Home home) {
		return (timeBetweenSMS <= 0) ? true : checkNoHomeBackoffPeriod(home);
	}
	
	
	/**
	 * Check if the houses backoff period is expired or not.
	 * @param home
	 * @return
	 */
	private boolean checkNoHomeBackoffPeriod(Home home) {
		if (home.getSMSTimestamp() != null){
		System.out.println("Time since last SMS: "+Duration.between(home.getSMSTimestamp(), LocalDateTime.now()).toMillis());
}
		return home.getSMSTimestamp() == null || home.getSMSTimestamp() != null && Duration.between(home.getSMSTimestamp(), LocalDateTime.now()).toMillis() >= timeBetweenSMS;
	}
	
	/**
	 * Transmit a message to the device via Chirpstack
	 * @param elem
	 * @param devEUI
	 */
	public void transmitMessage(JsonObject elem, String devEUI) { 
		ChirpstackRequest.sendDownlink(devEUI, elem.toString());
		
	}
	
	/**
	 * Handle the request found in the message based on the payload
	 * @param data
	 * @return
	 */
	public Optional<JsonObject> handleRequest(JsonObject data) {
		JsonObject output = new JsonObject();
		
		JsonObject input = data;
		String devEUI = input.get("devEUI").getAsString();
		
		String encodedString = null;
		
		Predicate<Device> filterFunction = n -> n.getComponentID().getID().equals(devEUI);
        Optional<Device> optDevice = deviceDB.get(filterFunction);
        if (!optDevice.isPresent()) {
        	return Optional.empty();
        }
        Device device = optDevice.get();
		device.updateLastDate(LocalDateTime.now());
		try {
			encodedString = input.get("data").getAsString();
		} catch (NullPointerException e) {
			System.out.println(devEUI+" joined the server");
			device.updateMessageCount(0);
			return Optional.empty();
		}
		
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        
        Predicate<Home> filterFunctionHome = n -> n.getHomeID().getID().equals(device.getHomeID().getID());
        Optional<Home> optHome = homeDB.get(filterFunctionHome);
        if (!optHome.isPresent()) {
        	return Optional.empty();
        }
        
       Home home = optHome.get();
       boolean deviceArmStatus = decodedBytes[0]== 0x02;
       boolean panicRecv = decodedBytes[1]== 0x02;
       boolean statusRecv = decodedBytes[2]== 0x02;
       
       int[] pw = new int[decodedBytes.length-3];
       for (int i = 0; i < pw.length; i++) {
    	   pw[i] = decodedBytes[i+3];
       }
       
       System.out.println("panic: " + panicRecv);
       System.out.println("Alarmstatus: " + statusRecv);
       System.out.println("armStatus: " + deviceArmStatus);
       System.out.println("password: ");
       for (int i = 0; i < pw.length; i++) {
    	   System.out.print(pw[i] + " ");
       }
       System.out.println("\nHouse Arm Status: " + home.getArmStatus());
       	 if (Arrays.stream(pw).count() > 0) {
    	   int counter = input.get("fCnt").getAsInt();
    	   if (handlePW(home, counter, pw)) {
    		   System.out.println("Login succeeded");
    		   output = handleLogin(home, device);
    		   return Optional.of(output);
    	   } else {
    		   System.out.println("login failed");
    	   }
       } else if (statusRecv && panicRecv) {
    	   home.setSMSsentTimestamp(LocalDateTime.now());
    	   alarm(home);
    	   
       } else if (statusRecv && home.getArmStatus()) {
    	   handleStartAlarm(home);
    	   
       } else if (deviceArmStatus != home.getArmStatus()) {
           output = getTransmitMessage(home, device);
		   return Optional.of(output);
       }
       
       return Optional.empty();
	}
	
	/**
	 * Check if a password is the correct one for a house.
	 * @param home
	 * @param counter
	 * @param password
	 * @return
	 */
	private boolean handlePW(Home home, int counter, int[] password) {
 	   byte[] salt = home.getSalt();
 	   salt[15] = (byte) counter;
 	   salt[14] = (byte) (counter >> 8);
 	   hash.initialize(salt);
 	   char[] pw = home.getPassword().toCharArray();
 	   for (int i = 0; i < 4; i++) {
 		   hash.updateHash((byte) pw[i]);
 	   }
 	   
 	   byte[] bytesPW = SipHash_2_4.longToBytes(hash.finish());
 	   for (int i = 0; i < bytesPW.length; i++) {
 		   if (bytesPW[i] != password[i]) {
 			   System.out.println("handlePW["+i+"]"+bytesPW[i]);
 			   return false;
 		   }
 	   }

		return true;
	}
	
	/**
	 * On a login handle the parameters as needed.
	 * @param output
	 * @param home
	 * @return 
	 */
	private JsonObject handleLogin(Home home, Device device) {
		 home.toggleArm();
		 
		 JsonObject output = getTransmitMessage(home, device);
		 HomeID id = home.getHomeID();
		 
		 List<Device> deviceList = deviceDB.filter(dev -> dev.getHomeID().equals(id));
		 for (Device dev : deviceList) {
				dev.updateLastDate(null);
		 }
		 
		 device.updateMessageCount(device.getMessageCount()+1);
		 
		 return output;
	}
	
	private JsonObject getTransmitMessage(Home home, Device device) {
		 JsonObject content = new JsonObject();
		 byte trueByte = (byte) 2;
		 byte falseByte = (byte) 1;
		 
		 byte[] payload = new byte[3];
		 payload[0] = home.getArmStatus() ? trueByte : falseByte;
		 payload[1] = falseByte;
		 payload[2] = falseByte;
		 String data = Base64.getEncoder().encodeToString(payload);
		 
		 content.addProperty("confirmed", home.getArmStatus());
		 content.addProperty("data", data);
		 content.addProperty("devEUI", device.getComponentID().getID());
		 content.addProperty("fPort", 1);
		 int fCnt = device.getMessageCount();
		 content.addProperty("fCnt", fCnt);
		 
		 JsonObject output = new JsonObject();
		 String info = content.toString();
		 output.addProperty("deviceQueueItem", info);
		 
		 return output;
	}


	/**
	 * Transmit an alarm
	 * @param home
	 */
	public void alarm(Home home) {
		System.out.println("ALARM IS GOING ON!");
		List<PhoneAddress> numbers = phoneAddrDB.filter(phone -> phone.equals(phone));
		String content = "Hey everyone, there is a break-in at " + home.getAddress() + " please respond quickly.";
		sendMsg(numbers, content);
	}
	
	/**
	 * Send a message to a list of given number
	 * @param numbers
	 * @param msg
	 */
	public void sendMsg(List<PhoneAddress> numbers, String msg) {
		for (PhoneAddress number : numbers) {
			sender.sendToNumber(number.getNumber(), msg);
		}
	}
	
	/**
	 * Adds a house to warning houses and starts an alarm after the duration if no backoff period exists. 
	 * @param home
	 */
	private void handleStartAlarm(Home home) {
		if (homeNoBackoffPeriodExists(home)) {
			warningHomes.add(home);
			home.setSMSsentTimestamp(LocalDateTime.now());
			home.setWarningTime(ALARM_DELAY);
		}
	}


}
