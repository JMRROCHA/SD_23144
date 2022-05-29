package edu.ufp.inf.sd.project.frogger.client;

import edu.ufp.inf.sd.project.frogger.resources.classes.GameSessionManagement;
import edu.ufp.inf.sd.project.frogger.resources.classes.MessagesExchange;
import edu.ufp.inf.sd.project.frogger.resources.classes.Player;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.FactoryRI;
import edu.ufp.inf.sd.project.frogger.resources.remoteInterfaces.SessionRI;
import edu.ufp.inf.sd.project.frogger.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientMain extends javax.swing.JFrame {

    private static SessionRI sessionRI;
    private static javax.swing.JTable jTableGameSessions;
    private final SetupContextRMI contextRMI;
    private final FactoryRI factoryRI;
    private final HashMap<String, String> argsRMQ;
    private javax.swing.JButton jButtonNewGameSession;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem_Exit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;

    public ClientMain(SetupContextRMI contextRMI, FactoryRI factoryRI, SessionRI sessionRI, HashMap<String, String> argsRMQ) {
        this.contextRMI = contextRMI;
        this.factoryRI = factoryRI;
        ClientMain.sessionRI = sessionRI;
        this.argsRMQ = argsRMQ;

        MessagesExchange messagesExchange = new MessagesExchange(argsRMQ);
        messagesExchange.run();
        messagesExchange.consumeRMQ("client.*");

        initGuiComponents();

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
                sessionRI.logout();
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    public static void updateGameSessionsTable() {

        HashMap<String, GameSessionManagement> gameSessions = null;
        try {
            gameSessions = sessionRI.listGameSessions();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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

    private void initGuiComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableGameSessions = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonNewGameSession = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem_Exit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableGameSessions.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"Room", "Start Time", "Users", "Level", "Score"}
        ) {
            final Class[] types = new Class[]{java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class};
            final boolean[] canEdit = new boolean[]{false, false, false, true, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTableGameSessions);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)

        );

        jToolBar1.setRollover(true);

        jButtonNewGameSession.setText("New Game");
        jButtonNewGameSession.setFocusable(false);
        jButtonNewGameSession.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNewGameSession.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jToolBar1.add(jButtonNewGameSession);

        jMenu1.setText("File");

        jMenuItem_Exit.setText("Exit");
        jMenu1.add(jMenuItem_Exit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );


        jTableGameSessions.getTableHeader().setReorderingAllowed(false);
        jTableGameSessions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTableGameSessionsMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableGameSessionsMouseReleased(evt);
            }
        });

        jButtonNewGameSession.addActionListener((ActionEvent e) -> doNewGameSession());
        jMenuItem_Exit.addActionListener((ActionEvent e) -> exitApp());

        pack();
    }

    private void jTableGameSessionsMouseReleased(MouseEvent evt) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Join Session");

        item.addActionListener((ActionEvent e) -> {
            attachToGameSession();
            //String row = jTable_GameSessions.getModel().getValueAt(jTable_GameSessions.getSelectedRow(), jTable_GameSessions.getSelectedColumn()).toString();
            //JOptionPane.showMessageDialog(null, row);
        });

        menu.add(item);


        int r = jTableGameSessions.rowAtPoint(evt.getPoint());
        if (r >= 0 && r < jTableGameSessions.getRowCount()) {
            jTableGameSessions.setRowSelectionInterval(r, r);
        } else {
            jTableGameSessions.clearSelection();
        }

        int rowIndex = jTableGameSessions.getSelectedRow();
        if (rowIndex < 0) {
            return;
        }
        if (evt.isPopupTrigger() && evt.getComponent() instanceof JTable) {
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }

    private void jTableGameSessionsMousePressed(MouseEvent evt) {
        if (evt.getClickCount() == 2 && jTableGameSessions.getSelectedRow() != -1) {
            attachToGameSession();
        }
    }

    private void doNewGameSession() {
        try {
            sessionRI.newGameSession(sessionRI.getUsername());
            updateGameSessionsTable();
            subscribeNewRoom(sessionRI.getUsername());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    private void attachToGameSession() {
        try {
            sessionRI.attachToGameSession(sessionRI.getUsername(), "room");
            updateGameSessionsTable();
            subscribeNewRoom("room");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void subscribeNewRoom(String roomName) {

        MessagesExchange messagesExchange = new MessagesExchange(argsRMQ);
        messagesExchange.run();
        messagesExchange.consumeRMQ(roomName);
    }

}

