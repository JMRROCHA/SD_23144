package edu.ufp.inf.sd.project.frogger.resources.forms;

import javax.swing.*;

public class Login extends JFrame {
    private JPanel loginPanel;
    private JButton registerButton;
    private JButton loginButton;
    private JTextField textField1;
    private JPasswordField passwordField1;

    public Login() {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(loginPanel);

        this.pack();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //Log.write(LoginForm.class.getName(), ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }


}
