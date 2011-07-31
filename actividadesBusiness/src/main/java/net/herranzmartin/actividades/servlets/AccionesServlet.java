package net.herranzmartin.actividades.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.herranzmartin.actividades.Util;
import net.herranzmartin.actividades.model.Accion;
import net.herranzmartin.actividades.model.Respuesta;
import net.herranzmartin.actividades.services.ActividadService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ActividadesServlet
 */
public class AccionesServlet extends HttpServlet {

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
	    
	    String op = request.getParameter("op").toString();
	    logger.info("Operación recibida:" + op);
	    
	    Accion accion;
	    List<Accion> acciones;
	    String json = "";
	    
	    if("add".equals(op)) {
	    	
	    	accion = altaAccion(request);
	    	
			Respuesta respuesta = new Respuesta();
			if(accion != null) {
				respuesta.setCodigoRespuesta("00");
				respuesta.setDescRespuesta("Alta de la acción " + accion.getId() + " realizada correctamente!");
			}else {
				respuesta.setCodigoRespuesta("01");
				respuesta.setDescRespuesta("Error en el alta de la acción!");
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
				respuesta.setDescRespuesta("Baja de la acción realizada correctamente!");
			}else {
				respuesta.setCodigoRespuesta("01");
				respuesta.setDescRespuesta("Error en la baja de la acción ");
			}
	        json = new GsonBuilder().setPrettyPrinting().create().toJson(respuesta);
	    }
		
        PrintWriter out = response.getWriter();
        out.write(json);
	}


	private boolean bajaAccion(HttpServletRequest request) {
		boolean ret = false;
		em.getTransaction().begin();

		// Localizamos las categorías
    	logger.info("acciones:operación delete ");
    	String id = request.getParameter("id");
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
		
		String agente = Util.getBrowser(request);
	
		logger.info("El usuario usa un navegador tipo " + agente + ':' + request.getHeader("user-agent"));
	
		try {
	
			if(agente.equals(Util.FIREFOX)) {
	
				nom = new String(request.getParameter( ACCION_NOMBRE ).getBytes(Util.UTF_8), Util.UTF_8);
				desc = new String(request.getParameter(ACCION_DESCRIPCION).getBytes(Util.UTF_8), Util.UTF_8); //Funciona bien en FireFox
				
			}else if(agente.equals(Util.CHROME)) {
	
				nom = new String(request.getParameter( ACCION_NOMBRE ).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				desc = new String(request.getParameter(ACCION_DESCRIPCION).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				
			}else {
				nom = request.getParameter( ACCION_NOMBRE ).toString();
				desc = request.getParameter(ACCION_DESCRIPCION).toString();
			}
	
		} catch (UnsupportedEncodingException e) {
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
