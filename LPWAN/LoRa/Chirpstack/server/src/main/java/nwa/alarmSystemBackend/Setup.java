package nwa.alarmSystemBackend;

import java.util.List;

import nwa.database.Database;
import nwa.devices.Device;
import nwa.home.Home;


/**
 * Some basic setup structure for the arm status and the devices
 */
public class Setup {

	/**
	 * Reset the value
	 * @param deviceDB
	 */
	public void resetAlarmLastSeen(Database<Device> deviceDB)
	{
		List<Device> devices = deviceDB.filter(device -> device.equals(device));
		for (Device device : devices)
		{
			device.updateLastDate(null);
		}
	}
	
	/**
	 * Resets the values for the arm status on startup.
	 * @param homeDB
	 */
	public void resetHomeArm(Database<Home> homeDB)
	{
		List<Home> homes = homeDB.filter(house -> house.equals(house));
		for (Home home : homes) {
			if (home.getArmStatus())
				home.toggleArm();
		}
	}
	
}
