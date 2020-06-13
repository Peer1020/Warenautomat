package warenautomat;

import java.util.ArrayList;

import warenautomat.SystemSoftware;

/**
 * Die Kasse verwaltet das eingenommene Geld sowie das Wechselgeld. <br>
 * Die Kasse hat fünf Münz-Säulen für: <br>
 * - 10 Rappen <br>
 * - 20 Rappen <br>
 * - 50 Rappen <br>
 * - 1 Franken <br>
 * - 2 Franken <br>
 */
public class Kasse {

	/**
	 * Standard-Konstruktor. <br>
	 * Führt die nötigen Initialisierungen durch.
	 */
	private Muenzsaeule[] mMuenzsaeule;
	final static boolean dry_run = true;
	final static int oeffnenMod = 1;
	private int restgeldMod = 2;
	final static double muenzEinheiten[] = { 0.10, 0.20, 0.50, 1.00, 2.00 };
	private double mEingabeBetrag;
	private Statistik mStatistik;

	public Kasse() {
		int totalMuenzen = muenzEinheiten.length;
		mMuenzsaeule = new Muenzsaeule[totalMuenzen];
		for (int i = 0; i < totalMuenzen; i++) {
			mMuenzsaeule[i] = new Muenzsaeule(muenzEinheiten[i], 0);
		}
		mEingabeBetrag = 0.0;
		mStatistik = new Statistik();
	}

	/**
	 * Diese Methode wird aufgerufen nachdem das Personal beim Verwalten des
	 * Wechselgeldbestand die Münzart und die Anzahl der Münzen über die Tastatur
	 * eingegeben hat (siehe Use-Case "Wechselgeldbestand (Münzbestand) verwalten").
	 * 
	 * @param pMuenzenBetrag Der Betrag der Münzart in Franken.
	 * @param pAnzahl        Die Anzahl der Münzen. Bei der Entnahme von Münzen als
	 *                       entsprechender negativer Wert.
	 * @return Anzahl der Münzen welche hinzugefügt resp. entnommen werden (bei
	 *         Entnahme als negativer Wert). <br>
	 *         Im Normalfall entspricht dieser Wert dem Übergabeparameter
	 *         <code>pAnzahl</code>. <br>
	 *         Er kann kleiner sein falls beim Hinzufügen in der Münzsäule zu wenig
	 *         Platz vorhanden ist oder wenn bei der Entnahme ein grössere Anzahl
	 *         angegeben wurde als tatsächlich in der Münzsäule vorhanden ist. <br>
	 *         Wenn ein nicht unterstützter Münzbetrag übergeben wurde: -200
	 */
	public int verwalteMuenzbestand(double pMuenzenBetrag, int pAnzahl) {
		boolean muenzeVorhanden = false;
		int totalMuenzDifferenz = 0;
		for (int i = 0; i < mMuenzsaeule.length; i++) {
			if (getWertMuenzen(mMuenzsaeule[i].getMuenzart()) == getWertMuenzen(pMuenzenBetrag)) {
				int startAnzahl = mMuenzsaeule[i].getAnzahlMuenzen();
				if (pAnzahl < 0) {
					mMuenzsaeule[i].entferneMuenzen(Math.abs(pAnzahl));
					totalMuenzDifferenz = mMuenzsaeule[i].getAnzahlMuenzen() - startAnzahl;
				} else {
					mMuenzsaeule[i].fuegeMuenzeHinzu(pAnzahl);
					totalMuenzDifferenz = mMuenzsaeule[i].getAnzahlMuenzen() - startAnzahl;
				}
				muenzeVorhanden = true;
				break;
			}
		}
		if (!muenzeVorhanden) {
			return -200;
		}
		return totalMuenzDifferenz;
	}

	/**
	 * Diese Methode wird aufgerufen nachdem das Personal beim Geldauffüllen den
	 * Knopf "Bestätigen" gedrückt hat (siehe Use-Case "Wechselgeldbestand
	 * (Münzbestand) verwalten"). <br>
	 * Verbucht die Münzen gemäss dem vorangegangenen Aufruf der Methode
	 * <code>verwalteMuenzbestand()</code>.
	 */
	public void verwalteMuenzbestandBestaetigung() {
		for (int i = 0; i < mMuenzsaeule.length; i++) {
			mMuenzsaeule[i].speichernMuenze();
			SystemSoftware.zeigeMuenzenInGui(mMuenzsaeule[i].getMuenzart(), mMuenzsaeule[i].getAnzahlMuenzen());
		}
	}

	/**
	 * Diese Methode wird aufgerufen wenn ein Kunde eine Münze eingeworfen hat. <br>
	 * Führt den eingenommenen Betrag entsprechend nach. <br>
	 * Stellt den nach dem Einwerfen vorhandenen Betrag im Kassen-Display dar. <br>
	 * Eingenommenes Geld steht sofort als Wechselgeld zur Verfügung. <br>
	 * Die Münzen werden von der Hardware-Kasse auf Falschgeld, Fremdwährung und
	 * nicht unterstützte Münzarten geprüft, d.h. diese Methode wird nur aufgerufen
	 * wenn ein Münzeinwurf soweit erfolgreich war. <br>
	 * Ist die Münzsäule voll (d.h. 100 Münzen waren vor dem Einwurf bereits darin
	 * enthalten), so wird mittels
	 * <code> SystemSoftware.auswerfenWechselGeld() </code> unmittelbar ein
	 * entsprechender Münz-Auswurf ausgeführt. <br>
	 * Hinweis: eine Hardware-Münzsäule hat jeweils effektiv Platz für 101 Münzen.
	 * 
	 * @param pMuenzenBetrag Der Betrag der neu eingeworfenen Münze in Franken.
	 * @return <code> true </code>, wenn er Einwurf erfolgreich war. <br>
	 *         <code> false </code>, wenn Münzsäule bereits voll war.
	 */
	public boolean einnehmen(double pMuenzenBetrag) {
		for (int i = 0; i < mMuenzsaeule.length; i++) {
			if (getWertMuenzen(mMuenzsaeule[i].getMuenzart()) == getWertMuenzen(pMuenzenBetrag)) {
				return verarbeiteMuenze(mMuenzsaeule[i], 1);
			}
		}
		return false;

	}

	private boolean verarbeiteMuenze(Muenzsaeule pMuenzsaeule, int pAmount) {
		if (pMuenzsaeule.fuegeMuenzeHinzu(pAmount)) {
			int eingefuegtesGeld = getWertMuenzen(pMuenzsaeule.getMuenzart()) * pAmount;
			mEingabeBetrag = getDoubleValueMuenzen(getWertMuenzen(mEingabeBetrag) + eingefuegtesGeld);
			SystemSoftware.zeigeBetragAn(mEingabeBetrag);
			return true;
		}
		return false;
	}

	/**
	 * Bewirkt den Auswurf des Restbetrages.
	 */
	public void gibWechselGeld() {
		if (getWertMuenzen(mEingabeBetrag) != 0) {
			if (entferneMuenzeVonIntBetrag(getWertMuenzen(mEingabeBetrag), restgeldMod) == 0) {
				SystemSoftware.auswerfenWechselGeld(mEingabeBetrag);
				mEingabeBetrag = 0.0;
				SystemSoftware.zeigeBetragAn(mEingabeBetrag);
			} else {
				System.out.print("Error: Wechselgeld");
			}
			for (int i = mMuenzsaeule.length - 1; i >= 0; i--) {
				System.out.println("Restgeldanzahl: " + mMuenzsaeule[i].getAnzahlMuenzen() + "  Münzarten: "
						+ mMuenzsaeule[i].getMuenzart());
			}
		}
	}

	/**
	 * Gibt den Gesamtbetrag der bisher verkauften Waren zurück. <br>
	 * Analyse: Abgeleitetes Attribut.
	 * 
	 * @return Gesamtbetrag der bisher verkauften Waren.
	 */
	public double gibBetragVerkaufteWaren() {
		int wert = 0;
		ArrayList<Ware> warenbezug = mStatistik.gibWarenbezug();
		for (Ware ware : warenbezug) {
			wert += getWertMuenzen(ware.getPreis());
		}
		return getDoubleValueMuenzen(wert);

	}

	public boolean istAusreichendWechselgeldVorhanden(double pBetrag) {
		return entferneGeldvonMuenzsaeule(pBetrag, dry_run, oeffnenMod);
	}

	public boolean entferneGeldvonMuenzsaeule(double pBetrag, boolean pdry_run, int pModus) {
		int preis = getWertMuenzen(pBetrag);
		int restGeld = getWertMuenzen(mEingabeBetrag) - preis;
		if (!pdry_run) {
			mEingabeBetrag = getDoubleValueMuenzen(restGeld);
			SystemSoftware.zeigeBetragAn(mEingabeBetrag);
		}
		restGeld = entferneMuenzeVonIntBetrag(restGeld, pModus);
		return restGeld == 0;

	}

	private int entferneMuenzeVonIntBetrag(int pBetrag, int pModus) {
		for (int i = mMuenzsaeule.length - 1; i >= 0; i--) {
			int maxMuenzenInSaeule = (int) (pBetrag / getWertMuenzen(mMuenzsaeule[i].getMuenzart()));
			int anzahlMuenzen = mMuenzsaeule[i].getAnzahlMuenzen();
			if (maxMuenzenInSaeule > anzahlMuenzen) {
				maxMuenzenInSaeule = anzahlMuenzen;
			}
			if (maxMuenzenInSaeule > 0) {
				int tempPreis = pBetrag - (maxMuenzenInSaeule * getWertMuenzen(mMuenzsaeule[i].getMuenzart()));
				if (tempPreis == 0) {
					pBetrag = tempPreis;
					if (pModus == restgeldMod) {
						mMuenzsaeule[i].entferneMuenzen(maxMuenzenInSaeule);
					}
					break;
				} else if (tempPreis > 0) {
					pBetrag = tempPreis;
					if (pModus == restgeldMod) {
						mMuenzsaeule[i].entferneMuenzen(maxMuenzenInSaeule);
					}
				}

			}

		}
		return pBetrag;
	}

	public double getDoubleValueMuenzen(int pMuenze) {
		return pMuenze / 100.0;
	}

	public int getWertMuenzen(double pMuenze) {
		return (int) Math.round(pMuenze * 100);
	}

	public Statistik getStatistik() {
		return mStatistik;
	}

	public boolean istGenugGuthabenVorhanden(double pPreis) {
		int eingeworfeneMuenzen = getWertMuenzen(mEingabeBetrag);
		int wertWare = getWertMuenzen(pPreis);
		return eingeworfeneMuenzen >= wertWare;
	}

	public double getEingabeBetrag() {
		return mEingabeBetrag;
	}

}
