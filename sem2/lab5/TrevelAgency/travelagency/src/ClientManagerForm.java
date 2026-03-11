import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class ClientManagerForm extends JFrame {
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private String authHeader;

    public ClientManagerForm(String authHeader) {
        this.authHeader = authHeader;

        System.out.println("Заголовок авторизации: " + authHeader);

        setTitle("Туристическое агентство - Управление клиентами");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Создаем таблицу для клиентов турагентства
        String[] columns = {"№", "Фамилия", "Имя", "Отчество", "Email", "Телефон", "Дата регистрации", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование
            }
        };

        clientsTable = new JTable(tableModel);
        clientsTable.setRowHeight(30);
        clientsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        clientsTable.getTableHeader().setBackground(new Color(44, 62, 80)); // Темно-синий цвет
        clientsTable.getTableHeader().setForeground(new Color(236, 240, 241)); // Светлый текст
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientsTable.setGridColor(new Color(189, 195, 199));
        clientsTable.setShowGrid(true);

        // Настройка ширины колонок
        clientsTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // №
        clientsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Фамилия
        clientsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Имя
        clientsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Отчество
        clientsTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
        clientsTable.getColumnModel().getColumn(5).setPreferredWidth(130); // Телефон
        clientsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Дата регистрации
        clientsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Статус

        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Список клиентов туристического агентства"
        ));

        // Панель инструментов
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setBackground(new Color(236, 240, 241));

        JLabel searchLabel = new JLabel("Поиск:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Найти");
        searchButton.setBackground(new Color(41, 128, 185));
        searchButton.setForeground(Color.WHITE);

        toolPanel.add(searchLabel);
        toolPanel.add(searchField);
        toolPanel.add(searchButton);

        // Панель для кнопок управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton refreshButton = new JButton("Обновить");
        JButton addButton = new JButton("Добавить клиента");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton viewBookingsButton = new JButton("Бронирования");
        JButton exportButton = new JButton("Экспорт");
        JButton logoutButton = new JButton("Выйти");

        // Стили кнопок
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(241, 196, 15));
        editButton.setForeground(Color.BLACK);
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        viewBookingsButton.setBackground(new Color(155, 89, 182));
        viewBookingsButton.setForeground(Color.WHITE);
        exportButton.setBackground(new Color(149, 165, 166));
        exportButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setForeground(Color.WHITE);

        // Добавляем кнопки
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewBookingsButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(logoutButton);

        // Статистика
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(236, 240, 241));
        JLabel statsLabel = new JLabel("Всего клиентов: 0");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsLabel.setForeground(new Color(44, 62, 80));
        statsPanel.add(statsLabel);

        // Добавляем панели на форму
        add(toolPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.PAGE_END);

        // Обработчики событий
        refreshButton.addActionListener(e -> loadClients(statsLabel));
        searchButton.addActionListener(e -> searchClients(searchField.getText(), statsLabel));

        addButton.addActionListener(e -> {
            AddClientDialog dialog = new AddClientDialog(this, authHeader);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadClients(statsLabel);
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long clientId = (Long) tableModel.getValueAt(selectedRow, 8); // ID скрыт в последнем столбце
                EditClientDialog dialog = new EditClientDialog(this, authHeader, clientId);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadClients(statsLabel);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Выберите клиента для редактирования",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить выбранного клиента?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Long clientId = (Long) tableModel.getValueAt(selectedRow, 8);
                    deleteClient(clientId, statsLabel);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Выберите клиента для удаления",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        viewBookingsButton.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow >= 0) {
                Long clientId = (Long) tableModel.getValueAt(selectedRow, 8);
                String clientName = tableModel.getValueAt(selectedRow, 1) + " " +
                        tableModel.getValueAt(selectedRow, 2);
                ClientBookingsForm bookingsForm = new ClientBookingsForm(authHeader, clientId, clientName);
                bookingsForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Выберите клиента для просмотра бронирований",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        exportButton.addActionListener(e -> exportToExcel());

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        // Загружаем данные при запуске
        loadClients(statsLabel);
    }

    private void loadClients(JLabel statsLabel) {
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    tableModel.addRow(new Object[]{"", "Загрузка...", "", "", "", "", "", ""});
                });

                URL url = new URL("http://localhost:8080/clients/api");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + authHeader);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();
                System.out.println("Код ответа: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder jsonResponse = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        jsonResponse.append(inputLine);
                    }
                    in.close();

                    String json = jsonResponse.toString();

                    // Обновляем UI
                    SwingUtilities.invokeLater(() -> {
                        parseAndDisplayClients(json);
                        statsLabel.setText("Всего клиентов: " + tableModel.getRowCount());
                    });

                } else {
                    String errorMessage = "Ошибка сервера: " + responseCode;
                    if (responseCode == 401) {
                        errorMessage = "Ошибка авторизации. Проверьте логин и пароль.";
                    } else if (responseCode == 403) {
                        errorMessage = "Доступ запрещен. Недостаточно прав.";
                    }

                    SwingUtilities.invokeLater(() -> {
                        tableModel.setRowCount(0);
                        tableModel.addRow(new Object[]{"", errorMessage, "", "", "", "", "", ""});
                        JOptionPane.showMessageDialog(ClientManagerForm.this,
                                errorMessage,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    tableModel.addRow(new Object[]{"", "Ошибка подключения: " + e.getMessage(), "", "", "", "", "", ""});
                    JOptionPane.showMessageDialog(ClientManagerForm.this,
                            "Не удалось подключиться к серверу.\nПроверьте соединение и настройки.",
                            "Ошибка подключения",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void parseAndDisplayClients(String json) {
        tableModel.setRowCount(0);

        try {
            if (json == null || json.trim().isEmpty()) {
                tableModel.addRow(new Object[]{"", "Нет данных", "", "", "", "", "", ""});
                return;
            }

            // Используем JSON библиотеку для парсинга
            JSONArray clientsArray = new JSONArray(json);

            for (int i = 0; i < clientsArray.length(); i++) {
                JSONObject client = clientsArray.getJSONObject(i);

                // Извлекаем данные
                Long id = client.optLong("id", 0);
                String lastName = client.optString("lastName", "");
                String firstName = client.optString("firstName", "");
                String patronymic = client.optString("patronymic", "");
                String email = client.optString("email", "");
                String phone = client.optString("phone", "");

                // Дата регистрации (если есть)
                String createdAt = "";
                if (client.has("createdAt")) {
                    createdAt = client.getString("createdAt");
                    // Форматируем дату
                    if (createdAt.length() > 10) {
                        createdAt = createdAt.substring(0, 10);
                    }
                }

                // Статус клиента (на основе количества бронирований)
                String status = "Новый";
                if (client.has("bookingsCount")) {
                    int bookingsCount = client.getInt("bookingsCount");
                    if (bookingsCount > 5) {
                        status = "Постоянный";
                    } else if (bookingsCount > 0) {
                        status = "Активный";
                    }
                }

                // Добавляем строку в таблицу (ID в скрытом столбце)
                Object[] row = {
                        i + 1,
                        lastName,
                        firstName,
                        patronymic.isEmpty() ? "-" : patronymic,
                        email,
                        phone,
                        createdAt.isEmpty() ? "-" : createdAt,
                        status,
                        id  // Скрытый ID для операций
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"", "Нет клиентов в базе", "", "", "", "", "", ""});
            }

        } catch (Exception e) {
            e.printStackTrace();
            tableModel.addRow(new Object[]{"", "Ошибка парсинга данных", "", "", "", "", "", ""});
            tableModel.addRow(new Object[]{"", e.getMessage(), "", "", "", "", "", ""});
        }
    }

    private void searchClients(String searchText, JLabel statsLabel) {
        new Thread(() -> {
            try {
                String encodedSearch = java.net.URLEncoder.encode(searchText, "UTF-8");
                URL url = new URL("http://localhost:8080/clients/api/search?query=" + encodedSearch);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + authHeader);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder jsonResponse = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        jsonResponse.append(inputLine);
                    }
                    in.close();

                    String json = jsonResponse.toString();

                    SwingUtilities.invokeLater(() -> {
                        parseAndDisplayClients(json);
                        statsLabel.setText("Найдено клиентов: " + tableModel.getRowCount());
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка при поиске: " + e.getMessage(),
                            "Ошибка поиска",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void deleteClient(Long clientId, JLabel statsLabel) {
        new Thread(() -> {
            try {
                URL url = new URL("http://localhost:8080/clients/api/" + clientId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Basic " + authHeader);

                int responseCode = conn.getResponseCode();

                SwingUtilities.invokeLater(() -> {
                    if (responseCode == 200 || responseCode == 204) {
                        JOptionPane.showMessageDialog(this,
                                "Клиент успешно удален",
                                "Успех",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadClients(statsLabel);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Ошибка при удалении: " + responseCode,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Ошибка подключения: " + e.getMessage(),
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Экспорт в Excel");
        fileChooser.setSelectedFile(new File("клиенты_турагентства.xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // Здесь реализуйте экспорт в Excel
            // Можно использовать библиотеку Apache POI
            JOptionPane.showMessageDialog(this,
                    "Экспорт в файл: " + file.getAbsolutePath(),
                    "Экспорт",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Вспомогательные классы для диалогов
    class AddClientDialog extends JDialog {
        private boolean success = false;

        public AddClientDialog(JFrame parent, String authHeader) {
            super(parent, "Добавить нового клиента", true);
            setSize(400, 400);
            setLocationRelativeTo(parent);

            // Реализация формы добавления клиента
            // ...
        }

        public boolean isSuccess() { return success; }
    }

    class EditClientDialog extends JDialog {
        private boolean success = false;

        public EditClientDialog(JFrame parent, String authHeader, Long clientId) {
            super(parent, "Редактировать клиента", true);
            setSize(400, 400);
            setLocationRelativeTo(parent);

            // Реализация формы редактирования клиента
            // ...
        }

        public boolean isSuccess() { return success; }
    }

    // Тестовый метод для демонстрации
    public static void testWithHardcodedJson() {
        String testJson = "[{\"id\":1,\"lastName\":\"Иванов\",\"firstName\":\"Иван\",\"patronymic\":\"Иванович\"," +
                "\"email\":\"ivanov@mail.ru\",\"phone\":\"+79161234567\",\"createdAt\":\"2024-01-15\",\"bookingsCount\":3}," +
                "{\"id\":2,\"lastName\":\"Петрова\",\"firstName\":\"Мария\",\"patronymic\":\"Сергеевна\"," +
                "\"email\":\"petrova@gmail.com\",\"phone\":\"+79262345678\",\"createdAt\":\"2024-02-20\",\"bookingsCount\":7}," +
                "{\"id\":3,\"lastName\":\"Сидоров\",\"firstName\":\"Алексей\",\"patronymic\":\"Николаевич\"," +
                "\"email\":\"sidorov@yandex.ru\",\"phone\":\"+79373456789\",\"createdAt\":\"2024-03-10\",\"bookingsCount\":0}]";

        SwingUtilities.invokeLater(() -> {
            ClientManagerForm form = new ClientManagerForm("test");
            form.parseAndDisplayClients(testJson);
            form.setVisible(true);
        });
    }
}