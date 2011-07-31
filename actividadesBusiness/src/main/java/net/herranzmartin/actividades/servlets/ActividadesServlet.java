package net.herranzmartin.actividades.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.herranzmartin.actividades.Util;
import net.herranzmartin.actividades.exceptions.NoSeHaRecibidoOperacionException;
import net.herranzmartin.actividades.model.Accion;
import net.herranzmartin.actividades.model.Actividad;
import net.herranzmartin.actividades.model.Categoria;
import net.herranzmartin.actividades.model.Respuesta;
import net.herranzmartin.actividades.services.ActividadService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ActividadesServlet
 */
public class ActividadesServlet extends HttpServlet {
	
	
	
	private static final String ACTIVIDAD_CATEGORIA = "actividadCategoria";
	private static final String ACTIVIDAD_ACCION = "actividadAccion";
	private static final String ACTIVIDAD_DESCRIPCION = "actividadDescripcion";
	private static final String ACTIVIDAD_NOMBRE = "actividadNombre";

	private static final String ACTIVIDAD_ID_UPDATE = "actividadIdUpdate";
	private static final String ACTIVIDAD_CATEGORIA_UPDATE = "actividadCategoriaUpdate";
	private static final String ACTIVIDAD_ACCION_UPDATE = "actividadAccionUpdate";
	private static final String ACTIVIDAD_DESCRIPCION_UPDATE = "actividadDescripcionUpdate";
	private static final String ACTIVIDAD_NOMBRE_UPDATE = "actividadNombreUpdate";

	private static final long serialVersionUID = 1L;
	
	private EntityManagerFactory emf = null;
	private EntityManager em = null;
	private ActividadService service = null;
	
	private static final Logger logger = Logger.getLogger(ActividadesServlet.class.getName());


    /**
     * Default constructor. 
     */
    public ActividadesServlet() {
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		procesaPeticion(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		procesaPeticion(request, response);
	}
	
	private void procesaPeticion(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		
		
		//Antes de nada, registramos lo que nos llega
		logger.info(Util.SEP_VERTICAL);
		logger.info(Util.showRequestParameters(request));
		logger.info(Util.SEP_VERTICAL);
		
		// Por si la habíamos cerrado..
		emf = Persistence.createEntityManagerFactory("ACTIVIDADES");
	    em = emf.createEntityManager();
	    service = new ActividadService(em);
	    
	    Actividad actividad = null;
	    List<Actividad> actividades = null;
	    PrintWriter out = response.getWriter();
	    String respuestaJSON = "";
	    
	    
	    response.setContentType("application/json");
	    response.setCharacterEncoding(Util.UTF_8);
	    
	    
	   
	    try {
	    	
			if(request.getParameter("op") == null){
				
				throw new NoSeHaRecibidoOperacionException("No ha llegado la operación...!");
				
			}else{
			
			    String op = request.getParameter("op").toString();
			    logger.info("Operación recibida:" + op);
			    
			    if("add".equals(op)) {
			    	
			    	actividad = altaActividad(request);

					Respuesta respuesta = new Respuesta();
					if(actividad != null) {
						logger.info("Alta de actividad: ejecutada correctamente!");
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta("Alta de la actividad " + actividad.getId() + " realizada correctamente!");
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta("Error en la creación de la actividad ");
					}
					
					respuestaJSON = new Gson().toJson(respuesta);
			        
			        logger.info("Actividad:alta:JSON:\n" + respuestaJSON);
			    	
			    }else if ("update".equals(op)) {
			    	
			    	actividad = updateActividad(request);
				    
					Respuesta respuesta = new Respuesta();
					if(actividad != null) {
						logger.info("Actividad: " + actividad.getId() +  " modificada correctamente!");
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta("Actividad: " + actividad.getId() +  " modificada correctamente!");
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta("Error en la modificación de la actividad!");
					}
					
					respuestaJSON = new Gson().toJson(respuesta);
			        
			        logger.info("Actividad:modificación:JSON:\n" + respuestaJSON);
			    	
			    }else if ("del".equals(op)) {
			    	
					boolean ok = bajaActividad(request);

					Respuesta respuesta = new Respuesta();
					if(ok) {
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta("Baja de la actividad  realizada correctamente!");
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta("Error en la baja de la actividad! ");
					}
					
			        
					respuestaJSON = new Gson().toJson(respuesta);
			        
			        logger.info("Actividades:bajaActividad:JSON:\n" + respuestaJSON);
					
			    }else if ("edit".equals(op)) {
			    	
					actividad = getActividad(request);

					respuestaJSON = new Gson().toJson(actividad);
			        
			        logger.info("Actividades:editActividad:JSON:\n" + respuestaJSON);
					
			    	
			    	
			    }else if ("lista".equals(op)) {
			    	
					// Localizamos las categorías
			    	logger.info("actividades:operación lista");
					actividades = service.listAllActividades();
					if(actividades == null) {
						logger.info("Sin actividades por ahora!");
					}else {
						logger.info("... y tenemos " + actividades.size() + " actividades!");
					}
					
			        //String jsonActividades = new Gson().toJson(actividades, ListaActividades.class);
					respuestaJSON = new GsonBuilder().setPrettyPrinting().create().toJson(actividades);
					
			    }else if ("find".equals(op)) {
			    	
					// Localizamos las categorías
			    	logger.info("actividades:operación find");
			    	String name = Util.getParameterFromBrowser( request, "actividadNombre" );
			    	String desc = Util.getParameterFromBrowser( request, "actividadDescripcion" );
			    	String acc = Util.getParameterFromBrowser( request, "actividadAccion" );
			    	String[] categorias = Util.getParameterArrayFromBrowser( request, "actividadCategoria" );
			    	logger.info("Se han recuperado los datos del formulario... ");
			    	if (name == null) logger.info("actividadNombre: NULO");
			    	if (desc == null) logger.info("actividadDescripcion: NULO");
			    	if (acc == null) logger.info("actividadAccion: NULO");
			    	if (categorias == null) logger.info("actividadCategoria: NULO");
			    	
			    	if(name == null && desc == null && acc == null && (categorias == null || categorias.length == 0)){
						Respuesta respuesta = new Respuesta();
							respuesta.setCodigoRespuesta("01");
							respuesta.setDescRespuesta("Error no se ha especificado ninguna condición de búsqueda! ");
							
						respuestaJSON = new Gson().toJson(respuesta);
			    		
			    	}else{
			    		Map<String, Object> params = new HashMap<String, Object>();
			    		if(name != null && !name.isEmpty()) params.put("nombre", name);
			    		if(desc != null && !desc.isEmpty()) params.put("description", desc);

			    		if(acc != null && !acc.isEmpty()) params.put("accion", acc);
			    		
			    		if(categorias != null && categorias.length > 0) params.put("categoria", categorias);
			    		
		    			logger.info("Parámetros de búsqueda:");
			    		for(String key : params.keySet()){
			    			logger.info(key + ":["+params.get(key)+"]");
			    		}
			    		
						actividades = service.listActividadesPorCriterios(params);
						if(actividades == null || actividades.size() == 0) {
							logger.info("Sin actividades con la condición especificada: nombre =[%"+ name+"%]");
							Respuesta respuesta = new Respuesta();
							respuesta.setCodigoRespuesta("01");
							respuesta.setDescRespuesta("Sin actividades con la condición especificada: nombre =[%"+ name+"%]");
							respuestaJSON = new GsonBuilder().setPrettyPrinting().create().toJson(respuesta);
							
						}else {
							logger.info("... y tenemos " + actividades.size() + " actividades!");
							respuestaJSON = new GsonBuilder().setPrettyPrinting().create().toJson(actividades);
						}
						
				        //String jsonActividades = new Gson().toJson(actividades, ListaActividades.class);
						
			    	}
					
			    }else{
					Respuesta respuesta = new Respuesta();
					respuesta.setCodigoRespuesta("03");
					respuesta.setDescRespuesta("Operación no implementada!");
					
					respuestaJSON = new Gson().toJson(respuesta);
			    }
			    
			}
		} catch (NoSeHaRecibidoOperacionException e) {
			Respuesta respuesta = new Respuesta();
				respuesta.setCodigoRespuesta("02");
				respuesta.setDescRespuesta("No ha llegado la operación...!");
				
				respuestaJSON = new Gson().toJson(respuesta);
		}finally{
		    out.write(respuestaJSON);
		    
		    em.close();
		    out.close();
		}
	}


	private boolean bajaActividad(HttpServletRequest request) {
		boolean ret = false;
		em.getTransaction().begin();

		// Localizamos las categorías
    	logger.info("acciones:operación delete ");
    	String id = request.getParameter("id");
    	logger.info("acciones:operación delete " + id);
    	long idActividad = Long.parseLong(id);
    	logger.info("acciones:operación delete " + idActividad);
		ret = service.deleteActividad(idActividad);
		
		em.getTransaction().commit(); // now committed

		return ret;
	}

	/**
	 * 
	 * @param request
	 * @return actividad o null
	 */
	private Actividad getActividad(HttpServletRequest request) {
		Actividad ret = null;

    	logger.info("getActividad:operación get ");
    	String id = request.getParameter("id");
    	logger.info("getActividad:operación get " + id);
    	long idActividad = Long.parseLong(id);
    	logger.info("getActividad:operación get " + idActividad);
		ret = service.getActividadById(idActividad);
		
		return ret;
	}



	private Actividad altaActividad(HttpServletRequest request) {
		
		logger.info("altaActividad...");
		
		String nom = "",
				desc = "",
				acc = "";
		
		String agente = Util.getBrowser(request);

		logger.info("El usuario usa un navegador tipo " + agente + ':' + request.getHeader("user-agent"));

		try {

			if(agente.equals(Util.FIREFOX)) {

				nom = new String(request.getParameter( ACTIVIDAD_NOMBRE ).getBytes(Util.UTF_8), Util.UTF_8);
				desc = new String(request.getParameter(ACTIVIDAD_DESCRIPCION).getBytes(Util.UTF_8), Util.UTF_8); //Funciona bien en FireFox
				
			}else if(agente.equals(Util.CHROME)) {

				nom = new String(request.getParameter( ACTIVIDAD_NOMBRE ).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				desc = new String(request.getParameter(ACTIVIDAD_DESCRIPCION).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				
			}else {
				nom = request.getParameter( ACTIVIDAD_NOMBRE ).toString();
				desc = request.getParameter(ACTIVIDAD_DESCRIPCION).toString();
			}
			acc = request.getParameter(ACTIVIDAD_ACCION);

		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		
		logger.info("nom:["+nom+"]");
		logger.info("desc:["+desc+"]");
		logger.info("acc:["+acc+"]");
		
		// extracting data from the checkbox field
	    String[] cats = request.getParameterValues(ACTIVIDAD_CATEGORIA);
		
		// Localizamos las categorías
		List<Categoria> categorias = new ArrayList<Categoria>();
		
		for(String cat : cats) {
			logger.info(cat);
			Categoria categoria = service.getCategoriaById(Long.parseLong(cat));
			categorias.add(categoria);
		}
		
		Accion accion = service.getAccionById(Long.parseLong(acc));
		
		Actividad actividad = new Actividad();
		actividad.setNombre(nom);
		actividad.setDescription(desc);
		actividad.setAccion(accion);
		
		actividad.setListaCategorias(categorias);
		
		Actividad retActividad = service.createActividad(actividad);
		logger.info("Se ha creado la actividad:" + retActividad.toXML());
		
		return retActividad;
	}


	private Actividad updateActividad(HttpServletRequest request) {
		
		logger.info("updateActividad...");
		
		String 
				id = "",
				nom = "",
				desc = "",
				acc = "";
		long idActividad;
		Actividad retActividad = null;
		
		String agente = Util.getBrowser(request);
	
		logger.info("El usuario usa un navegador tipo " + agente + ':' + request.getHeader("user-agent"));
	
		try {
	
			if(agente.equals(Util.FIREFOX)) {
	
				id = new String(request.getParameter( ACTIVIDAD_ID_UPDATE ).getBytes(Util.UTF_8), Util.UTF_8);
				nom = new String(request.getParameter( ACTIVIDAD_NOMBRE_UPDATE ).getBytes(Util.UTF_8), Util.UTF_8);
				desc = new String(request.getParameter(ACTIVIDAD_DESCRIPCION_UPDATE).getBytes(Util.UTF_8), Util.UTF_8); //Funciona bien en FireFox
				
			}else if(agente.equals(Util.CHROME)) {
	
				id = new String(request.getParameter( ACTIVIDAD_ID_UPDATE ).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				nom = new String(request.getParameter( ACTIVIDAD_NOMBRE_UPDATE ).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				desc = new String(request.getParameter(ACTIVIDAD_DESCRIPCION_UPDATE).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				
			}else {

				id = request.getParameter( ACTIVIDAD_ID_UPDATE ).toString();
				nom = request.getParameter( ACTIVIDAD_NOMBRE_UPDATE ).toString();
				desc = request.getParameter(ACTIVIDAD_DESCRIPCION_UPDATE).toString();
			}
			acc = request.getParameter(ACTIVIDAD_ACCION_UPDATE);
			
			idActividad = Long.parseLong(id);
	
			logger.info("id:["+id+"]");
			logger.info("nom:["+nom+"]");
			logger.info("desc:["+desc+"]");
			logger.info("acc:["+acc+"]");
			
			// extracting data from the checkbox field
		    String[] cats = request.getParameterValues(ACTIVIDAD_CATEGORIA_UPDATE);
			
			// Localizamos las categorías
			List<Categoria> categorias = new ArrayList<Categoria>();
			
			for(String cat : cats) {
				logger.info(cat);
				Categoria categoria = service.getCategoriaById(Long.parseLong(cat));
				categorias.add(categoria);
			}
			
			Accion accion = service.getAccionById(Long.parseLong(acc));
			
			Actividad actividad = service.getActividadById(idActividad);
			actividad.setNombre(nom);
			actividad.setDescription(desc);
			actividad.setAccion(accion);
			
			actividad.setListaCategorias(categorias);
			
			logger.info("Actividad para actualizar:\n" + actividad.toXML());
			
			retActividad = service.updateActividad(actividad);
			logger.info("Se ha actualizado la actividad:" + retActividad.toXML());
		
		} catch (NumberFormatException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		
		return retActividad;
	}

}


/**

logger.info("Actividades:lista:JSON:\n" + jsonActividades);

FileOutputStream fout = new FileOutputStream("/Users/jherranzm/dev/listaActividades_" + System.nanoTime() + ".json");
PrintStream       ps = new PrintStream(fout);
ps.println(jsonActividades);
fout.close();

logger.info("Acciones:lista:JSON:\n");
List<Accion> acciones = service.listAllAcciones();
logger.info("Acciones:lista:" + acciones.size() + " acciones!");
String jsonAcciones = new GsonBuilder().setPrettyPrinting().create().toJson(acciones);
logger.info("Acciones:lista:" + jsonAcciones);
fout = new FileOutputStream("/Users/jherranzm/dev/listaAcciones_" + System.nanoTime() + ".json");
ps = new PrintStream(fout);
ps.println(jsonAcciones);
fout.close();

logger.info("Categorias:lista:JSON:\n");
List<Categoria> categorias = service.listAllCategorias();
String jsonCategorias = new GsonBuilder().setPrettyPrinting().create().toJson(categorias);
fout = new FileOutputStream("/Users/jherranzm/dev/listaCategorias_" + System.nanoTime() + ".json");
ps = new PrintStream(fout);
ps.println(jsonCategorias);
fout.close();
*/

/**
try {
	ExcelReader er = new ExcelReader("/Users/jherranzm/Downloads/ResumenMaterialFormaciones.xls");
	er.execute();
	logger.info("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nSe han introducido datos!");
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

 */
