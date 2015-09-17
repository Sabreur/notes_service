package notes;

import static spark.Spark.post;
import static spark.Spark.get;

import java.sql.*;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


public class Main {

	// For a simple program, we hard-code this.  A more robust design would be to have this in a configuration file.
	private static String databaseUrl = "jdbc:sqlite:notes.db";
	private static Dao<Note,String> notesDao = null;
	private static ConnectionSource connectionSource = null;

	public static void main( String[] args) {
    	try {
    		initializeDB();	
		} catch (SQLException e) {			
			System.err.println(e.getMessage());
			System.exit(-1);
		}  catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
			
        post("/api/notes", (request, response) -> {
        	String noteBody = request.queryParams("body");        	
        	Note note = new Note();
        	note.setBody(noteBody);
        	notesDao.create(note);
        	        	
        	JSONObject responseJSON = new JSONObject();
        	responseJSON.put("id", note.getId());        	
        	responseJSON.put("body", note.getBody());        	        	
        	response.status(201);
        	response.type("application/json");
        	
        	return responseJSON.toString();
        });
        
        get("/api/notes", (request, response) -> {
        	String paramString = request.queryParams("query");
        	List<Note> queryResults = null;
        	if (null != paramString && !paramString.isEmpty()) {
        		String queryString = "%" + paramString + "%";
        		QueryBuilder<Note,String> queryBuilder = notesDao.queryBuilder();
        		queryBuilder.where().like("body", queryString);
        		PreparedQuery<Note> preparedQuery = queryBuilder.prepare();
        		queryResults = notesDao.query(preparedQuery);
        	} else {
        		queryResults = notesDao.queryForAll();
        	}
        	
        	JSONArray responseJSON = new JSONArray();
        	for (Note note : queryResults) {
        		JSONObject noteJSON = new JSONObject();
        		noteJSON.put("id", note.getId());        	
            	noteJSON.put("body", note.getBody());        	        	
            	responseJSON.put(noteJSON);
        	}
        	
        	response.status(200);
        	response.type("application/json");
        	return responseJSON.toString();	        	
        });
    }
    
    private static void initializeDB() throws SQLException, ClassNotFoundException {
   		Class.forName("org.sqlite.JDBC"); // Load the sqlite-JDBC driver	
		connectionSource = new JdbcConnectionSource(databaseUrl);
		((JdbcConnectionSource)connectionSource).setUsername("spark");
		((JdbcConnectionSource)connectionSource).setPassword("spark");
		TableUtils.createTableIfNotExists(connectionSource, Note.class);
    	notesDao = DaoManager.createDao(connectionSource, Note.class);		    	

    }
    
}