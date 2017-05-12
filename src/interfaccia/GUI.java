package interfaccia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import classi.Database;
import classi.Persona;

public class GUI {

	private static Database db = null;
	private static String USER = null;
	private static Vector<Persona> persone;
	private static JFrame login_frame;
	private static JFrame rubrica_frame;
	private static JFrame editor_frame;
	private static Dimension dim;
	private static JTable rubrica = null;
	private static Persona selected = null;
	private static JScrollPane spTable = null;
	private static JPanel rubrica_panel;
	
	
	public static void main(String[] args) {
		
		// Mi connetto al database
		db = new Database();
		
		// Creo la finestra di login
		login_frame = new JFrame("Login");
		JPanel login_panel = new JPanel();
		login_frame.add(login_panel);
		placeLogin(login_panel);
		dim = Toolkit.getDefaultToolkit().getScreenSize();
		login_frame.setSize(300,150);
		login_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login_frame.setLocation(dim.width/2-login_frame.getSize().width/2, dim.height/2-login_frame.getSize().height/2);
		login_frame.setVisible(true);
	}
	
	public static void populateRubrica() {
		persone = db.getRubrica(USER);
	    Object rowData[][] = new Object[persone.size()][3];
	    for (int i = 0; i<persone.size(); i++) {
    		rowData[i][0] = persone.get(i).getNome(); 
    		rowData[i][1] = persone.get(i).getCognome(); 
    		rowData[i][2] = persone.get(i).getTelefono(); 
	    }
	    
		Object columnNames[] = { "Nome", "Cognome", "Telefono" };
		rubrica = new JTable(rowData, columnNames);
		rubrica.setDefaultEditor(Object.class, null);
		spTable = new JScrollPane(rubrica);
		spTable.setBounds(1,26,800,550);
		rubrica_panel.add(spTable);
	}
	
	private static void initRubrica() {
		rubrica_frame = new JFrame("Rubrica");
		login_frame.setVisible(false);
		rubrica_panel = new JPanel();
		placeRubrica(rubrica_panel);
		rubrica_frame.add(rubrica_panel);
		rubrica_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rubrica_frame.setSize(800,600);
		rubrica_frame.setLocation(dim.width/2-rubrica_frame.getSize().width/2, dim.height/2-rubrica_frame.getSize().height/2);
		rubrica_frame.setVisible(true);
		createEditorFrame();
	}

	private static void placeRubrica(JPanel panel) {
		panel.setLayout(null);
		JToolBar toolbar = new JToolBar();
		JButton nuovo = new JButton("Nuovo");
		nuovo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selected = null;
				showEditorFrame();
			}
		});
		
		JButton modifica = new JButton("Modifica");
		modifica.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = rubrica.getSelectedRow();
				if (index == -1) {
				    JOptionPane.showMessageDialog(rubrica_panel, "Nessun contatto selezionato", "Errore", JOptionPane.ERROR_MESSAGE);
				}
				else {
					selected = persone.get(rubrica.getSelectedRow());
					showEditorFrame();
				}
			}
		});
		
		JButton elimina = new JButton("Elimina");
		elimina.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = rubrica.getSelectedRow();
				if (index == -1) {
				    JOptionPane.showMessageDialog(rubrica_panel, "Nessun contatto selezionato", "Errore", JOptionPane.ERROR_MESSAGE);
				}
				else {
					selected = persone.get(rubrica.getSelectedRow());
				    int result = JOptionPane.showConfirmDialog(rubrica_panel, "Eliminare la persona "+selected.getNome()+" "+selected.getCognome()+"?", "Conferma eliminazione", JOptionPane.YES_NO_OPTION);
				    if(result == 0) {
				    	db.removePersona(selected, USER);
				    	populateRubrica();
				    }
				}
			}
		});
		
		toolbar.add(nuovo);
		toolbar.add(modifica);
		toolbar.add(elimina);
		toolbar.setBounds(1, 1, 800, 25);
		panel.add(toolbar);
		
		populateRubrica();
	}
		
	public static void createEditorFrame() {
		editor_frame = new JFrame("Editor");
		JPanel panel = new JPanel();
		panel.setLayout(null);
		
		JLabel nome = new JLabel("Nome:");
		nome.setBounds(10, 10, 100, 40);
		panel.add(nome);

		JLabel cognome = new JLabel("Cognome:");
		cognome.setBounds(10, 60, 100, 40);
		panel.add(cognome);

		JLabel telefono = new JLabel("Telefono:");
		telefono.setBounds(10, 110, 100, 40);
		panel.add(telefono);

		JLabel indirizzo = new JLabel("Indirizzo:");
		indirizzo.setBounds(10, 160, 100, 40);
		panel.add(indirizzo);

		JLabel eta = new JLabel("Età:");
		eta.setBounds(10, 210, 100, 40);
		panel.add(eta);
		
		JTextField set_nome = new JTextField();
		set_nome.setBounds(110, 15, 160, 25);
		panel.add(set_nome);

		JTextField set_cognome = new JTextField();
		set_cognome.setBounds(110, 65, 160, 25);
		
		panel.add(set_cognome);

		JTextField set_telefono = new JTextField();
		set_telefono.setBounds(110, 115, 160, 25);
		
		panel.add(set_telefono);

		JTextField set_indirizzo = new JTextField();
		set_indirizzo.setBounds(110, 165, 160, 25);
		
		panel.add(set_indirizzo);

		JTextField set_eta = new JTextField();
		set_eta.setBounds(110, 215, 160, 25);

		panel.add(set_eta);
		
		JButton salva = new JButton("Salva");
		salva.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Persona p = new Persona(0, set_nome.getText(), set_cognome.getText(), set_indirizzo.getText(), set_telefono.getText(), Integer.parseInt(set_eta.getText()));
				if (selected == null) {
					db.savePersona(p, USER);
				}
				else db.modifyPersona(selected, p, USER);
				populateRubrica();
				editor_frame.setVisible(false);
			}
		});
		salva.setBounds(160, 270, 100, 25);
		panel.add(salva);
		
		JButton annulla = new JButton("Annulla");
		annulla.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				set_nome.setText("");
				set_cognome.setText("");
				set_telefono.setText("");
				set_indirizzo.setText("");
				set_eta.setText("");
				editor_frame.setVisible(false);
			}
		});
		annulla.setBounds(30, 270, 100, 25);
		panel.add(annulla);
		
		editor_frame.add(panel);
		editor_frame.setSize(300, 350);
		editor_frame.setLocation(dim.width/2-editor_frame.getSize().width/2, dim.height/2-editor_frame.getSize().height/2);
		editor_frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}
	
	public static void showEditorFrame() {
		editor_frame.setVisible(true);
		Container pane = editor_frame.getContentPane();
		JTextField nome = (JTextField) pane.getComponent(0).getComponentAt(110, 15);
		JTextField cognome = (JTextField) pane.getComponent(0).getComponentAt(110, 65);
		JTextField indirizzo = (JTextField) pane.getComponent(0).getComponentAt(110, 115);
		JTextField telefono = (JTextField) pane.getComponent(0).getComponentAt(110, 165);
		JTextField eta = (JTextField) pane.getComponent(0).getComponentAt(110, 215);
		if (selected != null) {
			nome.setText(selected.getNome());
			cognome.setText(selected.getCognome());
			indirizzo.setText(selected.getIndirizzo());
			telefono.setText(selected.getTelefono());
			eta.setText(Integer.toString(selected.getEta()));
		}
		else {
			nome.setText("");
			cognome.setText("");
			indirizzo.setText("");
			telefono.setText("");
			eta.setText("");
		}
	}
	
	private static void placeLogin(JPanel panel) {
		panel.setLayout(null);

		JLabel userLabel = new JLabel("Utente:");
		userLabel.setBounds(10, 10, 80, 25);
		panel.add(userLabel);

		JTextField userText = new JTextField();
		userText.setBounds(100, 10, 160, 25);
		panel.add(userText);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(10, 40, 80, 25);
		panel.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField();
		passwordText.setBounds(100, 40, 160, 25);
		panel.add(passwordText);

		JButton loginButton = new JButton("login");
		loginButton.setBounds(100, 80, 80, 25);
		panel.add(loginButton);
		
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String user = userText.getText();
				@SuppressWarnings("deprecation")
				String password = passwordText.getText();
				
				if (db.login(user, password)) {
					USER = user;
					System.out.println("Login riuscito");
					// Prendo la lista di persone per quell'utente
					initRubrica();
				}
				else {
					// Mostro un errore e torno indietro
				    JOptionPane.showMessageDialog(panel, "Login non riuscito", "Errore", JOptionPane.ERROR_MESSAGE);
				}
			}
        });
	}
}
