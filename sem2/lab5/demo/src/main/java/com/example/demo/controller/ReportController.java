package com.example.demo.controller;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Client;
import com.example.demo.entity.Tour;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public String reportsPage(Model model) {
        model.addAttribute("successMessage", "Выберите категорию и период для формирования отчета");
        return "reports";
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateReport(
            @RequestParam String category,
            @RequestParam String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        // Заголовок отчета
        writer.println("=".repeat(60));
        writer.println("ОТЧЕТ " + category.toUpperCase());
        writer.println("Период: " + getPeriodName(period, startDate, endDate));
        writer.println("Дата формирования: " + LocalDate.now().format(formatter));
        writer.println("=".repeat(60));
        writer.println();

        switch (category) {
            case "clients":
                generateClientsReport(writer, period, startDate, endDate);
                break;
            case "tours":
                generateToursReport(writer, period, startDate, endDate);
                break;
            case "bookings":
                generateBookingsReport(writer, period, startDate, endDate);
                break;
            case "guides":
                generateGuidesReport(writer);
                break;
            case "financial":
                generateFinancialReport(writer, period, startDate, endDate);
                break;
        }

        writer.println();
        writer.println("=".repeat(60));
        writer.println("Отчет сформирован системой TravelDream");
        writer.println("=".repeat(60));
        writer.flush();

        String filename = "report_" + category + "_" + LocalDate.now() + ".txt";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(new ByteArrayResource(baos.toByteArray()));
    }

    private void generateClientsReport(PrintWriter writer, String period, String startDate, String endDate) {
        List<Client> clients = clientRepository.findAll();
        writer.println("КЛИЕНТЫ:");
        writer.println("-".repeat(60));
        writer.printf("%-5s | %-20s | %-20s | %-15s%n", "ID", "ФИО", "Email", "Телефон");
        writer.println("-".repeat(60));

        for (Client client : clients) {
            String fullName = client.getLastName() + " " + client.getFirstName() +
                    (client.getPatronymic() != null ? " " + client.getPatronymic() : "");
            writer.printf("%-5d | %-20s | %-20s | %-15s%n",
                    client.getId(),
                    fullName.substring(0, Math.min(fullName.length(), 20)),
                    client.getEmail() != null ? client.getEmail().substring(0, Math.min(client.getEmail().length(), 20)) : "",
                    client.getPhone());
        }
        writer.println("-".repeat(60));
        writer.println("Всего клиентов: " + clients.size());
    }

    private void generateToursReport(PrintWriter writer, String period, String startDate, String endDate) {
        List<Tour> tours = tourRepository.findAll();
        writer.println("ТУРЫ:");
        writer.println("-".repeat(80));
        writer.printf("%-5s | %-30s | %-15s | %-10s | %-10s%n",
                "ID", "Название", "Страна, Город", "Дата", "Мест");
        writer.println("-".repeat(80));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Tour tour : tours) {
            String dates = tour.getStartDate().format(formatter) + " - " + tour.getEndDate().format(formatter);
            String location = tour.getCountry() + ", " + tour.getCity();
            writer.printf("%-5d | %-30s | %-15s | %-10s | %-10d%n",
                    tour.getId(),
                    tour.getTourName().substring(0, Math.min(tour.getTourName().length(), 30)),
                    location.substring(0, Math.min(location.length(), 15)),
                    dates,
                    tour.getAvailableSeats());
        }
        writer.println("-".repeat(80));
        writer.println("Всего туров: " + tours.size());
    }

    private void generateBookingsReport(PrintWriter writer, String period, String startDate, String endDate) {
        List<Booking> bookings = bookingRepository.findAll();
        writer.println("БРОНИРОВАНИЯ:");
        writer.println("-".repeat(90));
        writer.printf("%-5s | %-15s | %-25s | %-10s | %-12s | %-10s%n",
                "ID", "Клиент", "Тур", "Участники", "Статус", "Сумма");
        writer.println("-".repeat(90));

        for (Booking booking : bookings) {
            String clientName = booking.getClient().getLastName() + " " + booking.getClient().getFirstName();
            String tourName = booking.getTour().getTourName();
            writer.printf("%-5d | %-15s | %-25s | %-10d | %-12s | %-10.2f%n",
                    booking.getId(),
                    clientName.substring(0, Math.min(clientName.length(), 15)),
                    tourName.substring(0, Math.min(tourName.length(), 25)),
                    booking.getParticipantsCount(),
                    booking.getStatus(),
                    booking.getTotalPrice());
        }
        writer.println("-".repeat(90));
        writer.println("Всего бронирований: " + bookings.size());
    }

    private void generateGuidesReport(PrintWriter writer) {
        writer.println("ОТЧЕТ ПО ГИДАМ:");
        writer.println("-".repeat(60));
        writer.println("В данной версии отчет по гидам не реализован");
        writer.println("-".repeat(60));
    }

    private void generateFinancialReport(PrintWriter writer, String period, String startDate, String endDate) {
        List<Booking> bookings = bookingRepository.findAll();
        double total = bookings.stream().mapToDouble(b -> b.getTotalPrice().doubleValue()).sum();

        writer.println("ФИНАНСОВЫЙ ОТЧЕТ:");
        writer.println("-".repeat(60));
        writer.println("Общая выручка: " + String.format("%.2f", total) + " руб.");
        writer.println("Количество бронирований: " + bookings.size());
        writer.println("Средний чек: " + String.format("%.2f", total / bookings.size()) + " руб.");
    }

    private String getPeriodName(String period, String startDate, String endDate) {
        switch (period) {
            case "all": return "За всё время";
            case "year": return "За текущий год";
            case "month": return "За текущий месяц";
            case "week": return "За текущую неделю";
            case "today": return "За сегодня";
            case "custom":
                if (startDate != null && endDate != null) {
                    return "с " + startDate + " по " + endDate;
                }
                return "Произвольный период";
            default: return "Неизвестный период";
        }
    }
}
