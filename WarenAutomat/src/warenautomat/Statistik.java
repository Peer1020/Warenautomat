package warenautomat;

import java.time.LocalDate;
import java.util.ArrayList;

public class Statistik {

	private ArrayList<Ware> mWarenbezug=new ArrayList<Ware>();
	
	public ArrayList<Ware> gibWarenbezug() {
		return mWarenbezug;
	}

	public int berechneAnzahlWaren(String pName, LocalDate pDatum) {
		int totalWaren=0;
		for(Ware ware: mWarenbezug) {
			if(ware.getWarenname().equals(pName)&& ware.getVerkausdatum().isAfter(pDatum)) {
				totalWaren++;
			}
		}
		return totalWaren;
	}

	public void erfasseWarenbezug(Ware pWare) {
		mWarenbezug.add(pWare);
		
	}

}
