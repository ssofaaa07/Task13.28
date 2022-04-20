package ru.vsu.cs.vvp2021.g12.butovetskaya_s_i.task13.n28;

import utils.SwingUtils;

import java.util.Locale;

public class Program {

	public static void main(String[] args) throws Exception {
            Locale.setDefault(Locale.ROOT);
            //SwingUtils.setLookAndFeelByName("Windows");
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtils.setDefaultFont("Microsoft Sans Serif", 14);

            java.awt.EventQueue.invokeLater(() -> new MainForm().setVisible(true));
        }

}
