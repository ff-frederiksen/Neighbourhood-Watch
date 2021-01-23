package nwa.home;

import java.io.Serializable;

import nwa.home.HomeID;

public interface PhoneAddress extends Serializable
{
	public String getNumber();
	public HomeID getHomeID();

}
