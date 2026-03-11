import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Base64;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Туристическое агентство - Вход");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Панель с логотипом
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(52, 152, 219));
        JLabel logoLabel = new JLabel("ТУРИСТИЧЕСКОЕ АГЕНТСТВО", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        // Панель с полями ввода
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Логин (email):");
        usernameField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Пароль:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Войти");
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        // Размещение компонентов
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        inputPanel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(loginButton, gbc);

        // Панель с ссылками
        JPanel linkPanel = new JPanel();
        JLabel registerLink = new JLabel("<html><a href='#'>Регистрация</a></html>");
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setForeground(new Color(41, 128, 185));

        JLabel forgotPasswordLink = new JLabel("<html><a href='#'>Забыли пароль?</a></html>");
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.setForeground(new Color(41, 128, 185));

        linkPanel.add(registerLink);
        linkPanel.add(new JLabel(" | "));
        linkPanel.add(forgotPasswordLink);

        // Добавление компонентов на форму
        add(logoPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(linkPanel, BorderLayout.SOUTH);

        // Обработчик кнопки входа
        loginButton.addActionListener(e -> performLogin());

        // Enter для входа
        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Введите логин и пароль",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаем Basic Auth заголовок
        String authString = username + ":" + password;
        String authHeader = Base64.getEncoder().encodeToString(authString.getBytes());

        // Проверяем подключение
        if (testConnection(authHeader)) {
            dispose(); // Закрываем окно логина
            ClientManagerForm mainForm = new ClientManagerForm(authHeader);
            mainForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Неверный логин или пароль",
                    "Ошибка входа",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean testConnection(String authHeader) {
        try {
            URL url = new URL("http://localhost:8080/clients/api/test");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + authHeader);

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }
}