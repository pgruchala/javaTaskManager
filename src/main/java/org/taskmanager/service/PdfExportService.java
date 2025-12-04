package org.taskmanager.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import org.taskmanager.dto.StatsDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfExportService {
    public byte[] exportStatisticsToPdf(StatsDTO stats, String username) throws IOException {
        ByteArrayOutputStream bytearray = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(bytearray);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);


        Paragraph title = new Paragraph("Statystyki Zadań")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        document.add(new Paragraph("Użytkownik: " + username)
                .setFontSize(12));
        document.add(new Paragraph("Data wygenerowania: " + dateTime)
                .setFontSize(12));
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell("Metryka");
        table.addHeaderCell("Wartość");

        table.addCell("Wszystkie zadania");
        table.addCell(String.valueOf(stats.getTotalTasks()));

        table.addCell("Do zrobienia (TODO)");
        table.addCell(String.valueOf(stats.getTodoTasks()));

        table.addCell("W trakcie (IN_PROGRESS)");
        table.addCell(String.valueOf(stats.getInProgressTasks()));

        table.addCell("Ukończone (DONE)");
        table.addCell(String.valueOf(stats.getDoneTasks()));

        table.addCell("Procent ukończenia");
        table.addCell(String.format("%.2f%%", stats.getCompletionsPercentage()));

        document.add(table);

        document.close();

        return bytearray.toByteArray();
    }
}
