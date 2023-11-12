package AEV2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Controlador: Classe que gestiona els esdeveniments de la interfície d'usuari i coordina les accions entre la Vista i el Model.
 */
public class Controlador {
    private Model model;
    private Vista vista;
    private String tipusUsuari;

    /**
     * Constructor de la classe Controlador.
     * 
     * @param model Instància del Model a ser utilitzada pel controlador.
     * @param vista Instància de la Vista a ser utilitzada pel controlador.
     */
    public Controlador(Model model, Vista vista) {
        this.model = model;
        this.vista = vista;
        initManejadorsEsdeveniments();
    }

    /**
     * Inicialitza els manejadors d'esdeveniments per a les accions de la interfície d'usuari.
     */
    public void initManejadorsEsdeveniments() {
        try {
            model.connectarABaseDades("./connection-client.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        vista.botoIniciarSessio.addActionListener(new ActionListener() {
            /**
             * Acció que es realitza quan es fa clic al botó d'iniciar sessió.
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    String usuari = vista.campNomUsuari.getText();
                    String contrasenya = vista.campContrasenya.getText();
                    boolean sessioIniciada = model.iniciarSessio(usuari, contrasenya);
                    if (sessioIniciada) {
                        JOptionPane.showMessageDialog(null, "Sessió Iniciada");
                        vista.mostrarComponents(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Usuari o contrasenya incorrectes");
                        vista.campNomUsuari.setText("");
                        vista.campContrasenya.setText("");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        vista.botoExecutarConsulta.addActionListener(new ActionListener() {
            /**
             * Acció que es realitza quan es fa clic al botó d'executar consulta.
             */
            public void actionPerformed(ActionEvent arg0) {
                try {
                    String consulta = vista.areaTextConsulta.getText();
                    tipusUsuari = model.obtenirTipusUsuari(vista.campNomUsuari.getText(), vista.campContrasenya.getText());

                    if (consulta.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Has d'escriure alguna consulta abans.", "Advertència", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if ("client".equalsIgnoreCase(tipusUsuari) && !consulta.trim().toLowerCase().startsWith("select")) {
                        JOptionPane.showMessageDialog(null, "Els clients només poden realitzar consultes SELECT.");
                        return;
                    }

                    if (("admin".equalsIgnoreCase(tipusUsuari) || "root".equalsIgnoreCase(tipusUsuari)) &&
                            (consulta.trim().toLowerCase().startsWith("insert") || consulta.trim().toLowerCase().startsWith("update") || consulta.trim().toLowerCase().startsWith("delete"))) {
                        int confirmacion = JOptionPane.showConfirmDialog(null, "Estàs segur que vols executar aquesta consulta?", "Confirmació", JOptionPane.YES_NO_OPTION);
                        if (confirmacion != JOptionPane.YES_OPTION) {
                            return; 
                        }
                    }

                    model.executarIDesplegarResultatsConsulta(consulta, vista.areaTextResultats, tipusUsuari);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        vista.botoTancarSessio.addActionListener(new ActionListener() {
            /**
             * Acció que es realitza quan es fa clic al botó de tancar sessió.
             */
            public void actionPerformed(ActionEvent arg0) {
                try {
                    model.tancarConnexio();
                    model.connectarABaseDades("./connection-client.xml");
                    vista.mostrarComponents(false);
                    vista.campNomUsuari.setText("");
                    vista.campContrasenya.setText("");
                    vista.areaTextConsulta.setText("");
                    vista.areaTextResultats.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        vista.botoTancarConnexio.addActionListener(new ActionListener() {
            /**
             * Acció que es realitza quan es fa clic al botó de tancar connexió.
             */
            public void actionPerformed(ActionEvent arg0) {
                try {
                    model.tancarConnexio();
                    vista.mostrarComponents(false);
                    vista.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
