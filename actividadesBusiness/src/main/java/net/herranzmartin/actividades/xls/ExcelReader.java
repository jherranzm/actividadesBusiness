package net.herranzmartin.actividades.xls;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import net.herranzmartin.actividades.model.Accion;
import net.herranzmartin.actividades.model.Actividad;
import net.herranzmartin.actividades.model.Categoria;
import net.herranzmartin.actividades.services.ActividadService;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;

import com.google.gson.Gson;

public class ExcelReader {

	ExcelReader instance;
	String docPath;
	POIFSFileSystem document;
	HSSFWorkbook workbook;
	
	private static final Logger logger = Logger.getLogger(ExcelReader.class.getName());

	
	
	private static EntityManagerFactory emf = null;
	private static EntityManager em = null;
	private static ActividadService service = null;
	//private static List<Actividad> listaActividades = new ArrayList<Actividad>();


	public static ExcelReader getInstance(String sDocPath) throws IOException {
		return new ExcelReader(sDocPath);
	}

	private ExcelReader() {
	};

	public ExcelReader(String sDocPath) throws IOException {
		this.instance = new ExcelReader();
		this.docPath = sDocPath;
		this.document = new POIFSFileSystem(new FileInputStream(docPath));
		this.workbook = new HSSFWorkbook(document);
	}

	public ArrayList<String> getColNames(int sheetIndex) {
		ArrayList<String> colNames = new ArrayList<String>();
		HSSFSheet sheet = this.workbook.getSheetAt(sheetIndex);
		HSSFRow row = sheet.getRow(0);
		HSSFCell cell = null;
		int cols = 0;
		if (row != null) {
			cols = row.getPhysicalNumberOfCells();
			for (int i = 0; i < cols; i++) {
				cell = row.getCell(i);
				if (cell != null
						&& cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					colNames.add(cell.getRichStringCellValue().getString());
				}
			}
		}
		return colNames;
	}

	public ArrayList<String> getColNames(String sheetName) {
		return getColNames(this.workbook.getSheetIndex(sheetName));
	}

	/**
	 * @param sheetIndex
	 * @return ArrayList<Map> where the key is the field names. Assumes first
	 *         row contains field names
	 */
	public ArrayList<Map<String, Object>> getMappedValues(int sheetIndex) {
		ArrayList<String> colNames = null;
		ArrayList<Map<String, Object>> mapArray = null;
		HSSFRow row = null;
		HSSFSheet sheet = null;
		int sheetRows = 0;
		int rowCols = 0;
		Map<String, Object> rowMap = null;

		sheet = this.workbook.getSheetAt(sheetIndex);
		sheetRows = sheet.getPhysicalNumberOfRows();
		mapArray = new ArrayList<Map<String, Object>>(sheetRows - 1);
		colNames = getColNames(sheetIndex);

		colNames.trimToSize();

		rowCols = colNames.size();

		for (int i = 1; i < sheetRows; i++) {
			row = sheet.getRow(i);
			rowMap = new HashMap<String, Object>(rowCols);
			for (int c = 0; c < rowCols; c++) {
				rowMap.put(colNames.get(c), getCellValue(row.getCell(c)));
			}
			mapArray.add(rowMap);
		}
		return mapArray;
	}

	private Object getCellValue(HSSFCell cell) {

		Object obj = null;
		//
		if(cell == null){
			// celdas vacías
		}else{
			logger.info("TypeCell:" + cell.getCellType());
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				obj = cell.getRichStringCellValue().getString();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					obj = cell.getDateCellValue();
				} else {
					obj = cell.getNumericCellValue();
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				obj = cell.getBooleanCellValue();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				obj = cell.getCellFormula();
				break;
			default:
				
			}
			logger.info("Valor de la celda:[" + obj + "]");
		}		
		return obj;
	}

	public ArrayList<Map<String, Object>> getMappedValues(String sheetName) {
		return getMappedValues(this.workbook.getSheetIndex(sheetName));
	}

	public void testBorrarAcciones() {
		Gson gson = new Gson();
	
		List<Accion> lista = service.listAllAcciones();
		for (Accion elemento : lista) {
			logger.info(gson.toJson(elemento));
	
			em.getTransaction().begin();
			em.remove(elemento);
			em.getTransaction().commit();
	
		}
	
	}

	public void testBorrarActividades() {
		Gson gson = new Gson();
	
		List<Actividad> actividades = service.listAllActividades();
		for (Actividad actividad : actividades) {
			logger.info(gson.toJson(actividad));
	
			em.getTransaction().begin();
			em.remove(actividad);
			em.getTransaction().commit();
	
		}
	
	}

	public void testBorrarCategorias() {
		Gson gson = new Gson();
	
		List<Categoria> lista = service.listAllCategorias();
		for (Categoria elemento : lista) {
			logger.info(gson.toJson(elemento));
	
			em.getTransaction().begin();
			em.remove(elemento);
			em.getTransaction().commit();
	
		}
	
	}

	public void execute(){
		ArrayList<String> colNames = null;
		ArrayList<Map<String, Object>> columnMaps = null;
		Iterator<String> colNamesIt = null;
		Iterator<Map<String, Object>> columnMapsIt = null;
		Map<String, Object> columnMap = null;
		String colKey = null;
		String[] myRecordings = new String[] { "Hoja1" };
		
		emf = Persistence.createEntityManagerFactory("ACTIVIDADES");
		em = emf.createEntityManager();
	    service = new ActividadService(em);
	    //List<Actividad> listaActividades = new ArrayList<Actividad>();
	    
	    boolean procesa = true;
	    
	   


		try {
			testBorrarActividades();
			testBorrarCategorias();
			testBorrarAcciones();

			for (int i = 0; i < myRecordings.length; i++) {
				colNames = getColNames(myRecordings[i]);
				logger.info("Nombres de columnas:" + colNames);
				colNamesIt = colNames.iterator();
				columnMaps = getMappedValues(myRecordings[i]);
				columnMapsIt = columnMaps.iterator();
				
				
				

				while (columnMapsIt.hasNext()) {
					columnMap = columnMapsIt.next();
					
					// Por cada registro...
					String nombreActividad = (String)(columnMap.get("Nombre"));
					Actividad actividad = service.getActividadByName(nombreActividad);
					if(actividad == null){
						actividad = new Actividad();
						logger.info("Creamos la actividad...");
					}
					
					// Por cada columna...

					while (colNamesIt.hasNext()) {
						colKey = colNamesIt.next();

						logger.info(colKey
								+ "\t"
								+ ((columnMap.get(colKey) != null) ? columnMap.get(colKey) : ""));
						
						if (procesa) {
							if (colKey.equals("Nombre")) {
								// Buscamos una actvidad por nombre
								actividad.setNombre((String) ((columnMap.get(colKey) != null) ? columnMap.get(colKey) : ""));
							} else if (colKey.equals("Tipo Contenido")) {
								
								String unaAccion = (String) ((columnMap.get(colKey) != null) ? columnMap.get(colKey) : "");
								Accion accion = service.getAccionByName(unaAccion);
								if(accion == null){
									accion = service.createAccion(unaAccion, "");
									//em.getTransaction().commit();
								}
								actividad.setAccion(accion);
							} else if (colKey.equals("Competencia")) {
								String unaCategoria = (String) ((columnMap.get(colKey) != null) ? columnMap.get(colKey) : "");
								Categoria categoria = service.getCategoriaByName(unaCategoria);
								if(categoria == null){
									categoria = service.createCategoria(unaCategoria, "");
									//em.getTransaction().commit();
								}
								List<Categoria> listaCategorias = actividad.getListaCategorias();
								listaCategorias.add(categoria);
								actividad.setListaCategorias(listaCategorias);
							}
						}
					}
					logger.info("---------------------------------");
					colNamesIt = colNames.iterator();

					if (procesa){
						Actividad nuevaActividad = service.createActividad(actividad);
						//em.flush();
						logger.info("Se ha creado una nueva actividad!");
						logger.info(nuevaActividad.toXML());
					}
				}
				
			}
		} catch (Exception any) {
			any.printStackTrace();
		}finally{
			em.close();
			emf.close();
		}
	}
		
	public static void main(String args[]) {
		try {
			ExcelReader er = new ExcelReader("/Users/jherranzm/Downloads/ResumenMaterialFormaciones.xls");
			er.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
