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
import net.herranzmartin.actividades.model.Categoria;
import net.herranzmartin.actividades.model.Respuesta;
import net.herranzmartin.actividades.services.ActividadService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ActividadesServlet
 */
public class CategoriasServlet extends HttpServlet {

	private static final String CATEGORIA_DESCRIPCION = "categoriaDescripcion";
	private static final String CATEGORIA_NOMBRE = "categoriaNombre";

	
	private static final long serialVersionUID = 1L;
	
	private EntityManagerFactory emf = null;
	private EntityManager em = null;
	private ActividadService as = null;
	
	private static final Logger logger = Logger.getLogger(CategoriasServlet.class.getName());


    /**
     * Default constructor. 
     */
    public CategoriasServlet() {
        // TODO Auto-generated constructor stub
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
	    
	    logger.info("Estamos en getCategorias...");
	    
	    String op = request.getParameter("op").toString();
	    logger.info("Operación recibida:" + op);
	    
	    Categoria categoria;
	    List<Categoria> categorias;
	    String json = "";

	    if("add".equals(op)) {
	    	
	    	categoria = altaCategoria(request);
	    	
			Respuesta respuesta = new Respuesta();
			if(categoria != null) {
				respuesta.setCodigoRespuesta("00");
				respuesta.setDescRespuesta("Alta de la categoria " + categoria.getId() + " realizada correctamente!");
			}else {
				respuesta.setCodigoRespuesta("01");
				respuesta.setDescRespuesta("Error en el alta de la categoría!");
			}
			
	        json = new Gson().toJson(respuesta);


	    	
	    	
	    }else if("lista".equals(op)){
	    	
	    	categorias = as.listAllCategorias();
	    	
	    	logger.info("Se han localizado " + categorias.size() + " categorías!");
	    	
	        json = new GsonBuilder().setPrettyPrinting().create().toJson(categorias);

	    	logger.info("Texto que retorna: " + json);

	    }else if("del".equals(op)){

	    	boolean ok = bajaCategoria(request);

			Respuesta respuesta = new Respuesta();
			if(ok) {
				respuesta.setCodigoRespuesta("00");
				respuesta.setDescRespuesta("Baja de la categoría realizada correctamente!");
			}else {
				respuesta.setCodigoRespuesta("01");
				respuesta.setDescRespuesta("Error en la baja de la categoría!");
			}
	        json = new GsonBuilder().setPrettyPrinting().create().toJson(respuesta);
	    }

	    PrintWriter out = response.getWriter();
        out.write(json);
	}


	private Categoria altaCategoria(HttpServletRequest request) {

		em.getTransaction().begin();
	    
		logger.info("altaCategoria...");
		
		String nom = "",
				desc = "";
		
		String agente = Util.getBrowser(request);

		logger.info("El usuario usa un navegador tipo " + agente + ':' + request.getHeader("user-agent"));

		try {

			if(agente.equals(Util.FIREFOX)) {

				nom = new String(request.getParameter( CATEGORIA_NOMBRE ).getBytes(Util.UTF_8), Util.UTF_8);
				desc = new String(request.getParameter(CATEGORIA_DESCRIPCION).getBytes(Util.UTF_8), Util.UTF_8); //Funciona bien en FireFox
				
			}else if(agente.equals(Util.CHROME)) {

				nom = new String(request.getParameter( CATEGORIA_NOMBRE ).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				desc = new String(request.getParameter(CATEGORIA_DESCRIPCION).getBytes(Util.ISO_8859_1), Util.UTF_8); //Funciona bien en Chrome
				
			}else {
				nom = request.getParameter( CATEGORIA_NOMBRE ).toString();
				desc = request.getParameter(CATEGORIA_DESCRIPCION).toString();
			}

		} catch (UnsupportedEncodingException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		
		
		logger.info("nom:["+nom+"]");
		logger.info("desc:["+desc+"]");
		
		
		
		Categoria categoria = as.createCategoria(nom, desc);

		em.getTransaction().commit(); // now committed
		return categoria;
	}


	private boolean bajaCategoria(HttpServletRequest request) {
		boolean ret = false;
		em.getTransaction().begin();
	
		// Localizamos las categorías
		logger.info("acciones:operación delete ");
		String id = request.getParameter("id");
		logger.info("acciones:operación delete " + id);
		long idCategoria = Long.parseLong(id);
		logger.info("acciones:operación delete " + idCategoria);
		ret = as.deleteCategoria(idCategoria);
		
		em.getTransaction().commit(); // now committed
	
		return ret;
	}

}
