package warenautomat;

import java.time.LocalDate;

public class Fach {
	
	public Ware mWare;
	
	public void wareEinlesen(String pWarenName, double pPreis, 
			LocalDate pVerfallsDatum){
		mWare = new Ware(pWarenName, pPreis, pVerfallsDatum);	
	}

	public Ware getWare() {
		return mWare;
	}

	public void setWare(Ware pWare) {
		mWare=pWare;		
	}
	
	
}
