package net.herranzmartin.actividades.exceptions;

import java.util.logging.Logger;

public class NoSeHaRecibidoOperacionException extends Exception {
	
	private static final Logger logger = Logger.getLogger(NoSeHaRecibidoOperacionException.class.getName());
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoSeHaRecibidoOperacionException(){
		
	}

	public NoSeHaRecibidoOperacionException(String mensaje){
		logger.severe(mensaje);
		logger.severe(getMessage());
		logger.severe(getLocalizedMessage());
	}
}
