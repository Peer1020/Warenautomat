package warenautomat;

import java.time.LocalDate;

/**
 * Der Automat besteht aus 7 Drehtellern welche wiederum je aus 16 Fächer
 * bestehen. <br>
 * Der erste Drehteller und das jeweils erste Fach haben jeweils die Nummer 1
 * (nicht 0!). <br>
 * Im Weitern hat der Automat eine Kasse. Diese wird vom Automaten instanziert.
 */
public class Automat {

	private static final int NR_DREHTELLER = 7;
	private static final int max_faecher = 16;
	private Drehteller[] mDrehteller;
	private Kasse mKasse;
	private int mDrehtellerposition;

	/**
	 * Der Standard-Konstruktor. <br>
	 * Führt die nötigen Initialisierungen durch (u.a. wird darin die Kasse
	 * instanziert).
	 */
	public Automat() {
		mDrehteller = new Drehteller[NR_DREHTELLER];
		for (int i = 0; i < NR_DREHTELLER; i++) {
			mDrehteller[i] = new Drehteller(max_faecher);
		}
		mDrehtellerposition = 0;
		mKasse = new Kasse();
	}

	/**
	 * Füllt ein Fach mit Ware. <br>
	 * Wenn das Service-Personal den Automaten füllt, wird mit einem Bar-Code-Leser
	 * zuerst die Ware gescannt. <br>
	 * Daraufhin wird die Schiebe-Tür geöffnet. <br>
	 * Das Service-Personal legt die neue Ware ins Fach und schliesst das Fach. <br>
	 * Die Hardware resp. System-Software ruft die Methode
	 * <code> Automat.neueWareVonBarcodeLeser() </code> auf.
	 * 
	 * @param pDrehtellerNr  Der Drehteller bei welchem das Fach hinter der
	 *                       Schiebe-Türe gefüllt wird. <br>
	 *                       Nummerierung beginnt mit 1 (nicht 0)!
	 * @param pWarenName     Der Name der neuen Ware.
	 * @param pPreis         Der Preis der neuen Ware.
	 * @param pVerfallsDatum Das Verfallsdatum der neuen Ware.
	 */
	public void neueWareVonBarcodeLeser(int pDrehtellerNr, String pWarenName, double pPreis, LocalDate pVerfallsDatum) {
		if (pDrehtellerNr <= 7) {
			mDrehteller[pDrehtellerNr - 1].fachAuffuelen(mDrehtellerposition, pWarenName, pPreis, pVerfallsDatum);
			wechseleDrehteller(pDrehtellerNr-1,false);
		}

	}

	/**
	 * Gibt die Objekt-Referenz auf die <em> Kasse </em> zurück.
	 */
	public Kasse gibKasse() {
		return mKasse;
	}

	/**
	 * Wird von der System-Software jedesmal aufgerufen wenn der gelbe Dreh-Knopf
	 * gedrückt wird. <br>
	 * Die Applikations-Software führt die Drehteller-Anzeigen nach (Warenpreis,
	 * Verfallsdatum). <br>
	 * Das Ansteuern des Drehteller-Motors übernimmt die System-Software (muss nicht
	 * von der Applikations-Software gesteuert werden.). <br>
	 * Die System-Software stellt sicher, dass <em> drehen </em> nicht durchgeführt
	 * wird wenn ein Fach offen ist.
	 */
	public void drehen() {
		SystemSoftware.dreheWarenInGui();
		mDrehtellerposition++;
		if (mDrehtellerposition >= 16) {
			mDrehtellerposition = 0;
		}
		for (int i = 0; i < NR_DREHTELLER; i++) {
			wechseleDrehteller(i,false);
		}
		
	}

	private void wechseleDrehteller(int pDrehtellernummer, boolean pBestellen) {
		Ware ware = getWarenPosition(pDrehtellernummer, mDrehtellerposition);
		if (ware != null) {
			SystemSoftware.zeigeWareInGui(pDrehtellernummer + 1, ware.getWarenname(), ware.getVerfallsDatum());
			SystemSoftware.zeigeWarenPreisAn(pDrehtellernummer + 1, ware.getPreis());
			SystemSoftware.zeigeVerfallsDatum(pDrehtellernummer + 1,
					SystemSoftware.gibAktuellesDatum().isBefore(ware.getVerfallsDatum()) ? 1 : 2);
		} else {
			SystemSoftware.zeigeWareInGui(pDrehtellernummer + 1, "", SystemSoftware.gibAktuellesDatum());
			SystemSoftware.zeigeWarenPreisAn(pDrehtellernummer + 1, 0.0);
			SystemSoftware.zeigeVerfallsDatum(pDrehtellernummer + 1, 0);
		}
		if (pBestellen) {
			pruefeWarenbestellung(pDrehtellernummer,mDrehtellerposition);
		}
	}
	
	
	private Ware getWarenPosition(int pDrehtellernummer, int pAktuelleDrehtellerposition) {
		return mDrehteller[pDrehtellernummer].getFach(pAktuelleDrehtellerposition).getWare();
	}

	/**
	 * Beim Versuch eine Schiebetüre zu öffnen ruft die System-Software die Methode
	 * <code> oeffnen() </code> der Klasse <em> Automat </em> mit der
	 * Drehteller-Nummer als Parameter auf. <br>
	 * Es wird überprüft ob alles o.k. ist: <br>
	 * - Fach nicht leer <br>
	 * - Verfallsdatum noch nicht erreicht <br>
	 * - genug Geld eingeworfen <br>
	 * - genug Wechselgeld vorhanden <br>
	 * Wenn nicht genug Geld eingeworfen wurde, wird dies mit
	 * <code> SystemSoftware.zeigeZuWenigGeldAn() </code> signalisiert. <br>
	 * Wenn nicht genug Wechselgeld vorhanden ist wird dies mit
	 * <code> SystemSoftware.zeigeZuWenigWechselGeldAn() </code> signalisiert. <br>
	 * Wenn o.k. wird entriegelt (<code> SystemSoftware.entriegeln() </code>) und
	 * positives Resultat zurückgegeben, sonst negatives Resultat. <br>
	 * Es wird von der System-Software sichergestellt, dass zu einem bestimmten
	 * Zeitpunkt nur eine Schiebetüre offen sein kann.
	 * 
	 * @param pDrehtellerNr Der Drehteller bei welchem versucht wird die
	 *                      Schiebe-Türe zu öffnen. <br>
	 *                      Nummerierung beginnt mit 1 (nicht 0)!
	 * @return Wenn alles o.k. <code> true </code>, sonst <code> false </code>.
	 */
	public boolean oeffnen(int pDrehtellernummer) {
		
		System.out.println("Drehteller:: oeffnen(): mDrehtellerNr = "+pDrehtellernummer+ " / mFachvorOeffnung "+ 
		(mDrehtellerposition+1));
		
		if (istFachLeer(pDrehtellernummer - 1)) {
			return false;
		}
		if (istVerfallsdatumErreicht(pDrehtellernummer - 1)) {
			return false;
		}
		if (!istGenugGeldEingeworfen(pDrehtellernummer - 1)) {
			SystemSoftware.zeigeZuWenigGeldAn();
			return false;
		}
		if (!istGenugWechselgeldvorhanden(pDrehtellernummer - 1)) {
			SystemSoftware.zeigeZuWenigWechselGeldAn();
			return false;
		}
		
		mKasse.entferneGeldvonMuenzsaeule(getWarenPosition(pDrehtellernummer-1, 
				mDrehtellerposition).getPreis(), !Kasse.dry_run, Kasse.oeffnenMod);
		Fach fach =mDrehteller[pDrehtellernummer-1].getFach(mDrehtellerposition);
		Ware ware = fach.getWare();
		ware.setVerkausdatum(SystemSoftware.gibAktuellesDatum());
		mKasse.getStatistik().erfasseWarenbezug(ware);
		fach.setWare(null);	
		wechseleDrehteller(pDrehtellernummer-1,true);
		SystemSoftware.entriegeln(pDrehtellernummer);
		return true;
	}
	

	private boolean pruefeWarenbestellung(int pDrehtellernummer, int akutelleDrehtellerposition) {
		Ware ware = getWarenPosition(pDrehtellernummer, akutelleDrehtellerposition);
		if (ware != null&& ware.getWarenbestellung()!= null&& anzahlWare(ware.getWarenname()) <=
				ware.getWarenbestellung().getGrenze()) {
			SystemSoftware.bestellen(ware.getWarenname(), ware.getWarenbestellung().getBestellanzahl(),
					anzahlWare(ware.getWarenname()));	
			return true;						
		}
		return false;
		
		
	}

	private int anzahlWare(String warenname) {
		int totalWaren = 0;
		for (int i = 0; i < mDrehteller.length; i++) {
			for (int j = 0; j < max_faecher; j++) {
				Ware ware = getWarenPosition(i, j);
				if(ware != null && !SystemSoftware.gibAktuellesDatum().isBefore(ware.getVerfallsDatum())){
					totalWaren++;
				}
				
			}
		}
		return totalWaren;
	}

	private boolean istGenugWechselgeldvorhanden(int pDrehtellernummer) {
		return mKasse.istAusreichendWechselgeldVorhanden(getWarenPosition(
				pDrehtellernummer, mDrehtellerposition).getPreis());
	}

	private boolean istFachLeer(int pDrehtellernummer) {
		if (getWarenPosition(pDrehtellernummer, mDrehtellerposition) == null) {
			return true;
		}
		return false;
	}

	private boolean istVerfallsdatumErreicht(int pDrehtellernummer) {
		if (SystemSoftware.gibAktuellesDatum()
				.isBefore(getWarenPosition(pDrehtellernummer, mDrehtellerposition).getVerfallsDatum())) {
			return false;
		}
		return true;
	}

	private boolean istGenugGeldEingeworfen(int pDrehtellernummer) {
		return mKasse.istGenugGuthabenVorhanden(
				getWarenPosition(pDrehtellernummer, mDrehtellerposition).getPreis());
	}

	/**
	 * Gibt den aktuellen Wert aller im Automaten enthaltenen Waren in Franken
	 * zurück. <br>
	 * Analyse: <br>
	 * Abgeleitetes Attribut. <br>
	 * 
	 * @return Der totale Warenwert des Automaten.
	 */
	public double gibTotalenWarenWert() {
		int totalWaren = 0;
		for (int i = 0; i < mDrehteller.length; i++) {
			for (int j = 0; j < max_faecher; j++) {
				Ware ware = getWarenPosition(i, j);
				if (ware != null && SystemSoftware.gibAktuellesDatum().isBefore(ware.getVerfallsDatum())) {
					totalWaren += mKasse.getWertMuenzen(ware.getPreis());
				} else if(ware != null && !SystemSoftware.gibAktuellesDatum().isBefore(ware.getVerfallsDatum())) {
					totalWaren +=mKasse.getWertMuenzen(ware.getPreis()) / 100*20;
				}

			}
		}
		return mKasse.getDoubleValueMuenzen(totalWaren);

	}

	/**
	 * Gibt die Anzahl der verkauften Ware <em> pName </em> seit (>=) <em> pDatum
	 * </em> zurück.
	 * 
	 * @param pName  Der Name der Ware nach welcher gesucht werden soll.
	 * @param pDatum Das Datum seit welchem gesucht werden soll.
	 * @return Anzahl verkaufter Waren.
	 */
	public int gibVerkaufsStatistik(String pName, LocalDate pDatum) {
		return mKasse.getStatistik().berechneAnzahlWaren(pName,pDatum);
	}
	
	  /**
	   * Konfiguration einer automatischen Bestellung. <br>
	   * Der Automat setzt automatisch Bestellungen ab mittels
	   * <code> SystemSoftware.bestellen() </code> wenn eine Ware ausgeht.
	   * 
	   * @param pWarenName
	   *          Warenname derjenigen Ware, für welche eine automatische 
	   *          Bestellung konfiguriert wird.
	   * @param pGrenze
	   *          Ab welcher Anzahl von verkaufbarer Ware jeweils eine 
	   *          Bestellung abgesetzt werden soll.
	   * @param pAnzahlBestellung
	   *          Wieviele neue Waren jeweils bestellt werden sollen.
	   */
	  public void konfiguriereBestellung(String pWarenName, int pGrenze,
	                                     int pAnzahlBestellung) {
		  for (int i = 0; i < mDrehteller.length; i++) {
			  for (int j = 0; j < max_faecher; j++) {
				  Ware ware =getWarenPosition(i, j);
				  if(ware!=null&&pWarenName.equals(ware.getWarenname())) {
					  ware.aktualisiereBestellung(pWarenName,pGrenze,pAnzahlBestellung);
				  }
				
			}
			
		}
	    

	  }
	

}
