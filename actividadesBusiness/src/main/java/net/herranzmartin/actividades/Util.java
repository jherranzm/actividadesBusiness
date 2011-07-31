package net.herranzmartin.actividades;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

public class Util {
	
	private static final boolean DEBUG = true;
	
	private static final Logger logger = Logger.getLogger(Util.class.getName());
	

	/* Browsers */
	public static final String CHROME = "CHROME";
	public static final String FIREFOX = "FIREFOX";
	public static final String IE = "MSIE";

	/* Encodings */
	public static final String ISO_8859_1 = "ISO-8859-1";
	public static final String UTF_8 = "UTF-8";
	
	public static final String NL = "\n";
    public static final String SEPARADOR = "* * * * * * * * * * * * * * * * * * * * * *";
    public static final String SEP_VERTICAL = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";

	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static  String getBrowser(HttpServletRequest request) {
		String browser = (String)request.getHeader("user-agent");
		String agente = IE;
		if(DEBUG)
			logger.info("browser:" + browser);
		
		if(browser.toUpperCase().indexOf(FIREFOX) != -1){ // Firefox
			agente = FIREFOX;
		}else if(browser.toUpperCase().indexOf(CHROME) != -1){ // Chrome
			agente = CHROME;
		}else if(browser.toUpperCase().indexOf(IE) != -1){ // IE
			agente = IE;
		}
		return agente;
	}
	
	
	/**
	 * @param request
	 */
	public static String showRequestParameters(final HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		for ( @SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>)request.getParameterNames(); e.hasMoreElements(); ) {
		    final String nom_par = (String) e.nextElement();
		    
		    String[] values = request.getParameterValues(nom_par);
		    
		    for(String value : values){
		    	logger.info("showRequestParameters:nom_par:" + nom_par);
			    try {
					sb
						.append("Parámetro:[")
						.append(nom_par).append("]:[");
					
					//TODO: Darle una vuelta a recuperar valores en función del navegador...
						if(CHROME.equals(getBrowser(request))){
							sb.append(new String(value.getBytes(ISO_8859_1), UTF_8));
						}else if(FIREFOX.equals(getBrowser(request))){
							sb.append(new String(value.getBytes(UTF_8), UTF_8));
						}else{
							sb.append(new String(value.getBytes(ISO_8859_1), UTF_8));
						}
					sb	
						.append("]")
						.append(NL);
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
		    }
		    
		    /**
			System.out.println("showRequestParameters:nom_par:" + nom_par);
		    sb
		    	.append("Parámetro:[")
		    	.append(nom_par).append("]:[")
		    	.append(getParameterFromBrowser(request, nom_par))
		    	.append("]")
		    	.append(NL);
		    */
		    
		}
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param request
	 * @param nom_par
	 * @return
	 */
	public static String getParameterFromBrowser(HttpServletRequest request, String nom_par){
		String str = "";
		String agente = getBrowser(request);
		if(DEBUG)
			logger.info("param:" + nom_par);
		if(agente.equals(IE)){
			str = getIE(request, nom_par);
		}else if(agente.equals(FIREFOX)){
			str = getFF(request, nom_par);
		}else{ // Chrome
			str = getChromeMac(request, nom_par);
		}
		if(DEBUG)
			logger.info("param:[" + nom_par + "]:[" +str+ "]");
		return str;
	}


	/**
	 * 
	 * @param request
	 * @param nom_par
	 * @return
	 */
	public static String getFF(final HttpServletRequest request, final String nom_par){
		String str = "";
		if(request == null) return str;
		try {
			str = new String(request.getParameter( nom_par ).getBytes(UTF_8), UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}


	/**
	 * 
	 * @param request
	 * @param nom_par
	 * @return
	 */
	public static String getIE(final HttpServletRequest request, final String nom_par){
		String str = "";
		if(request == null) return str;
		try {
			str =request.getParameter( nom_par );
			if(str == null) return str;
			str = new String(str.getBytes(ISO_8859_1), ISO_8859_1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 
	 * @param request
	 * @param nom_par
	 * @return
	 */
	public static String getChromeMac(final HttpServletRequest request, final String nom_par){
		String str = "";
		if(request == null) return str;
		try {
			str =request.getParameter( nom_par );
			if(str == null) return str;
			str = new String(str.getBytes(ISO_8859_1), UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}


	public static String[] getParameterArrayFromBrowser(
			HttpServletRequest request, String nom_par) {
		
	    String[] values = request.getParameterValues(nom_par);
	    if(values == null){
	    	logger.info("Sin condiciones en las categorías...!");
	    }else{
		    // Ahora hay que limpiarlos en función del navegador
		    int numValues = values.length;
		    
		    for(int k = 0; k<numValues; k++){
		    	try {
		    		//TODO: Habilitar un buen cargador de Strings en función del navegador... Ahora sólo va para el Chrome de Mac
					values[k] = new String(values[k].getBytes(ISO_8859_1), UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}//try
		    	
		    }//for
		    
	    }//if
	    
		return values;
	}
	
}
