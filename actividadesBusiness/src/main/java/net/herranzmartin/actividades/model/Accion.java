package net.herranzmartin.actividades.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@NamedQueries({
	@NamedQuery(
			name="findAccionPorNombre",
			query="select a from Accion a WHERE a.nombre = ?1"),
	@NamedQuery(
			name="findAccionPorId",
			query="select a from Accion a WHERE a.id = ?1")		
})
@Table(name="tbl_Acciones")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Accion extends ClaseGenerica implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6995238235513413306L;

	@Id
	@Column(name="accion_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="accion_nombre")
	private String nombre;

	@Column(name="accion_description")
	private String description;
	
	
	
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Accion [id=" + id 
				+ ", nombre=" + nombre 
				+ ", description=" + description 
				+ "]";
	}

	
	
	
	
	

}
