package classi;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;


public class Database {

	private static final String connectionString = "jdbc:mysql://localhost:3306/TestDB?user=user&password=password";
	private Connection connection = null;

	private void createTables() {
		String loginTable = "CREATE TABLE login " +
                			"(id INTEGER not NULL AUTO_INCREMENT, " +
                			" user VARCHAR(255), " + 
                			" password VARCHAR(255), " + 
                			" PRIMARY KEY ( id ))"; 
		String rubricaTable = "CREATE TABLE rubrica " +
    						  "(id INTEGER not NULL AUTO_INCREMENT, " +
    						  " nome VARCHAR(255), " + 
    						  " cognome VARCHAR(255), " + 
    						  " indirizzo VARCHAR(255), " + 
    						  " telefono VARCHAR(255), " + 
    						  " eta INTEGER, " + 
    						  " rif_user VARCHAR(255), " + 
    						  " PRIMARY KEY ( id ))";
		
		Statement stmt;
		try {
			stmt = (Statement) connection.createStatement();
		    stmt.executeUpdate(loginTable);
		    stmt.executeUpdate(rubricaTable);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Tabelle create");
	}
	
	public boolean login(String user, String password) {
		String login_query = "SELECT * FROM login WHERE user='" + user + "' && password='" + password+ "'";
		Statement stmt;
		try {
			stmt = (Statement) connection.createStatement();
			ResultSet rs = stmt.executeQuery(login_query);
			if(rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void savePersona(Persona p, String user) {
		String save = "INSERT INTO rubrica(nome, cognome, indirizzo, telefono, eta, rif_user) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = (PreparedStatement) connection.prepareStatement(save);
		    preparedStmt.setString(1, p.getNome());
		    preparedStmt.setString(2, p.getCognome());
		    preparedStmt.setString(3, p.getIndirizzo());
		    preparedStmt.setString(4, p.getTelefono());
		    preparedStmt.setInt(5, p.getEta());
		    preparedStmt.setString(6, user);
	        preparedStmt.execute();
	        System.out.println("Salvataggio persona effettuato:"+ p.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void modifyPersona(Persona old, Persona p, String user) {

	}

	
	public Vector<Persona> getRubrica(String user) {
		Vector<Persona> tmp = new Vector<Persona>();
		String query = "SELECT * FROM rubrica WHERE rif_user='" + user+ "' ORDER BY NOME ASC";
		Statement stmt;
		try {
			stmt = (Statement) connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				Persona p = new Persona(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6));
				tmp.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp;
	}
	
	public Database() {
		
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
		
		try {
		    connection = (Connection) DriverManager.getConnection(connectionString);
		    // Controllo se esistono le tabelle
			DatabaseMetaData dbm = (DatabaseMetaData) connection.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "login", null);
			if (tables.next()) {
				System.out.println("Tabelle MySql esistenti");
			}
			else {
				System.out.println("Tabelle MySql da creare...");
				createTables();
			}
		 
		} catch (SQLException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		}
	}
	
}
