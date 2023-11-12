package AEV2;

import javax.swing.SwingUtilities;

/**
 * Principal: Classe principal que inicia l'aplicació i crea les instàncies del model, la vista i el controlador.
 */
public class Principal {

    /**
     * Mètode principal que inicia l'aplicació.
     * 
     * @param args Arguments de línia de comandament (no utilitzats en aquest cas).
     */
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            Vista vista = new Vista();
            Model model = new Model();
            Controlador controlador = new Controlador(model, vista);


            vista.setVisible(true);
        });
    }

}
