package warenautomat;

import java.time.LocalDate;

public class Ware {

	private String mWarenName;
	private double mPreis; 
	private LocalDate mVerfallsDatum;
	private LocalDate mVerkausdatum;
	
	public Ware(String pWarenName, double pPreis, LocalDate pVerfallsDatum) {
		mWarenName =pWarenName;
		mPreis=pPreis;
		mVerfallsDatum=pVerfallsDatum;
		mVerkausdatum=null;
	}
	
	public String getWarenname() {
		return mWarenName;
	}
	
	public LocalDate getVerfallsDatum() {
		return mVerfallsDatum;
	}
	
	public double getPreis() {
		return mPreis;
	}
	
	public void setVerkausdatum(LocalDate pVerkausdatum) {
		mVerkausdatum=pVerkausdatum;
	}
	
	public LocalDate getVerkausdatum() {
		return mVerkausdatum;
	}
	
	
}
