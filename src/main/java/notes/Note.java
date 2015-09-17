package notes;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notes")
public class Note {
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String body;
	
	public Note() {
		// No-arg constructor
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getBody() {
		return this.body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

}
