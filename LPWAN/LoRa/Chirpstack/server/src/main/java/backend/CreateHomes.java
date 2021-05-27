package backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import nwa.database.Database;
import nwa.database.DatabaseArrayList;
import nwa.home.Home;
import nwa.home.HomeID;
import nwa.home.HomeIDValue;
import nwa.home.HomeUnit;

public class CreateHomes {

	public static void main() { 
		
		Database<Home> houseDB = new DatabaseArrayList<Home>();

		//Salt example for file with house and two devices
		byte[] salt = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x00, 0x00};
		HomeID houseID1 = new HomeIDValue("p-40");
		Home sebroom = new HomeUnit("Nybrovej 304, 1, P-40", houseID1, "1234", salt);
		HomeID houseID2 = new HomeIDValue("PR-even right kitchen");
		Home kitchen = new HomeUnit("Nybrovej 304, 1, PR-even right kitchen", houseID2, "1234", salt);
		
		
		houseDB.add(sebroom);
		houseDB.add(kitchen);

		
		FileOutputStream f1;
		try {
			f1 = new FileOutputStream(new File("database_home.txt"));
			ObjectOutputStream  o1 = new ObjectOutputStream (f1);
			
			o1.writeObject(houseDB);

			
			o1.close();
			f1.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

}
