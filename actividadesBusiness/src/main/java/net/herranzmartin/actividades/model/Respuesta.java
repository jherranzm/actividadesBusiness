package net.herranzmartin.actividades.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Respuesta extends ClaseGenerica {
	
	private String codigoRespuesta;
	private String descRespuesta;
	
	@XmlElement(name="mensaje")
	@XmlElementWrapper(name="mensajes")
	private List<String> mensajes = new ArrayList<String>();
	
	
	
	
	
	
	
	

	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}

	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}

	public String getDescRespuesta() {
		return descRespuesta;
	}

	public void setDescRespuesta(String descRespuesta) {
		this.descRespuesta = descRespuesta;
	}

	public List<String> getMensajes() {
		return mensajes;
	}

	public void setMensajes(List<String> mensajes) {
		this.mensajes = mensajes;
	}

}
