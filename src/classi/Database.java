package classi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.DatabaseMetaData;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;


public class Database {

	private static final String FILENAME = "config.ini";
	private static String USER = null;
	private static String IP = null;
	private static String PASS = null;
	private static String PORT = null;
	private static String DB = null;
	private static String connectionString = null;
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
		System.out.println(old+ "\n"+ p);
		String update = "UPDATE rubrica SET nome=?, cognome=?, indirizzo=?, telefono=?, eta=? WHERE nome=? AND cognome=? AND indirizzo=? AND telefono=? AND eta=? AND rif_user=?";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = (PreparedStatement) connection.prepareStatement(update);
		    preparedStmt.setString(1, p.getNome());
		    preparedStmt.setString(2, p.getCognome());
		    preparedStmt.setString(3, p.getIndirizzo());
		    preparedStmt.setString(4, p.getTelefono());
		    preparedStmt.setInt(5, p.getEta());
		    preparedStmt.setString(6, old.getNome());
		    preparedStmt.setString(7, old.getCognome());
		    preparedStmt.setString(8, old.getIndirizzo());
		    preparedStmt.setString(9, old.getTelefono());
		    preparedStmt.setInt(10, old.getEta());
		    preparedStmt.setString(11, user);
	        preparedStmt.execute();
	        System.out.println("Modifica persona effettuata:"+ p.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removePersona(Persona p, String user) {
		String delete = "DELETE FROM rubrica WHERE nome=? AND cognome=? AND indirizzo=? AND telefono=? AND eta=? AND rif_user=?";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = (PreparedStatement) connection.prepareStatement(delete);
		    preparedStmt.setString(1, p.getNome());
		    preparedStmt.setString(2, p.getCognome());
		    preparedStmt.setString(3, p.getIndirizzo());
		    preparedStmt.setString(4, p.getTelefono());
		    preparedStmt.setInt(5, p.getEta());
		    preparedStmt.setString(6, user);
	        preparedStmt.execute();
	        System.out.println("Eliniazione persona effettuata:"+ p.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.startsWith("IP="))
					IP = sCurrentLine.split("=")[1];
				else if (sCurrentLine.startsWith("PORT="))
					PORT = sCurrentLine.split("=")[1];
				else if (sCurrentLine.startsWith("DB_NAME="))
					DB = sCurrentLine.split("=")[1];
				else if (sCurrentLine.startsWith("USER="))
					USER = sCurrentLine.split("=")[1];
				else if (sCurrentLine.startsWith("PASS="))
					PASS = sCurrentLine.split("=")[1];
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		connectionString = "jdbc:mysql://"+IP+":"+PORT+"/"+DB+"?user="+USER+"&password="+PASS;

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
