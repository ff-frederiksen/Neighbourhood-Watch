package nwa.home;

import java.io.Serializable;

import nwa.home.HomeID;

public class PhoneAddressImplementation implements PhoneAddress, Serializable
{

	private static final long serialVersionUID = 3549641719780977811L;
	private String number;
	private HomeID id;
	/**
	 * Ties a number together with a houseID
	 * @param phoneNumber
	 * @param id
	 */
	public PhoneAddressImplementation(String phoneNumber, HomeID id)
	{
		number = phoneNumber;
		this.id = id;
	}
	
	/**
	 * Get the number belonging to this phoneAddress
	 */
	public String getNumber()
	{
		return number;
	}
	
	/**
	 * Get the id of the house
	 */
	public HomeID getHomeID()
	{
		return id;
	}

}
