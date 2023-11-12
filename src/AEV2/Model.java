package AEV2;

import java.io.File;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Model: Classe que gestiona la connexió a la base de dades i executa consultes.
 */
public class Model {
    private static Connection connexio;

    /**
     * Constructor per defecte de la classe Model.
     */
    public Model() {

    }

    /**
     * Estableix una connexió a la base de dades utilitzant la informació proporcionada en un fitxer XML.
     *
     * @param fitxerXml El nom del fitxer XML que conté la informació de connexió.
     * @return Cert si la connexió s'estableix correctament, fals altrament.
     */
    public boolean connectarABaseDades(String fitxerXml) {
        try {
            String cami = System.getProperty("user.dir") + File.separator + fitxerXml;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new File(cami));

            NodeList nodeList = document.getElementsByTagName("connectionInfo");
            Node connectionNode = nodeList.item(0);
            if (connectionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element connectionElement = (Element) connectionNode;

                String url = connectionElement.getElementsByTagName("url").item(0).getTextContent();
                String usuari = connectionElement.getElementsByTagName("username").item(0).getTextContent();
                String contrasenya = connectionElement.getElementsByTagName("password").item(0).getTextContent();

                connexio = DriverManager.getConnection(url, usuari, contrasenya);
                return true;
            } else {
                System.out.println("Error llegint el node de connexió al fitxer XML.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Estableix una connexió a la base de dades amb les credencials proporcionades.
     *
     * @param usuari    El nom d'usuari per a la connexió.
     * @param contrasenya La contrasenya per a la connexió.
     * @param tipusUsuari El tipus d'usuari per determinar el fitxer XML de connexió.
     * @throws SQLException Si hi ha un error durant la connexió.
     */
    public void connectar(String usuari, String contrasenya, String tipusUsuari) throws SQLException {
        connectarABaseDades("connection-" + tipusUsuari + ".xml");
    }

    /**
     * Executa una consulta a la base de dades i retorna el conjunt de resultats.
     *
     * @param consulta La consulta SQL a executar.
     * @return ResultSet amb els resultats de la consulta.
     * @throws SQLException Si hi ha un error durant l'execució de la consulta.
     */
    public ResultSet executarConsulta(String consulta) throws SQLException {
        ResultSet conjuntResultats = null;

        try {
            if (connexio != null) {
                Statement sentencia = connexio.createStatement();
                conjuntResultats = sentencia.executeQuery(consulta);
            } else {
                throw new SQLException("No hi ha connexió a la base de dades.");
            }
        } catch (SQLException e) {
            throw e;
        }

        return conjuntResultats;
    }

    /**
     * Obté el tipus d'usuari basat en el nom d'usuari i la contrasenya.
     *
     * @param usuari   El nom d'usuari per determinar el tipus d'usuari.
     * @param contrasenya La contrasenya per validar l'usuari.
     * @return Tipus d'usuari o null si l'usuari no es troba.
     * @throws SQLException Si hi ha un error durant la consulta a la base de dades.
     */
    public String obtenirTipusUsuari(String usuari, String contrasenya) throws SQLException {
        String tipusUsuari = null;

        try {
            String consulta = "SELECT type, pass FROM users WHERE user=?";
            PreparedStatement sentencia = connexio.prepareStatement(consulta);
            sentencia.setString(1, usuari);
            ResultSet conjuntResultats = sentencia.executeQuery();

            if (conjuntResultats.next()) {
                tipusUsuari = conjuntResultats.getString("type");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tipusUsuari;
    }

    /**
     * Valida l'usuari comparant el nom d'usuari i la contrasenya amb la base de dades.
     *
     * @param usuari   El nom d'usuari per la validació.
     * @param contrasenya La contrasenya per la validació.
     * @return Cert si l'usuari és vàlid, fals altrament.
     */
    public boolean iniciarSessio(String usuari, String contrasenya) {
        try {
            String consulta = "SELECT type, pass FROM users WHERE user=?";
            PreparedStatement sentencia = connexio.prepareStatement(consulta);
            sentencia.setString(1, usuari);
            ResultSet conjuntResultats = sentencia.executeQuery();

            if (conjuntResultats.next()) {
                String tipusUsuari = conjuntResultats.getString("type");
                String hashDesDeBD = conjuntResultats.getString("pass");

                String contrasenyaXifrada = obtenirMD5(contrasenya);

                if (hashDesDeBD.equals(contrasenyaXifrada)) {
                    if (tipusUsuari.equals("admin")) {
                        connexio.close();
                        connectarABaseDades("connection-admin.xml");
                        System.out.println("Connexió tancada i XML d'administrador obert.");
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Calcula el hash MD5 d'una cadena d'entrada.
     *
     * @param entrada La cadena d'entrada per a la qual es calcularà el hash MD5.
     * @return Hash MD5 de la cadena d'entrada.
     */
    public String obtenirMD5(String entrada) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashMissatge = md.digest(entrada.getBytes());

            StringBuilder cadenaHexadecimal = new StringBuilder();
            for (byte b : hashMissatge) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    cadenaHexadecimal.append('0');
                }
                cadenaHexadecimal.append(hex);
            }
            return cadenaHexadecimal.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Executa una consulta i mostra els resultats en un JTextArea.
     *
     * @param consulta         La consulta SQL a executar.
     * @param txtPanellInfo    JTextArea on es mostraran els resultats de la consulta.
     * @param tipusUsuari      El tipus d'usuari.
     */
    public void executarIDesplegarResultatsConsulta(String consulta, JTextArea txtPanellInfo, String tipusUsuari) throws SQLException{
        try {
            PreparedStatement sentencia = connexio.prepareStatement(consulta);
            boolean esConjuntResultats = sentencia.execute();
            
            
            
            if (esConjuntResultats) {
                ResultSet conjuntResultats = sentencia.getResultSet();
                ResultSetMetaData metaData = conjuntResultats.getMetaData();

                StringBuilder resultat = new StringBuilder();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    resultat.append(metaData.getColumnName(i)).append("\t");
                }
                resultat.append("\n");

                while (conjuntResultats.next()) {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        resultat.append(conjuntResultats.getString(i)).append("\t");
                    }
                    resultat.append("\n");
                }

                txtPanellInfo.setText(resultat.toString());

                conjuntResultats.close();
            } else {
                int filesAfectades = sentencia.getUpdateCount();
                if (filesAfectades > 0) {
                    JOptionPane.showMessageDialog(null, "Operació completada amb èxit. Files afectades: " + filesAfectades);
                } else {
                    JOptionPane.showMessageDialog(null, "L'operació no ha tingut efecte.");
                }
            }

            sentencia.close();
        } catch (SQLSyntaxErrorException e) {
        	JOptionPane.showMessageDialog(null, "Error de sintaxis SQL");
            e.printStackTrace();
        }	catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error durante la ejecución de la consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tanca la connexió a la base de dades.
     */
    public void tancarConnexio() {
        try {
            if (connexio != null && !connexio.isClosed()) {
                connexio.close();
                System.out.println("Connexió tancada");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error tancant la connexió");
        }
    }
}
