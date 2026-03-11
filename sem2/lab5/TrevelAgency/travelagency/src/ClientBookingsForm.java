import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientBookingsForm extends JFrame {
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private String authHeader;
    private Long clientId;
    private String clientName;

    public ClientBookingsForm(String authHeader, Long clientId, String clientName) {
        this.authHeader = authHeader;
        this.clientId = clientId;
        this.clientName = clientName;

        setTitle("Бронирования клиента: " + clientName);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Таблица бронирований
        String[] columns = {"№", "Тур", "Страна", "Город", "Даты", "Участники", "Стоимость", "Статус"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        JButton addBookingButton = new JButton("Новое бронирование");
        JButton cancelBookingButton = new JButton("Отменить");
        JButton refreshButton = new JButton("Обновить");

        buttonPanel.add(addBookingButton);
        buttonPanel.add(cancelBookingButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Загрузка данных
        loadBookings();
    }

    private void loadBookings() {
        // Загрузка бронирований клиента
        // ...
    }
}