package warenautomat;

public class Bestellung {
	
	private int mGrenze;
	private int mBestellanzahl;
	private String mWarenname;
	
	public Bestellung(String pWarenname,int pGrenze,int pBestellanzahl) {
		mWarenname=pWarenname;
		mGrenze=pGrenze;
		mBestellanzahl=pBestellanzahl;
	}
	
	public int getGrenze() {
		return mGrenze;
	}
	
	public int getBestellanzahl() {
		return mBestellanzahl;
	}
	
	

}
