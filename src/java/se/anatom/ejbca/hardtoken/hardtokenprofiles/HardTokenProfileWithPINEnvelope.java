package se.anatom.ejbca.hardtoken.hardtokenprofiles;


import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.StringReader;

import se.anatom.ejbca.ra.UserAdminData;


/**
 * HardTokenProfileWithPINEnvelope is a basic class that should be inherited by all types
 * of hardtokenprofiles that should have PIN envelope functionality.
 * 
 * @version $Id: HardTokenProfileWithPINEnvelope.java,v 1.2 2004-01-27 10:10:47 herrvendil Exp $
 */
public abstract class HardTokenProfileWithPINEnvelope extends HardTokenProfile implements IPINEnvelopeSettings{
		
	
	// Protected Constants
	protected static final String PINENVELOPETYPE                = "pinenvelopetype";
	protected static final String PINENVELOPEFILENAME            = "pinenvelopefilename";
	protected static final String PINENVELOPEDATA                = "pinenvelopetdata";
	protected static final String PINENVELOPECOPIES              = "pinenvelopetcopies";
	protected static final String VISUALVALIDITY                 = "visualvalidity";

	private SVGImageManipulator envelopesvgimagemanipulator = null;
			
    // Default Values
    public HardTokenProfileWithPINEnvelope() {
      super();
      
      setPINEnvelopeType(IPINEnvelopeSettings.PINENVELOPETYPE_GENERALENVELOBE);
      setPINEnvelopeTemplateFilename("");
	  setNumberOfPINEnvelopeCopies(1);
      setVisualValidity(356);            
      
    }

    // Public Methods mostly used by PrimeCard
    
    
    public void upgrade(){
      // Perform upgrade functionality 
      super.upgrade(); 
    }
    


	/** 
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#getPINEnvelopeType()
	 */
	public int getPINEnvelopeType() {
		return ((Integer) data.get(PINENVELOPETYPE)).intValue();
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#setPINEnvelopeType(int)
	 */
	public void setPINEnvelopeType(int type) {
	  data.put(PINENVELOPETYPE, new Integer(type));		
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#getPINEnvelopeTemplateFilename()
	 */
	public String getPINEnvelopeTemplateFilename() {		
		return (String) data.get(PINENVELOPEFILENAME);
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#setPINEnvelopeTemplateFilename(java.lang.String)
	 */
	public void setPINEnvelopeTemplateFilename(String filename) {
	  data.put(PINENVELOPEFILENAME, filename);		
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#getPINEnvelopeData()
	 */
	public String getPINEnvelopeData() {		
		return (String) data.get(PINENVELOPEDATA);
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#setPINEnvelopeData(java.lang.String)
	 */
	public void setPINEnvelopeData(String templatedata) {
	  data.put(PINENVELOPEDATA, templatedata);	
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#getNumberOfPINEnvelopeCopies()
	 */
	public int getNumberOfPINEnvelopeCopies() {		
		return ((Integer) data.get(PINENVELOPECOPIES)).intValue();
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#setNumberOfPINEnvelopeCopies(int)
	 */
	public void setNumberOfPINEnvelopeCopies(int copies) {		
	  data.put(PINENVELOPECOPIES, new Integer(copies));	
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#getVisualValidity()
	 */
	public int getVisualValidity(){
	  return ((Integer) data.get(VISUALVALIDITY)).intValue();	
	}

	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#setVisualValidity(int)
	 */
	public void setVisualValidity(int validity){
	  data.put(VISUALVALIDITY, new Integer(validity));	
	}


	/**
	 * @see se.anatom.ejbca.hardtoken.hardtokenprofiles.IPINEnvelopeSettings#printPINEnvelope(se.anatom.ejbca.ra.UserAdminData, java.lang.String[], java.lang.String[], java.lang.String, java.lang.String)
	 */
	public Printable printPINEnvelope(UserAdminData userdata, String[] pincodes, String[] pukcodes, String hardtokensn, String copyoftokensn) throws IOException, PrinterException{
		Printable returnval = null;
	  
		if(getPINEnvelopeData() != null){
			if(envelopesvgimagemanipulator == null)
			  envelopesvgimagemanipulator = new SVGImageManipulator(new StringReader(getPINEnvelopeData()),
														  getVisualValidity(),
														  getHardTokenSNPrefix()); 
														
		  returnval = envelopesvgimagemanipulator.print(userdata, pincodes, pukcodes, hardtokensn, copyoftokensn); 														
		}
	  
	  
		return returnval;	
	}

    

}
