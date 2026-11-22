package org.presentacion.mvc;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.GridLayout;

public final class FrmConfiguracionPartida {
    private FrmConfiguracionPartida() {
    }

    public static int[] solicitarConfiguracion() {
        JSpinner fichas = new JSpinner(new SpinnerNumberModel(4, 3, 6, 1));
        JSpinner seguras = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        JSpinner dadoSalida = new JSpinner(new SpinnerNumberModel(5, 1, 6, 1));

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new javax.swing.JLabel("Fichas por jugador:"));
        panel.add(fichas);
        panel.add(new javax.swing.JLabel("Casillas seguras por ala:"));
        panel.add(seguras);
        panel.add(new javax.swing.JLabel("Dado necesario para salir:"));
        panel.add(dadoSalida);

        int opcion = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Crear partida",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        return new int[] {
            (int) fichas.getValue(),
            (int) seguras.getValue(),
            (int) dadoSalida.getValue()
        };
    }
}
