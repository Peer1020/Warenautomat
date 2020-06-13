package warenautomat;

public class Muenzsaeule {

	final static int kapazitaet_Muenzsaeule = 100;
	private double mMuenzart;
	private int mAnzahlMuenzen;
	private int mAktuelleAnzahlMuenzen;
	private boolean mIstFalsch;

	public Muenzsaeule(double pMuenzart, int pAnzahlMuenzen) {
		mMuenzart = pMuenzart;
		mAnzahlMuenzen = pAnzahlMuenzen;
		mAktuelleAnzahlMuenzen = pAnzahlMuenzen;
		mIstFalsch = false;
	}

	public double getMuenzart() {
		return mMuenzart;
	}

	public int getAnzahlMuenzen() {
		return mAktuelleAnzahlMuenzen;
	}

	public boolean entferneMuenzen(int pAnzahl) {
		if (pAnzahl == 0) {
			return false;
		}
		if (mAktuelleAnzahlMuenzen - pAnzahl >= 0) {
			mAktuelleAnzahlMuenzen = mAktuelleAnzahlMuenzen - pAnzahl;
		} else {
			mAktuelleAnzahlMuenzen = 0;
		}
		return true;
	}

	public boolean fuegeMuenzeHinzu(int pAnzahl) {
		if (pAnzahl == 0) {
			return false;
		}
		if (mAktuelleAnzahlMuenzen + pAnzahl <= Muenzsaeule.kapazitaet_Muenzsaeule) {
			mAktuelleAnzahlMuenzen = mAktuelleAnzahlMuenzen + pAnzahl;
		} else {
			mAktuelleAnzahlMuenzen = Muenzsaeule.kapazitaet_Muenzsaeule;
		}
		return true;
	}

	public void speichernMuenze() {
		mAnzahlMuenzen = mAktuelleAnzahlMuenzen;
		mIstFalsch = false;
	}

}
