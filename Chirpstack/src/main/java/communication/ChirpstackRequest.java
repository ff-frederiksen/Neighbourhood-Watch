package communication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import backend.Server;
import nwa.devices.Device;
import nwa.home.Home;
import user.User;

public class ChirpstackRequest {
	private static JSONParser parser = new JSONParser();

	public static String getNetworkKey(String jwt, String devEUI) {
		String urlEnd = "/api/devices/"+devEUI+"/keys";
		String requestMethod = "GET";
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, null, authentificationProperty(jwt));
		
	
		try {
			JSONObject deviceKeys = (JSONObject) ((JSONObject) parser.parse(response)).get("deviceKeys");
			return deviceKeys.get("nwkKey").toString();
		} catch (NullPointerException |ParseException e) {
			return null;
		}
		
	}

	@SuppressWarnings({ "unchecked"})
	public static String giveNetworkKey(String jwt, String devEUI) {
		String urlEnd = "/api/devices/"+devEUI+"/keys";
		String requestMethod = "POST";
		
		JSONObject deviceKeys = new JSONObject();
		JSONObject networkKey = new JSONObject();
		
		networkKey.put("nwkKey",Server.byteStringGenerator(16));
		
		deviceKeys.put("deviceKeys", networkKey);
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, deviceKeys.toJSONString(), authentificationProperty(jwt));
		
		return response;
		
	}

	@SuppressWarnings("unchecked")
	public static String changeUserPassword(String jwt, String userID, String password) {
		String urlEnd = "/api/users/"+userID+"/password";
		String requestMethod = "PUT";
		
		JSONObject newCredentials = new JSONObject();
		newCredentials.put("userID", userID);
		newCredentials.put("password", password);
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, newCredentials.toJSONString(), authentificationProperty(jwt));
		
		return response;
	}

	public static String deleteUser(String jwt, String userID) {
		String urlEnd = "/api/users/"+userID;
		String requestMethod = "DELETE";
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, null, authentificationProperty(jwt));
		
		return response;
	}

	@SuppressWarnings("unchecked")
	public static String createUser(String jwt, String username, String password, String note, String email, boolean isAdmin) throws InterruptedException {
		String urlEnd = "/api/users";
		String requestMethod = "POST";
		
		JSONObject organizationrequest = new JSONObject();
		JSONArray organizationArray = new JSONArray();
		JSONObject organizationInfo = new JSONObject();
		organizationInfo.put("isAdmin", false);
		organizationInfo.put("isDeviceAdmin", true);
		organizationInfo.put("isGatewayAdmin", false);
		organizationInfo.put("organizationID", Server.organization_id);
		
		organizationArray.add(organizationInfo);
		organizationrequest.put("organizations", organizationArray);
		
		organizationrequest.put("password", password);
		
		JSONObject user = new JSONObject();
		
		user.put("email", email);
		user.put("isActive", true);
		user.put("isAdmin", isAdmin);
		user.put("note",note);
		user.put("sessionTTL", 0);
		
		organizationrequest.put("user", user);
		System.out.println(organizationrequest.toJSONString());
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod,  organizationrequest.toJSONString(), authentificationProperty(jwt));
		
		
		String id;
		try {
			id = ((JSONObject) parser.parse(response)).get("id").toString();
			return id;
		} catch (NullPointerException | ParseException e) {
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	public static String getDevice(String jwt, String devEUI) {
		String urlEnd ="/api/devices/"+devEUI;
		String requestMethod = "GET";
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, null, authentificationProperty(jwt));
		
		try {
			JSONObject deviceSummary = (JSONObject) parser.parse(response);
			JSONObject device = (JSONObject) deviceSummary.get("device");
			device.put("nwkKey", getNetworkKey(jwt, devEUI));
			return device.toJSONString();
		} catch (NullPointerException |ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public static String deleteDevice(String jwt, String devEUI) {
		String urlEnd ="/api/devices/"+devEUI;
		String requestMethod = "DELETE";
		
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, null, authentificationProperty(jwt));
		
		System.out.println();
		
		return response;		
	}

	public static String transferDevice(String jwt, Device device, Home transferHome) {
		String urlEnd ="/api/devices/"+device.getComponentID().getID();
		String requestMethod = "PUT";
		
		try {
			JSONObject deviceInfo = (JSONObject) parser.parse(getDevice(jwt, device.getComponentID().getID()));
			
			JSONObject tags = new JSONObject();
			tags.put("HomeID", transferHome.getHomeID().getID());
			
			deviceInfo.put("tags", tags);
			
			JSONObject newDevice = new JSONObject();
			newDevice.put("device", deviceInfo);
			
			String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, newDevice.toJSONString(), authentificationProperty(jwt));
			
			return response;
			
		} catch (NullPointerException |ParseException e) {
			return null;
		}
		
		
		
	}

	public static String createDevice(String jwt, String body) {
		String urlEnd = "/api/devices";
		String requestMethod = "POST";
		String response = ChirpstackRequest.getChirpstackResponse(urlEnd, requestMethod, body, authentificationProperty(jwt));
		return response;		
	}

	@SuppressWarnings("unchecked")
	public static String sendDownlink(String devEUI, String data) {
		
		try {
			String urlEnd = "/api/devices/"+devEUI+"/queue";
			JSONObject bodyData = (JSONObject) parser.parse(data);
			
			JSONObject queue = (JSONObject) parser.parse(bodyData.get("deviceQueueItem").toString());
			
			bodyData.put("deviceQueueItem", queue);
			System.out.println(devEUI);
			System.out.println("HERE: "+bodyData.toJSONString());
			
			String response = ChirpstackRequest.getChirpstackResponse(urlEnd,"POST",bodyData.toJSONString(), authentificationProperty(Server.api_key));
			
			System.out.println(response);
			JSONObject result = ((JSONObject) parser.parse(response));
			return (result.get("fCnt").toString());
		} catch (NullPointerException |ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getChirpstackResponse(String urlEnd, String requestMethod, String body, JSONObject requestProperties) {
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(Server.REST_URL+urlEnd).openConnection();
				connection.setRequestMethod(requestMethod);
				connection.setDoOutput(true);
	//			connection.setDoInput(true);
				StringBuilder response = null;
				
				if(!requestProperties.isEmpty()) {
					Object[] keys = requestProperties.keySet().toArray();
					for (Object k : keys) {
						String value = requestProperties.get(k).toString();
						connection.setRequestProperty(k.toString(), value);
					}
				}
				
				if (body != null) { // add body if existant
					OutputStream os = connection.getOutputStream();
					byte[] in = body.getBytes("UTF-8");
					os.write(in, 0, in.length);
				}
				
				if (connection.getResponseCode()!=200) {
					connection.disconnect();
					return null;
				}
				BufferedReader bfr = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				response = new StringBuilder();
				String s = null;
				
				while ((s=bfr.readLine())!=null) {
					response.append(s.trim());
				}
				
				connection.disconnect();
				return response.toString();
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
	
		}

	public static JSONArray getDevices(String jwt, int offset, int limit) {
		String urlEnd = "/api/devices?limit="+limit+"&offset="+offset+"&applicationID="+Server.application_id;
		
		
		System.out.println(urlEnd);
		String response = getChirpstackResponse(urlEnd,"GET",null, authentificationProperty(jwt));
			
		try {
			return ((JSONArray)(((JSONObject) parser.parse(response)).get("result")));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static JSONArray getUsers(String jwt, int offset, int limit) {
		String urlEnd = "/api/users?limit="+limit+"&offset="+offset;
			
		String response = getChirpstackResponse(urlEnd,"GET",null, authentificationProperty(jwt));
			
		try {
			return ((JSONArray)(((JSONObject) parser.parse(response)).get("result")));
		} catch (NullPointerException |ParseException e) {
			return null;
		}
	}
	
	public static JSONObject getUser(String jwt, String userID) {
		String urlEnd = "/api/users/"+userID;
			
		String response = getChirpstackResponse(urlEnd,"GET",null, authentificationProperty(jwt));
		try {
			return ((JSONObject)(((JSONObject) parser.parse(response)).get("user")));
		} catch (NullPointerException |ParseException e) {
			return null;
		}
	}

	public static String chirpstackLogin(String usr, String pw) {
		String body="{\"email\": \""+usr+"\",\"password\": \""+pw+"\"}";
		JSONObject item = new JSONObject();
		String response = getChirpstackResponse("/api/internal/login", "POST", body, item);
		try {
			return ((JSONObject) parser.parse(response)).get("jwt").toString();
		} catch (NullPointerException |ParseException e) {
			return null;
		}
		
	}

	public static String getJWT(String admin_usr, String admin_pw) {
		String jwt = chirpstackLogin(admin_usr, admin_pw);
		String urlEnd = "/api/internal/api-keys?limit=1&isAdmin=true";
		String method = "POST";

		JSONObject apikeyObject = new JSONObject();
		JSONObject keyInfo = new JSONObject();
		
		keyInfo.put("id","api_key");
		keyInfo.put("isAdmin", true);
		keyInfo.put("name", "Nabohjælp");
		
		apikeyObject.put("apiKey", keyInfo);
		
		String key = getChirpstackResponse(urlEnd, method, apikeyObject.toJSONString(), authentificationProperty(jwt));
		
		try {
			JSONObject keyObject = (JSONObject) parser.parse(key);
			return jwt;
		} catch (NullPointerException | ParseException e) {
			return null;
		}
	}

	private static JSONObject authentificationProperty(String jwt) {
		JSONObject requestProperties = new JSONObject();
		requestProperties.put("Grpc-Metadata-Authorization", jwt);
		return requestProperties;
	}

}
