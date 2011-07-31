/**
 * 
 */
package net.herranzmartin.actividades.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jherranzm
 *
 */
@Entity
@NamedQueries({
	@NamedQuery(
			name="findActividadPorId",
			query="select a from Actividad a WHERE a.id = ?1"),		
	@NamedQuery(
			name="findActividadPorNombre",
			query="select a from Actividad a WHERE a.nombre = ?1")		
})
@Table(name="tbl_Actividades")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Actividad extends ClaseGenerica  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4355840283086964870L;

	@Id
	@Column(name="actividad_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name="actividad_nombre", columnDefinition = "VARCHAR(255) ")
	private String nombre;

	@Column(name="actividad_description", columnDefinition = "TEXT ")
	private String description;

	@Column(name="actividad_link", columnDefinition = "VARCHAR(255) ")
	private String link;

	@OneToMany
	@XmlElement(name="categoria")
	@XmlElementWrapper(name="categorias")
	private List<Categoria> listaCategorias = new ArrayList<Categoria>();
	
	@OneToOne
	@XmlElement(name="accion")
	private Accion accion;

	
	
	
	
	
	/*
	 * */
	public Actividad() {
		super();
	}




	

	/**
	 * Getters and Setters
	 */
	


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






	public String getLink() {
		return link;
	}






	public void setLink(String link) {
		this.link = link;
	}






	public List<Categoria> getListaCategorias() {
		return listaCategorias;
	}






	public void setListaCategorias(List<Categoria> listaCategorias) {
		this.listaCategorias = listaCategorias;
	}











	public Accion getAccion() {
		return accion;
	}






	public void setAccion(Accion accion) {
		this.accion = accion;
	}
	
	
	
	
	@Override
	public String toString() {
		
		long j = listaCategorias.size();
		System.out.println(j);
		return "Actividad [id=" + id 
				+ ", nombre=" + nombre 
				+ ", description=" + description 
				+ ", accion=" + accion 
				+ ", listaCategorias=" + listaCategorias 
				+ ", link=" + link 
				+ "]";
	}
	
	

	
	
}
