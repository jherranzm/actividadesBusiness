package net.herranzmartin.actividades.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.herranzmartin.actividades.exceptions.NoSeHaRecibidoOperacionException;
import net.herranzmartin.actividades.model.Accion;
import net.herranzmartin.actividades.model.Respuesta;
import net.herranzmartin.actividades.services.ActividadService;
import net.herranzmartin.actividades.util.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ActividadesServlet
 */
public class AccionesServlet extends HttpServlet {

	private static final String OP_ACCION_ADD_OK = "Alta de la acción %d realizada correctamente!";
	private static final String OP_ACCION_ADD_ERROR = "Error en el alta de la acción!";
	private static final String OP_ACCION_NOOP_ERROR = "No ha llegado la operación...!";
	private static final String OP_ACCION_DEL_ERROR = "Error en la baja de la acción ";
	private static final String OP_ACCION_DEL_OK = "Baja de la acción realizada correctamente!";
	private static final String ACCION_DESCRIPCION = "accionDescripcion";
	private static final String ACCION_NOMBRE = "accionNombre";

	
	private static final long serialVersionUID = 1L;
	
	private EntityManagerFactory emf = null;
	private EntityManager em = null;
	private ActividadService as = null;
	
	private static final Logger logger = Logger.getLogger(AccionesServlet.class.getName());


    /**
     * Default constructor. 
     */
    public AccionesServlet() {
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
		
		response.setContentType("application/json");
		//response.setCharacterEncoding("UTF-8");
		
		emf = Persistence.createEntityManagerFactory("ACTIVIDADES");
	    em = emf.createEntityManager();
	    as = new ActividadService(em);
	    
	    String json = "";
	    
	    try {
			if(request.getParameter("op") == null){
				
				throw new NoSeHaRecibidoOperacionException(OP_ACCION_NOOP_ERROR);
				
			}else{
			    String op = Util.getParameterFromBrowser(request, "op");
			    logger.info("Operación recibida:" + op);
			    
			    Accion accion;
			    List<Accion> acciones;
			    
			    
			    if("add".equals(op)) {
			    	
			    	accion = altaAccion(request);
			    	
					Respuesta respuesta = new Respuesta();
					if(accion != null) {
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta(String.format(OP_ACCION_ADD_OK, accion.getId()));
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta(OP_ACCION_ADD_ERROR);
					}
					
			        json = new Gson().toJson(respuesta);


			    	
			    	
			    }else if("lista".equals(op)){
			    	
			    	acciones = as.listAllAcciones();
			    	
			        json = new GsonBuilder().setPrettyPrinting().create().toJson(acciones);

			    }else if("del".equals(op)){
			    	
			    	boolean ok = bajaAccion(request);

					Respuesta respuesta = new Respuesta();
					if(ok) {
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta(OP_ACCION_DEL_OK);
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta(OP_ACCION_DEL_ERROR);
					}
			        json = new GsonBuilder().setPrettyPrinting().create().toJson(respuesta);
			    }
				
			}
		} catch (NoSeHaRecibidoOperacionException e) {
			Respuesta respuesta = new Respuesta();
			respuesta.setCodigoRespuesta("02");
			respuesta.setDescRespuesta(OP_ACCION_NOOP_ERROR);
			
			json = new Gson().toJson(respuesta);
		}finally{
	        PrintWriter out = response.getWriter();
	        out.write(json);
		}
	    
		
	}


	private boolean bajaAccion(HttpServletRequest request) {
		boolean ret = false;
		em.getTransaction().begin();

		// Localizamos las categorías
    	logger.info("acciones:operación delete ");
    	//String id = request.getParameter("id");
    	String id = Util.getParameterFromBrowser(request, "id");
    	logger.info("acciones:operación delete " + id);
    	long idAccion = Long.parseLong(id);
    	logger.info("acciones:operación delete " + idAccion);
		ret = as.deleteAccion(idAccion);
		
		em.getTransaction().commit(); // now committed

		return ret;
	}


	private Accion altaAccion(HttpServletRequest request) {
	
		em.getTransaction().begin();
	    
		logger.info("altaAccion...");
		
		String nom = "",
				desc = "";
		
		try {
	
			nom = Util.getParameterFromBrowser(request, ACCION_NOMBRE);
			desc = Util.getParameterFromBrowser(request, ACCION_DESCRIPCION);
	
		} catch (Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		
		logger.info("nom:["+nom+"]");
		logger.info("desc:["+desc+"]");
		
		logger.info("Vamos a crear acción...!");
		Accion accion = as.createAccion(nom, desc);
	
		em.getTransaction().commit(); // now committed
		return accion;
	}

}
