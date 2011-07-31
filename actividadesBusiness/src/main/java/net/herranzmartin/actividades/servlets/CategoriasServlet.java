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
import net.herranzmartin.actividades.model.Categoria;
import net.herranzmartin.actividades.model.Respuesta;
import net.herranzmartin.actividades.services.ActividadService;
import net.herranzmartin.actividades.util.Util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class ActividadesServlet
 */
public class CategoriasServlet extends HttpServlet {

	private static final String OP_CATEGORIA_NOOP_ERROR = "No ha llegado la operación...!";
	private static final String OP_CATEGORIA_DEL_ERROR = "Error en la baja de la categoría!";
	private static final String OP_CATEGORIA_DEL_OK = "Baja de la categoría realizada correctamente!";
	private static final String OP_CATEGORIA_ADD_ERROR = "Error en el alta de la categoría!";
	private static final String OP_CATEGORIA_ADD_OK = "Alta de la categoria %s realizada correctamente!";
	
	private static final String CATEGORIA_DESCRIPCION = "categoriaDescripcion";
	private static final String CATEGORIA_NOMBRE = "categoriaNombre";

	
	private static final long serialVersionUID = 1L;
	
	private EntityManagerFactory emf = null;
	private EntityManager em = null;
	private ActividadService as = null;
	
	private static final Logger LOGGER = Logger.getLogger(CategoriasServlet.class.getName());


    /**
     * Default constructor. 
     */
    public CategoriasServlet() {
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
	    
	    LOGGER.info("Estamos en getCategorias...");

	    String json = "";
	    
	    try {
			if(request.getParameter("op") == null){
				
				throw new NoSeHaRecibidoOperacionException(OP_CATEGORIA_NOOP_ERROR);
				
			}else{
			    //String op = request.getParameter("op").toString();
			    String op = Util.getParameterFromBrowser(request, "op");
			    LOGGER.info("Operación recibida:" + op);
			    
			    Categoria categoria;
			    List<Categoria> categorias;

			    if("add".equals(op)) {
			    	
			    	categoria = altaCategoria(request);
			    	
					Respuesta respuesta = new Respuesta();
					if(categoria != null) {
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta(String.format(OP_CATEGORIA_ADD_OK, categoria.getId()));
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta(OP_CATEGORIA_ADD_ERROR);
					}
					
			        json = new Gson().toJson(respuesta);


			    	
			    	
			    }else if("lista".equals(op)){
			    	
			    	categorias = as.listAllCategorias();
			    	
			    	LOGGER.info("Se han localizado " + categorias.size() + " categorías!");
			    	
			        json = new GsonBuilder().setPrettyPrinting().create().toJson(categorias);

			    	LOGGER.info("Texto que retorna: " + json);

			    }else if("del".equals(op)){

			    	boolean ok = bajaCategoria(request);

					Respuesta respuesta = new Respuesta();
					if(ok) {
						respuesta.setCodigoRespuesta("00");
						respuesta.setDescRespuesta(OP_CATEGORIA_DEL_OK);
					}else {
						respuesta.setCodigoRespuesta("01");
						respuesta.setDescRespuesta(OP_CATEGORIA_DEL_ERROR);
					}
			        json = new GsonBuilder().setPrettyPrinting().create().toJson(respuesta);
			    }
				
			}
		} catch (NoSeHaRecibidoOperacionException e) {
			Respuesta respuesta = new Respuesta();
			respuesta.setCodigoRespuesta("02");
			respuesta.setDescRespuesta(OP_CATEGORIA_NOOP_ERROR);
			
			json = new Gson().toJson(respuesta);
		}finally{
	        PrintWriter out = response.getWriter();
	        out.write(json);
		}
	}


	private Categoria altaCategoria(HttpServletRequest request) {

		em.getTransaction().begin();
	    
		LOGGER.info("altaCategoria...");
		
		String nom = "",
				desc = "";
		
		try {

			nom = Util.getParameterFromBrowser(request, CATEGORIA_NOMBRE);
			desc = Util.getParameterFromBrowser(request, CATEGORIA_DESCRIPCION);


		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
		
		
		LOGGER.info("nom:["+nom+"]");
		LOGGER.info("desc:["+desc+"]");
		
		
		
		Categoria categoria = as.createCategoria(nom, desc);

		em.getTransaction().commit(); // now committed
		return categoria;
	}


	private boolean bajaCategoria(HttpServletRequest request) {
		boolean ret = false;
		em.getTransaction().begin();
	
		// Localizamos las categorías
		LOGGER.info("acciones:operación delete ");
		//String id = request.getParameter("id");
		String id = Util.getParameterFromBrowser(request, "id");
		LOGGER.info("acciones:operación delete " + id);
		long idCategoria = Long.parseLong(id);
		LOGGER.info("acciones:operación delete " + idCategoria);
		ret = as.deleteCategoria(idCategoria);
		
		em.getTransaction().commit(); // now committed
	
		return ret;
	}

}
