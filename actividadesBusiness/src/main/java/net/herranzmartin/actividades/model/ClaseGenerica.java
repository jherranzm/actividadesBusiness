package net.herranzmartin.actividades.model;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

public class ClaseGenerica {

	
	public String toXML() {
		//
		StringWriter writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(this.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(this, writer);
		} catch (JAXBException e) {
			System.err.println("JAXBException:" + e.getMessage());
			e.printStackTrace();
		}
		
		return writer.toString();
	}
	
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
