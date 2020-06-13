
package warenautomat;

import java.time.LocalDate;

public class Drehteller {
  
private Fach[] mFach;
	
	
	
  public Drehteller(int pmax_faecher) {
	  mFach=new Fach[pmax_faecher];
	  for (int i = 0; i < pmax_faecher; i++) {
		  mFach[i]=new Fach();		
	}
  }
  
  public void fachAuffuelen(int mFachNr, String pWarenName, double pPreis, 
			LocalDate pVerfallsDatum){
	  mFach[mFachNr].wareEinlesen(pWarenName, pPreis, pVerfallsDatum); 
  }
  
  public Fach getFach(int mFachNr) {
	  return mFach[mFachNr];
  }
  
  
}
