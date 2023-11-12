package AEV2;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;

/**
 * Vista: Classe que representa la interfície d'usuari de l'aplicació de gestió de base de dades.
 */
public class Vista extends JFrame {

    private static final long serialVersionUID = 1L;
    public JPanel panellContingut;
    public JTextField campNomUsuari;
    public JPasswordField campContrasenya;
    public JTextArea areaTextConsulta;
    public JTextArea areaTextResultats;
    public JPanel panellConsulta;
    public JPanel panellResultats;
    public JButton botoIniciarSessio;
    public JButton botoExecutarConsulta;
    public JButton botoTancarSessio;
    public JButton botoTancarConnexio;

    /**
     * Constructor de la classe Vista. Inicialitza i configura els elements de la interfície d'usuari.
     */
    public Vista() {

        setTitle("Aplicació de Gestió de Base de Dades");
        setSize(800, 624);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panellIniciSessio = new JPanel();
        panellIniciSessio.setBackground(Color.LIGHT_GRAY);
        campNomUsuari = new JTextField(20);
        campNomUsuari.setBounds(298, 54, 166, 20);
        campContrasenya = new JPasswordField(20);
        campContrasenya.setBounds(298, 110, 166, 20);
        botoIniciarSessio = new JButton("Iniciar Sessió");

        botoIniciarSessio.setBounds(322, 141, 123, 23);
        panellIniciSessio.setLayout(null);
        JLabel etiquetaNomUsuari = new JLabel("Usuari");
        etiquetaNomUsuari.setBounds(355, 29, 56, 14);
        panellIniciSessio.add(etiquetaNomUsuari);
        panellIniciSessio.add(campNomUsuari);
        JLabel etiquetaContrasenya = new JLabel("Contrasenya: ");
        etiquetaContrasenya.setBounds(339, 85, 85, 14);
        panellIniciSessio.add(etiquetaContrasenya);
        panellIniciSessio.add(campContrasenya);
        panellIniciSessio.add(botoIniciarSessio);

        panellConsulta = new JPanel();
        panellConsulta.setBackground(Color.LIGHT_GRAY);
        panellConsulta.setVisible(false);
        JScrollPane desplaçamentConsulta = new JScrollPane();
        desplaçamentConsulta.setBounds(532, 96, 2, 2);
        botoExecutarConsulta = new JButton("Executar Consulta");
        botoExecutarConsulta.setBounds(539, 85, 150, 23);
        panellConsulta.setLayout(null);
        JLabel etiquetaConsulta = new JLabel("Consulta SQL: ");
        etiquetaConsulta.setBounds(109, 90, 89, 14);
        panellConsulta.add(etiquetaConsulta);
        areaTextConsulta = new JTextArea(10, 40);
        areaTextConsulta.setLineWrap(true);
        areaTextConsulta.setBounds(203, 5, 324, 184);
        panellConsulta.add(areaTextConsulta);
        panellConsulta.add(desplaçamentConsulta);
        panellConsulta.add(botoExecutarConsulta);

        panellResultats = new JPanel();
        panellResultats.setBackground(Color.LIGHT_GRAY);
        panellResultats.setVisible(false);
        areaTextResultats = new JTextArea(10, 40);
        areaTextResultats.setEditable(false);
        JScrollPane desplaçamentResultats = new JScrollPane(areaTextResultats);
        desplaçamentResultats.setBounds(200, 0, 326, 186);
        botoTancarSessio = new JButton("Tancar Sessió");
        botoTancarSessio.setBounds(536, 69, 128, 23);

        botoTancarConnexio = new JButton("Tancar Connexió");
        botoTancarConnexio.setBounds(536, 104, 130, 23);

        panellResultats.setLayout(null);
        JLabel etiquetaResultats = new JLabel("Resultats: ");
        etiquetaResultats.setBounds(115, 85, 75, 14);
        panellResultats.add(etiquetaResultats);
        panellResultats.add(desplaçamentResultats);
        panellResultats.add(botoTancarSessio);
        panellResultats.add(botoTancarConnexio);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(panellIniciSessio);
        getContentPane().add(panellConsulta);
        getContentPane().add(panellResultats);
    }

    /**
     * Mostra un missatge d'error en una finestra emergent.
     * 
     * @param missatge El missatge d'error a mostrar.
     */
    public void mostrarMissatgeError(String missatge) {
        JOptionPane.showMessageDialog(this, missatge, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Mostra un missatge d'informació en una finestra emergent.
     * 
     * @param missatge El missatge d'informació a mostrar.
     * 
     * Nota: Aquest mètode és privat ja que no s'utilitza directament fora de la classe.
     */
    private void mostrarMissatgeInformacio(String missatge) {
        JOptionPane.showMessageDialog(this, missatge, "Informació", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostra o amaga els components de la interfície d'usuari basant-se en la condició proporcionada.
     * 
     * @param comp Cert si els components han de ser mostrats, fals si han de ser amagats.
     */
    public void mostrarComponents(boolean comp) {
        panellConsulta.setVisible(comp);
        panellResultats.setVisible(comp);
    }
}
