package edu.ufp.inf.sd.project.frogger.client;

import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameFactoryRI;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameSessionRI;
import edu.ufp.inf.sd.project.frogger.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import java.rmi.RemoteException;


public class GameClientMain extends javax.swing.JFrame {
    private static GameSessionRI gameSessionRI;
    private final SetupContextRMI contextRMI;
    private final GameFactoryRI gameFactoryRI;

    public GameClientMain(SetupContextRMI contextRMI, GameFactoryRI gameFactoryRI, GameSessionRI gameSessionRI) {
        this.contextRMI = contextRMI;
        this.gameFactoryRI = gameFactoryRI;
        GameClientMain.gameSessionRI = gameSessionRI;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        java.awt.EventQueue.invokeLater(() -> {

                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setMinimumSize(new java.awt.Dimension(800, 600));
                setTitle("GameClient");
                setResizable(true);
                setLocationRelativeTo(null);
                setVisible(true);
                pack();

                addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        exitApp();
                    }
                });

        });

    }

    private static void exitApp() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to close this window?", "Close Window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
                gameSessionRI.logout();
            } catch (RemoteException e) {
                e.printStackTrace();
            }finally {
                System.exit(0);
            }
        }
    }


}
