package net.herranzmartin.actividades.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.herranzmartin.actividades.model.Accion;
import net.herranzmartin.actividades.model.Actividad;
import net.herranzmartin.actividades.model.Categoria;
import net.herranzmartin.actividades.util.Util;



public class ActividadService {
	
	private EntityManager em = null;
	
	private static final Logger logger = Logger.getLogger(ActividadService.class.getName());
	
	
	
	/**
	 * 
	 * @param unGestorDeEntidades
	 */
	public ActividadService(EntityManager unGestorDeEntidades) {
		this.em = unGestorDeEntidades;
	}

	/**
	 * 
	 * @param unNombre
	 * @param unaDescripcion
	 * @return
	 */
	public Actividad createActividad(
			String unNombre, 
			String unaDescripcion
			){
		Actividad actividad = new Actividad();
		actividad.setNombre(unNombre);
		actividad.setDescription(unaDescripcion);
		
		em.getTransaction().begin();
		em.persist(actividad);
		
		em.flush();
		em.refresh(actividad);
		em.getTransaction().commit();

		return actividad;
	}

	/**
	 * 
	 * @param unNombre
	 * @param unaDescripcion
	 * @return
	 */
	public Actividad createActividad(
			Actividad unaActividad
			){

		em.getTransaction().begin();
		em.persist(unaActividad);
		
		em.flush();
		em.refresh(unaActividad);
		em.getTransaction().commit();

		return unaActividad;
	}

	/**
	 * 
	 * @param unNombre
	 * @param unaDescripcion
	 * @return
	 */
	public Actividad updateActividad(
			Actividad unaActividad
			){

		logger.info("inicio transacción...");
		em.getTransaction().begin();
		em.persist(unaActividad);
		
		em.flush();
		em.refresh(unaActividad);
		em.getTransaction().commit();
		logger.info("fin transacción!");
		
		em.refresh(unaActividad);
		logger.info("refreshed!");
		
		
		
		//em.flush();

		return unaActividad;
	}

	/**
	 * 
	 * @param unNombre
	 * @param unaDescripcion
	 * @return
	 */
	public Actividad createActividad(
			String unNombre, 
			String unaDescripcion,
			Accion unaAccion
			){
		Actividad actividad = new Actividad();
		actividad.setNombre(unNombre);
		actividad.setDescription(unaDescripcion);
		actividad.setAccion(unaAccion);
		
		em.getTransaction().begin();
		em.persist(actividad);
		
		em.flush();
		em.refresh(actividad);
		em.getTransaction().commit();
		
		return actividad;
	}
	
	
	/**
	 * 
	 * @param unNombre
	 * @param unaDescripcion
	 * @param unasCategorias
	 * @return
	 */
	public Actividad createActividad(
			String unNombre, 
			String unaDescripcion,
			ArrayList<Categoria> unasCategorias
			){
		Actividad actividad = new Actividad();
		actividad.setNombre("Nombre de una actividad");
		actividad.setDescription("Lorem ipsum dolor...");
		actividad.setListaCategorias(unasCategorias);

		em.getTransaction().begin();
		em.persist(actividad);
		
		em.flush();
		em.refresh(actividad);
		em.getTransaction().commit();
		
		

		return actividad;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> listAllActividades(){

		logger.info("Recuperamos todas las actividades...");
		em.clear();
		Query query = em.createQuery("select a from Actividad a ORDER BY a.nombre");
		
		List<Actividad> actividades = query.getResultList();
		logger.info("Recuperamos " + actividades.size() + " actividades...");
		
		return actividades;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public List<Actividad> listActividadesPorNombre(String name){
		

		List<Actividad> actividades = new ArrayList<Actividad>();
		logger.info("Por aquí...");
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
		Root<Actividad> from = criteriaQuery.from(Actividad.class);
		CriteriaQuery<Object> select = criteriaQuery.select(from);
		 
		Expression<String> path = from.get("nombre");
		
		
		Predicate predicate = criteriaBuilder.like(path, "%" +name + "%");
		 
		criteriaQuery.where(predicate);
		 
		TypedQuery<Object> typedQuery = em.createQuery(select);
		List<Object> resultList = typedQuery.getResultList();
		
		logger.info("Por acullá..." + resultList.size());
		//categorias = (List<Categoria>) resultList;
		for(Object obj : resultList){
			actividades.add((Actividad) obj);
		}
		
		return actividades;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Actividad> listActividadesPorCriterios(Map<String, Object> params){
		

		List<Actividad> actividades = new ArrayList<Actividad>();
		logger.info("Por aquí...");
		
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
		
		Root<Actividad> actividad = criteriaQuery.from(Actividad.class);
		
		Predicate p = criteriaBuilder.conjunction();
		
		CriteriaQuery<Object> select = criteriaQuery.select(actividad);
		
		Expression<String> path = null;
		
		//Join<Actividad, Categoria> categoria = null;
		
		for(String key : params.keySet()){
			logger.info("clave:" + key + "\tvalue:["+params.get(key)+"]");
			
			
			if(key.equals("accion")){
				p = criteriaBuilder.and(p, criteriaBuilder.equal(actividad.get("accion").get("id"), Integer.parseInt((String)params.get(key))) );
			}else if(key.equals("categoria")){
				//TODO: fijar un predicado en función de la categoría

				try {
					//path = from.join("listaCategorias").get("id");
					
					String[] cats = (String[]) params.get(key);
					ArrayList<Long> idsCat = new ArrayList<Long>();
					for(String cat : cats){
						idsCat.add(Long.parseLong(cat));
						p = criteriaBuilder.and(p, 
									criteriaBuilder.equal(
											actividad.join("listaCategorias").get("id"),  
											Long.parseLong(cat)
										)//equal
									);//and
						
					}
					logger.info("categorias:" + idsCat);
					// Salen más de los que tocan... y se soluciona con un distinct
					p = criteriaBuilder.and(p, actividad.join("listaCategorias").get("id").in(idsCat));
					
				} catch (Exception e) {
					logger.severe(Util.SEP_VERTICAL + "Ha habido una excepcion!!");
					e.printStackTrace();
					logger.severe(e.toString());
					logger.severe(Util.SEP_VERTICAL);
					logger.severe(e.getMessage());
					logger.severe(Util.SEP_VERTICAL);
				}
				
				
			}else{
				path = actividad.get(key);
				logger.info("path.toString():" + path.toString());
				p = criteriaBuilder.and(p, criteriaBuilder.like(path, "%" + params.get(key) + "%"));
			}
		}
		 
		criteriaQuery.where(p).distinct(true);
		
		logger.info("criteriaQuery:" + criteriaQuery.toString());
		 
		TypedQuery<Object> typedQuery = em.createQuery(select);
		List<Object> resultList = typedQuery.getResultList();
		
		logger.info("Por acullá..." + resultList.size());
		//categorias = (List<Categoria>) resultList;
		for(Object obj : resultList){
			actividades.add((Actividad) obj);
		}
		
		return actividades;
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Accion> listAllAcciones(){

		logger.info("listAllAcciones 1");
		Query query = em.createQuery("select a from Accion a ORDER BY a.nombre");
		logger.info("listAllAcciones 2");
		List<Accion> acciones = query.getResultList();
		logger.info("listAllAcciones 3");
		
		return acciones;
	}
	
	public Categoria getCategoriaByName(String unaCategoria) {
		
		Categoria categoria = null;
		
		try {
			logger.info("Buscando una Categoria: " + unaCategoria);
			
			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findCategoriaPorNombre");
			query.setParameter(1, unaCategoria);
			
			categoria = (Categoria)query.getSingleResult();
			
			logger.info(categoria.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la categoría de nombre:["+unaCategoria+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return categoria;
	}
	
	public Categoria getCategoriaById(long idCategoria) {
		
		Categoria categoria = null;
		
		try {
			logger.info("Buscando la Categoria: " + idCategoria);
			
			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findCategoriaPorId");
			query.setParameter(1, idCategoria);
			
			categoria = (Categoria)query.getSingleResult();
			
			logger.info(categoria.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la categoría de id:["+idCategoria+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return categoria;
	}
	
	
	public Accion getAccionByName(String unaAccion) {
		
		Accion accion = null;
		
		try {
			logger.info("Buscando una Accion: " + unaAccion);

			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findAccionPorNombre");
			query.setParameter(1, unaAccion);
			
			accion = (Accion)query.getSingleResult();
			
			logger.info(accion.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la acción de nombre:["+unaAccion+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return accion;
	}
	
	
	public Accion getAccionById(long unaAccion) {
		
		Accion accion = null;
		
		try {
			logger.info("Buscando una Accion: " + unaAccion);

			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findAccionPorId");
			query.setParameter(1, unaAccion);
			
			accion = (Accion)query.getSingleResult();
			
			logger.info(accion.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la acción de id:["+unaAccion+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return accion;
	}
	
	public Actividad getActividadById(long idActividad) {
		
		Actividad actividad = null;
		
		try {
			logger.info("Buscando una Actividad: " + idActividad);

			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findActividadPorId");
			query.setParameter(1, idActividad);
			
			actividad = (Actividad)query.getSingleResult();
			
			logger.info(actividad.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la actividad de id:["+idActividad+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return actividad;
	}
	
	public Actividad getActividadByName(String nombreActividad) {
		
		Actividad actividad = null;
		
		try {
			logger.info("Buscando una Actividad: " + nombreActividad);

			// Tirando de NamedQuery...
			Query query = em.createNamedQuery("findActividadPorNombre");
			query.setParameter(1, nombreActividad);
			
			actividad = (Actividad)query.getSingleResult();
			
			logger.info(actividad.toXML());
			
		} catch (NoResultException e) {
			logger.severe("No se ha localizado la actividad de nombre:["+nombreActividad+"]");
		} catch (Exception e) {
			logger.severe("Se ha producido una Exception:" + e.getMessage());
			e.printStackTrace();
		}
		
		return actividad;
	}
	
	
	public Categoria createCategoria(
			String unNombre, 
			String unaDescripcion
			) {
		Categoria categoria = new Categoria();
		categoria.setNombre(unNombre);
		categoria.setDescription(unaDescripcion);

		em.persist(categoria);

		return categoria;
		
	}
 
	public Accion createAccion(
			String unNombre, 
			String unaDescripcion
			) {
		Accion accion = new Accion();
		
		try {
			accion.setNombre(unNombre);
			accion.setDescription(unaDescripcion);

			logger.info("Ya tenemos la acción :" + accion.toXML());
			em.persist(accion);
			logger.info("Ya tenemos la acción guardada!");
		} catch (Exception e) {
			logger.warning("Exception: " + e.getMessage());
			e.printStackTrace();
		}
		
		return accion;
		
	}
	
	public boolean deleteActividad(long id) {
		boolean ret = false;
		
		
		
		Actividad actividad = getActividadById(id);
		ret = true;
		
		em.remove(actividad);
		
		
		return ret;
	}

	public boolean deleteCategoria(long id) {
		boolean ret = false;
		
		
		
		Categoria obj = getCategoriaById(id);
		ret = true;
		
		em.remove(obj);
		
		
		return ret;
	}

	public boolean deleteAccion(long id) {
		boolean ret = false;
		
		
		
		Accion obj = getAccionById(id);
		if(obj == null){
			logger.severe("No se ha localizado la acción " + id);
		}else{
	
			em.remove(obj);
			ret = true;
		}
		
		
		
		return ret;
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Accion> listAccionesPorNombre(){
	
		Query query = em.createQuery("select a from Accion a ORDER BY a.nombre");
		
		List<Accion> acciones = query.getResultList();
		
		return acciones;
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Categoria> listAllCategorias(){
	
		Query query = em.createQuery("select a from Categoria a ORDER BY a.nombre");
		
		List<Categoria> categorias = query.getResultList();
		
		return categorias;
	}

}
