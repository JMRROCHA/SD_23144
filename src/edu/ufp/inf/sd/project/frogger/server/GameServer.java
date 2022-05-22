package edu.ufp.inf.sd.project.frogger.server;

import edu.ufp.inf.sd.project.frogger.resources.classes.User;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameFactoryImpl;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameFactoryRI;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.GameSessionRI;
import edu.ufp.inf.sd.project.frogger.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameServer extends javax.swing.JFrame {
    private static javax.swing.JTable jTableUserSessions;
    private static javax.swing.JTable jTableUsers;

    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JPanel jPanelGameSessions;
    private javax.swing.JPanel jPanelServers;
    private javax.swing.JPanel jPanelUserSessions;
    private javax.swing.JPanel jPanelUsers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane;
    private SetupContextRMI contextRMI;
    private GameFactoryRI gamefactoryRI;

    public GameServer(String[] args) {
        initGuiComponents();
        initContextRMI(args);
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }

        java.awt.EventQueue.invokeLater(() -> {
            if (args != null && args.length < 3) {
                System.exit(-1);
            } else {
                //1. ============ Create Servant ============
                GameServer gameServer = new GameServer(args);
                //2. ============ Rebind servant on rmiregistry ============
                gameServer.rebindService();

                gameServer.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                gameServer.setExtendedState(JFrame.MAXIMIZED_BOTH);
                gameServer.setMinimumSize(new java.awt.Dimension(800, 600));
                gameServer.setTitle("GameServer");
                gameServer.setResizable(true);
                gameServer.setLocationRelativeTo(null);
                gameServer.setVisible(true);
                gameServer.pack();

                gameServer.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        exitApp();
                    }
                });
            }
        });
    }

    private static void exitApp() {
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to close this window?", "Close Window?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void updateUsersTable() {

        ArrayList<User> users = GameFactoryImpl.bd.getUsers();

        DefaultTableModel model = (DefaultTableModel) jTableUsers.getModel();
        model.setRowCount(0);

        if (users != null) {
            for (User user : users) {
                Object[] row = {user.getUsername(), user.getPassword(), user.getName()};
                model.addRow(row);
            }
        }
    }

    public static void updateUserSessionsTable() {

        HashMap<String, GameSessionRI> sessions = GameFactoryImpl.sessions;

        DefaultTableModel model = (DefaultTableModel) jTableUserSessions.getModel();
        model.setRowCount(0);

        if (sessions != null) {
            sessions.forEach(
                    (key, value) -> {
                        Object[] row = {key, value.toString()};
                        model.addRow(row);
                    }
            );
        }
    }

    private void initContextRMI(String[] args) {
        try {
            //============ List and Set args ============
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            //============ Create a context for RMI setup ============
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            Registry registry = contextRMI.getRegistry();

            if (registry != null) {
                //============ Create Servant ============
                gamefactoryRI = new GameFactoryImpl() {
                };
                //Get service url (including servicename)
                String serviceUrl = contextRMI.getServicesUrl(0);
                //============ Rebind servant ============
                registry.rebind(serviceUrl, gamefactoryRI);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
                //registry = LocateRegistry.createRegistry(1099);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initGuiComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelUsers = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableUsers = new javax.swing.JTable();
        jPanelServers = new javax.swing.JPanel();
        jPanelUserSessions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableUserSessions = new javax.swing.JTable();
        jPanelGameSessions = new javax.swing.JPanel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        Exit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableUsers.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Username", "Password", "Name"
                }
        ) {
            final Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            final boolean[] canEdit = new boolean[]{
                    false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableUsers);

        javax.swing.GroupLayout jPanelUsersLayout = new javax.swing.GroupLayout(jPanelUsers);
        jPanelUsers.setLayout(jPanelUsersLayout);
        jPanelUsersLayout.setHorizontalGroup(
                jPanelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        jPanelUsersLayout.setVerticalGroup(
                jPanelUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelUsersLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Users", jPanelUsers);

        javax.swing.GroupLayout jPanelServersLayout = new javax.swing.GroupLayout(jPanelServers);
        jPanelServers.setLayout(jPanelServersLayout);
        jPanelServersLayout.setHorizontalGroup(
                jPanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 920, Short.MAX_VALUE)
        );
        jPanelServersLayout.setVerticalGroup(
                jPanelServersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 574, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("Servers", jPanelServers);

        jTableUserSessions.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Username", "Session"
                }
        ) {
            final Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class
            };
            final boolean[] canEdit = new boolean[]{
                    false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableUserSessions);

        javax.swing.GroupLayout jPanelUserSessionsLayout = new javax.swing.GroupLayout(jPanelUserSessions);
        jPanelUserSessions.setLayout(jPanelUserSessionsLayout);
        jPanelUserSessionsLayout.setHorizontalGroup(
                jPanelUserSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        jPanelUserSessionsLayout.setVerticalGroup(
                jPanelUserSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("UserSessions", jPanelUserSessions);

        javax.swing.GroupLayout jPanelGameSessionsLayout = new javax.swing.GroupLayout(jPanelGameSessions);
        jPanelGameSessions.setLayout(jPanelGameSessionsLayout);
        jPanelGameSessionsLayout.setHorizontalGroup(
                jPanelGameSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 920, Short.MAX_VALUE)
        );
        jPanelGameSessionsLayout.setVerticalGroup(
                jPanelGameSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 574, Short.MAX_VALUE)
        );

        jTabbedPane.addTab("GameSessions", jPanelGameSessions);

        jMenuFile.setText("File");

        Exit.setText("jMenuItem1");
        Exit.addActionListener(evt -> exitApp());
        jMenuFile.add(Exit);

        jMenuBar.add(jMenuFile);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTabbedPane)
        );

        pack();

    }
}
