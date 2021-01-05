package nwa.devices;

import java.time.LocalDateTime;

import dtu.home.HomeID;

public interface Device
{
	public DeviceID getComponentID();
	public HomeID getHomeID();
	public DeviceEnum getComponentType();
	public LocalDateTime getLastSignalDate();
	public void updateLastDate(LocalDateTime localDateTime);
	public int getMessageCount();
	public void updateMessageCount(int newVal);
	public void changeHomeID(HomeID homeID);
	
}
