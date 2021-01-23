package nwa.home;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HomeUnit implements Home, Serializable
{

	/**
	 * 
	 */
	private String password;
	private byte[] salt;
	private static final long serialVersionUID = -4110152058379289436L;
	private HomeID id;
	private int warningTime;
	private boolean armStatus = false;
	private String address;
	private LocalDateTime smsStamp;
	
	/**
	 * The address for the house, the ID should be the unique object, password should consist of the numbers 0-9.
	 * The salt should also be set, remember that only the first 14 bytes should be used in the 16 byte array.
	 * @param address
	 * @param ID
	 * @param password
	 * @param salt
	 */
	public HomeUnit(String address, HomeID ID, String password, byte[] salt)
	{
		this.address = address;
		this.id = ID;
		this.password = password;
		this.salt = salt;
	}
	
	/**
	 * Return the address for the house	
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * Return the HomeID for the house
	 */
	public HomeID getHomeID() 
	{
		return id;
	}

	/**
	 * Return the house arming status
	 */
	public boolean getArmStatus()
	{
		return armStatus;
	}

	/**
	 * Toogle the arming status
	 */
	public void toggleArm()
	{
		armStatus = !armStatus;
	}


	/**
	 * Return the warning time
	 */
	public int getWarningTime() {
		return warningTime;
	}
	
	/**
	 * Set the warning time to a specific value
	 */
	public void setWarningTime(int value)
	{
		warningTime = value;
	}
	
	/**
	 * Modify the warning time
	 */
	public void modifyWarningTime(int value) {
		warningTime += value;
		
	}


	/**
	 * Set the timeStamp for the timeStamp
	 */
	public void setSMSsentTimestamp(LocalDateTime date) {
		smsStamp = date;
		
	}

	/**
	 * Get the timeStamp for the last smsSent
	 */
	public LocalDateTime getSMSTimestamp() {
		return smsStamp;
	}

	/**
	 * Get the device password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * Get the salt belonging to the device.
	 */
	public byte[] getSalt() {
		return salt;
	}

	@Override
	public void changePassword(String pin) {
		this.password=pin;
		
	}


}
