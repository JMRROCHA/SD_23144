package edu.ufp.inf.sd.project.frogger.client;

import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameFactoryRI;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameSessionRI;
import edu.ufp.inf.sd.project.frogger.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameClient extends javax.swing.JFrame {

    private static GameClient gameClient;
    private static GameSessionRI gameSessionRI;
    private javax.swing.JButton jButton_Login;
    private javax.swing.JButton jButton_NewAccount;
    private javax.swing.JButton jButton_TogglePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel_Login;
    private javax.swing.JLabel jLabel_Password;
    private javax.swing.JPanel jPanel_Login;
    private javax.swing.JPanel jPanel_NewAccount;
    private javax.swing.JPasswordField jPasswordField_Password;
    private javax.swing.JTextField jTextField_NAName;
    private javax.swing.JPasswordField jPasswordField_NAPassword;
    private javax.swing.JTextField jTextField_NAUsername;
    private javax.swing.JTextField jTextField_Username;
    private SetupContextRMI contextRMI;
    private GameFactoryRI gameFactoryRI;

    public GameClient(String[] args) {
        initGuiComponents();
        initContextRMI(args);
    }

    public static void main(String[] args) throws RemoteException {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        java.awt.EventQueue.invokeLater(() -> {
            if (args != null && args.length < 2) {
                System.exit(-1);
            } else {
                gameClient = new GameClient(args);
                gameClient.lookupService();

                gameClient.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                gameClient.setTitle("Login");
                gameClient.setResizable(false);
                gameClient.setLocationRelativeTo(null);
                gameClient.setVisible(true);
                gameClient.setSize(225, 270);
                gameClient.pack();
            }
        });
    }

    private void initGuiComponents() {

        jPanel_NewAccount = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField_NAUsername = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField_NAPassword = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jTextField_NAName = new javax.swing.JTextField();
        jButton_NewAccount = new javax.swing.JButton();
        jPanel_Login = new javax.swing.JPanel();
        jLabel_Login = new javax.swing.JLabel();
        jTextField_Username = new javax.swing.JTextField();
        jLabel_Password = new javax.swing.JLabel();
        jPasswordField_Password = new javax.swing.JPasswordField();
        jButton_Login = new javax.swing.JButton();
        jButton_TogglePanel = new javax.swing.JButton();

        jPanel_NewAccount.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setText("Username");

        jLabel2.setText("Password");

        jLabel3.setText("Nome");

        jButton_NewAccount.setText("New Account");

        javax.swing.GroupLayout jPanel_NewAccountLayout = new javax.swing.GroupLayout(jPanel_NewAccount);
        jPanel_NewAccount.setLayout(jPanel_NewAccountLayout);
        jPanel_NewAccountLayout.setHorizontalGroup(
                jPanel_NewAccountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_NewAccountLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel_NewAccountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField_NAName)
                                        .addComponent(jLabel2)
                                        .addComponent(jPasswordField_NAPassword)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField_NAUsername)
                                        .addComponent(jButton_NewAccount, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_NewAccountLayout.setVerticalGroup(
                jPanel_NewAccountLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_NewAccountLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_NAUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPasswordField_NAPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField_NAName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_NewAccount)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_Login.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel_Login.setPreferredSize(new java.awt.Dimension(212, 211));

        jLabel_Login.setText("Username");

        jLabel_Password.setText("Password");

        jButton_Login.setText("Login");

        jButton_TogglePanel.setText(">>");

        javax.swing.GroupLayout jPanel_LoginLayout = new javax.swing.GroupLayout(jPanel_Login);
        jPanel_Login.setLayout(jPanel_LoginLayout);
        jPanel_LoginLayout.setHorizontalGroup(
                jPanel_LoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_LoginLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel_LoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton_Login, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel_Login)
                                        .addComponent(jPasswordField_Password, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField_Username, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel_Password)
                                        .addComponent(jButton_TogglePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        jPanel_LoginLayout.setVerticalGroup(
                jPanel_LoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel_LoginLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel_Login)
                                .addGap(6, 6, 6)
                                .addComponent(jTextField_Username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(jLabel_Password)
                                .addGap(9, 9, 9)
                                .addComponent(jPasswordField_Password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton_Login)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_TogglePanel)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel_Login, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jPanel_NewAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel_NewAccount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel_Login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_NewAccount.setVisible(false);

        jButton_TogglePanel.addActionListener((ActionEvent e) -> togglePanel());
        jButton_NewAccount.addActionListener((ActionEvent e) -> doNewAccount());
        jButton_Login.addActionListener((ActionEvent e) -> doLogin());

        pack();
    }

    private void initContextRMI(String[] args) {
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Remote lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = contextRMI.getServicesUrl(0);
                gameFactoryRI = (GameFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return gameFactoryRI;
    }

    private void doNewAccount() {
        try {
            boolean result = this.gameFactoryRI.register(jTextField_NAUsername.getText(), String.valueOf(jPasswordField_NAPassword.getPassword()), jTextField_NAName.getText());
            if (result == true) {
                JOptionPane.showMessageDialog(null, "User Created!");
            } else {
                JOptionPane.showMessageDialog(null, "Error creating user");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void doLogin() {
        try {
            gameSessionRI = this.gameFactoryRI.login(jTextField_Username.getText(), String.valueOf(jPasswordField_Password.getPassword()));
            if (gameSessionRI == null) {
                JOptionPane.showMessageDialog(null, "Login Error!");
            } else {
                new GameClientMain(contextRMI, gameFactoryRI, gameSessionRI);
                this.dispose();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void togglePanel() {
        if (!jPanel_NewAccount.isVisible()) {
            jPanel_NewAccount.setVisible(true);
            this.setSize(439, 270);
            jButton_TogglePanel.setText("<<");
        } else {
            jPanel_NewAccount.setVisible(false);
            this.setSize(225, 270);
            jButton_TogglePanel.setText(">>");
        }
    }
}
