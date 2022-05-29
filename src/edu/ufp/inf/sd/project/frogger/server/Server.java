package edu.ufp.inf.sd.project.frogger.server;

import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.resources.classes.MessagesExchange;
import edu.ufp.inf.sd.project.frogger.resources.classes.Player;
import edu.ufp.inf.sd.project.frogger.resources.classes.User;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.FactoryImpl;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.FactoryRI;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.SessionRI;
import edu.ufp.inf.sd.project.frogger.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends javax.swing.JFrame {
    static HashMap<String, String> argsRMQ = new HashMap<>();
    private static javax.swing.JTable jTableUserSessions;
    private static javax.swing.JTable jTableUsers;
    private static javax.swing.JTable jTableGameSessions;
    private final String hostIp;
    private final String rmiPort;
    private final String rmiServiceName;
    MessagesExchange messagesExchange;
    private javax.swing.JMenuItem jButtonExit;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JPanel jPanelGameSessions;
    private javax.swing.JPanel jPanelLog;
    private javax.swing.JPanel jPanelUserSessions;
    private javax.swing.JPanel jPanelUsers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextArea jTextAreaLog;
    private SetupContextRMI contextRMI;
    private FactoryRI factoryRI;

    public Server(String[] args) {

        hostIp = args[0];
        rmiPort = args[1];
        rmiServiceName = args[2];

        initContextRMI();

        argsRMQ.put("hostIp", hostIp);
        argsRMQ.put("rmqPort", args[3]);
        argsRMQ.put("exchangeName", args[4]);

        messagesExchange = new MessagesExchange(argsRMQ);
        messagesExchange.run();
        messagesExchange.consumeRMQ("server");

        initGuiComponents();
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
                Server server = new Server(args);
                server.rebindService();

                server.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                server.setExtendedState(JFrame.MAXIMIZED_BOTH);
                server.setMinimumSize(new java.awt.Dimension(800, 600));
                server.setTitle("GameServer");
                server.setResizable(true);
                server.setLocationRelativeTo(null);
                server.setVisible(true);
                server.pack();

                server.addWindowListener(new java.awt.event.WindowAdapter() {
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

    public static void updateTables() {
        updateUsersTable();
        updateUserSessionsTable();
        updateGameSessionsTable();
    }

    private static void updateUsersTable() {
        ArrayList<User> users = FactoryImpl.bd.getUsers();

        DefaultTableModel model = (DefaultTableModel) jTableUsers.getModel();
        model.setRowCount(0);

        for (User user : users) {
            Object[] row = {user.getUsername(), user.getPassword(), user.getName()};
            model.addRow(row);
        }

    }

    private static void updateUserSessionsTable() {
        HashMap<String, SessionRI> sessions = FactoryImpl.sessions;

        DefaultTableModel model = (DefaultTableModel) jTableUserSessions.getModel();
        model.setRowCount(0);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        sessions.forEach(
                (key, value) -> {
                    Object[] row = new Object[0];
                    try {
                        row = new Object[]{key, formatter.format(value.getDate()), value.getToken(), value.toString()};
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    model.addRow(row);
                }
        );
    }

    private static void updateGameSessionsTable() {
        HashMap<String, GameSessionManagement> gameSessions = FactoryImpl.gameSessions;

        DefaultTableModel model = (DefaultTableModel) jTableGameSessions.getModel();
        model.setRowCount(0);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        gameSessions.forEach(
                (key, value) -> {
                    String queueName = key;
                    String date = formatter.format(value.getDate());
                    ArrayList<String> usernames = new ArrayList<>();
                    ArrayList<Integer> levels = new ArrayList<>();
                    ArrayList<Integer> scores = new ArrayList<>();

                    for (Player player : value.getPlayers()) {
                        usernames.add(player.getUsername());
                        levels.add(player.getLevel());
                        scores.add(player.getScore());
                    }

                    Object[] row = {queueName, date, usernames.toString(), levels.toString(), scores.toString()};
                    model.addRow(row);

                }
        );
    }

    public static void newExchangeRoom(String roomName) {
        MessagesExchange messagesExchange = new MessagesExchange(argsRMQ);
        messagesExchange.run();
        messagesExchange.consumeRMQ(roomName);
    }

    private void initContextRMI() {
        try {
            contextRMI = new SetupContextRMI(this.getClass(), hostIp, rmiPort, new String[]{rmiServiceName});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void rebindService() {
        try {
            Registry registry = contextRMI.getRegistry();

            if (registry != null) {
                factoryRI = new FactoryImpl() {
                };

                String serviceUrl = contextRMI.getServicesUrl(0);
                registry.rebind(serviceUrl, factoryRI);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
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
        jPanelLog = new javax.swing.JPanel();
        jPanelUserSessions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableUserSessions = new javax.swing.JTable();
        jTableGameSessions = new javax.swing.JTable();
        jPanelGameSessions = new javax.swing.JPanel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jButtonExit = new javax.swing.JMenuItem();
        jTextAreaLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);


        jTableUsers.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Username", "Password", "Name"}) {
            final Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class};
            final boolean[] canEdit = new boolean[]{false, false, false};

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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );


        jTableUserSessions.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Username", "Start Time", "Token", "Session"}) {
            final Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class};
            final boolean[] canEdit = new boolean[]{false, false, false, false};

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


        jTableGameSessions.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Room", "Start Time", "Users", "Level", "Score"}) {
            final Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class};
            final boolean[] canEdit = new boolean[]{false, false, false, false, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableGameSessions);

        javax.swing.GroupLayout jPanelGameSessionsLayout = new javax.swing.GroupLayout(jPanelGameSessions);
        jPanelGameSessions.setLayout(jPanelGameSessionsLayout);
        jPanelGameSessionsLayout.setHorizontalGroup(
                jPanelGameSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        jPanelGameSessionsLayout.setVerticalGroup(
                jPanelGameSessionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );


        jTextAreaLog.setColumns(20);
        jTextAreaLog.setRows(5);
        jScrollPane4.setViewportView(jTextAreaLog);

        javax.swing.GroupLayout jPanelLogLayout = new javax.swing.GroupLayout(jPanelLog);
        jPanelLog.setLayout(jPanelLogLayout);
        jPanelLogLayout.setHorizontalGroup(
                jPanelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
        );
        jPanelLogLayout.setVerticalGroup(
                jPanelLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );


        jTabbedPane.addTab("Users", jPanelUsers);
        jTabbedPane.addTab("UserSessions", jPanelUserSessions);
        jTabbedPane.addTab("GameSessions", jPanelGameSessions);
        jTabbedPane.addTab("Log", jPanelLog);


        jMenuFile.setText("File");

        jButtonExit.setText("Exit");
        jButtonExit.addActionListener(evt -> exitApp());
        jMenuFile.add(jButtonExit);

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
