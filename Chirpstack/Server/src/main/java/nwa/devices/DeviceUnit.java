package nwa.devices;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import nwa.home.HomeID;

/**
 * The component device, originally intended to be one type of device, in its current state it encomppases more.
 */
public class DeviceUnit implements Device, Serializable
{
	private static final long serialVersionUID = 3930834081472801135L;
	private DeviceID id;
	private HomeID homeID;
	private LocalDateTime date;
	private LocalDate firstMessageOfTheDay;
	private int dailyMessageCount = 0;
	
	/**
	 * Create a component using the TTN identifier and the homeID for house to which it belongs
	 * @param id
	 * @param homeID
	 */
	public DeviceUnit(String id, HomeID homeID)
	{
		this.id = new DeviceIDValue(id);
		this.homeID = homeID;
		this.firstMessageOfTheDay = LocalDate.now();
	}
	
	/**
	 * Returns the component ID for the device
	 */
	public DeviceID getComponentID()
	{
		return id;
	}
	/**
	 * Return sthe homeID for the device
	 */
	public HomeID getHomeID()
	{
		return homeID;
	}
	
	/**
	 * Get the type of alarm
	 */
	public DeviceEnum getComponentType()
	{
		return DeviceEnum.SIGNAL_ALARM;
	}
	
	/**
	 * Return the last time the component was seen
	 */
	public LocalDateTime getLastSignalDate()
	{
		return date;
	}
	
	/**
	 * Set the last time this object was seen.
	 */
	public void updateLastDate(LocalDateTime date)
	{
		this.date = date;
	}

	/**
	 * Get how many messages were recieved today.
	 */
	public int getMessageCount() {
		return dailyMessageCount;
	}
	
	/**
	 * Set the amount of messages recieved.s
	 */
	public void updateMessageCount(int newVal) {
		dailyMessageCount = newVal;
	}

	@Override
	public void changeHomeID(HomeID homeID) {
		this.homeID = homeID;
		
	}
}
