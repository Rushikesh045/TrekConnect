package com.trekconnect.core.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.trekconnect.core.entity.Booking;
import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.Trek;
import com.trekconnect.core.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Service for generating downloadable, branded PDF booking receipts.
 */
@Service
public class BookingReceiptPdfService {

    private static final Logger logger = LoggerFactory.getLogger(BookingReceiptPdfService.class);

    private static final Color PRIMARY_COLOR = new Color(27, 54, 93);    // Navy #1B365D
    private static final Color ACCENT_COLOR = new Color(15, 118, 110);   // Emerald #0F766E
    private static final Color BG_LIGHT = new Color(248, 250, 252);     // Slate 50
    private static final Color BORDER_COLOR = new Color(203, 213, 225); // Slate 300

    public byte[] generateReceiptPdf(Booking booking) {
        logger.info("Generating PDF booking receipt for Booking ID: {}", booking.getId());

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            // Document Header Banner
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, PRIMARY_COLOR);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, ACCENT_COLOR);
            Font headerLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.DARK_GRAY);
            Font headerValueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font tableHdrFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
            Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

            Paragraph title = new Paragraph("TREKCONNECT", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            document.add(title);

            Paragraph subtitle = new Paragraph("OFFICIAL EXPEDITION BOOKING RECEIPT & TAX INVOICE", subtitleFont);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // Receipt Metadata Table
            PdfPTable metaTable = new PdfPTable(4);
            metaTable.setWidthPercentage(100);
            metaTable.setWidths(new float[]{25, 25, 25, 25});

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            String bookingDate = booking.getCreatedAt() != null ? booking.getCreatedAt().format(dtf) : "N/A";

            addMetaCell(metaTable, "Receipt No:", "TC-REC-" + booking.getId().substring(0, Math.min(8, booking.getId().length())).toUpperCase(), headerLabelFont, headerValueFont);
            addMetaCell(metaTable, "Booking Status:", booking.getStatus() != null ? booking.getStatus() : "CONFIRMED", headerLabelFont, headerValueFont);
            addMetaCell(metaTable, "Issue Date:", bookingDate, headerLabelFont, headerValueFont);
            addMetaCell(metaTable, "Payment Mode:", "Razorpay (Online Verified)", headerLabelFont, headerValueFont);

            metaTable.setSpacingAfter(15);
            document.add(metaTable);

            // Adventurer & Expedition Details Section
            UserProfile user = booking.getUser();
            Event event = booking.getEvent();
            Trek trek = event != null ? event.getTrek() : null;

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{50, 50});

            // Customer Info Box
            PdfPCell userCell = new PdfPCell();
            userCell.setBackgroundColor(BG_LIGHT);
            userCell.setBorderColor(BORDER_COLOR);
            userCell.setPadding(10);
            Paragraph userHeader = new Paragraph("ADVENTURER DETAILS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR));
            userCell.addElement(userHeader);
            userCell.addElement(new Paragraph("Name: " + (user != null ? user.getName() : "Valued Trekker"), headerValueFont));
            userCell.addElement(new Paragraph("Email: " + (user != null ? user.getEmail() : "N/A"), headerValueFont));
            userCell.addElement(new Paragraph("User ID: " + (user != null ? user.getUserId() : "N/A"), headerValueFont));
            infoTable.addCell(userCell);

            // Expedition Info Box
            PdfPCell eventCell = new PdfPCell();
            eventCell.setBackgroundColor(BG_LIGHT);
            eventCell.setBorderColor(BORDER_COLOR);
            eventCell.setPadding(10);
            Paragraph eventHeader = new Paragraph("EXPEDITION SCHEDULE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR));
            eventCell.addElement(eventHeader);
            eventCell.addElement(new Paragraph("Trek: " + (trek != null ? trek.getName() : "Expedition Trek"), headerValueFont));
            eventCell.addElement(new Paragraph("Region: " + (trek != null ? trek.getRegion() : "N/A") + " | Difficulty: " + (trek != null ? trek.getDifficulty() : "N/A"), headerValueFont));
            String dateStr = event != null && event.getEventDate() != null ? event.getEventDate().toString() : "Scheduled Batch";
            eventCell.addElement(new Paragraph("Event Date: " + dateStr, headerValueFont));
            infoTable.addCell(eventCell);

            infoTable.setSpacingAfter(20);
            document.add(infoTable);

            // Itemized Financial Breakdown Table
            PdfPTable itemsTable = new PdfPTable(4);
            itemsTable.setWidthPercentage(100);
            itemsTable.setWidths(new float[]{45, 20, 15, 20});

            addTableHeader(itemsTable, "Description", tableHdrFont);
            addTableHeader(itemsTable, "Unit Price", tableHdrFont);
            addTableHeader(itemsTable, "Quantity", tableHdrFont);
            addTableHeader(itemsTable, "Subtotal", tableHdrFont);

            String trekDesc = (trek != null ? trek.getName() : "Trek Expedition") + " - Seat Reservation";
            String unitPrice = event != null && event.getPrice() != null ? "₹ " + event.getPrice() : "N/A";
            int seats = booking.getNumSeats() != null ? booking.getNumSeats() : 1;
            String totalAmount = booking.getTotalAmount() != null ? "₹ " + booking.getTotalAmount() : "N/A";

            addTableRow(itemsTable, trekDesc, tableBodyFont, Element.ALIGN_LEFT);
            addTableRow(itemsTable, unitPrice, tableBodyFont, Element.ALIGN_RIGHT);
            addTableRow(itemsTable, String.valueOf(seats), tableBodyFont, Element.ALIGN_CENTER);
            addTableRow(itemsTable, totalAmount, tableBodyFont, Element.ALIGN_RIGHT);

            // Total Summary Row
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL AMOUNT PAID:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR)));
            totalLabelCell.setColspan(3);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabelCell.setPadding(8);
            totalLabelCell.setBackgroundColor(BG_LIGHT);
            totalLabelCell.setBorderColor(BORDER_COLOR);
            itemsTable.addCell(totalLabelCell);

            PdfPCell totalValCell = new PdfPCell(new Phrase(totalAmount, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, ACCENT_COLOR)));
            totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValCell.setPadding(8);
            totalValCell.setBackgroundColor(BG_LIGHT);
            totalValCell.setBorderColor(BORDER_COLOR);
            itemsTable.addCell(totalValCell);

            itemsTable.setSpacingAfter(25);
            document.add(itemsTable);

            // Terms & Conditions / Policy Footer
            Paragraph footerTitle = new Paragraph("IMPORTANT INSTRUCTIONS & CANCELLATION POLICY", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, PRIMARY_COLOR));
            document.add(footerTitle);

            String terms = "1. Please carry a digital or printed copy of this receipt along with a valid government photo ID on the expedition date.\n"
                    + "2. Cancellations made 7+ days prior to start date are eligible for full refund minus processing fees.\n"
                    + "3. The expedition guide reserve the right to modify routes based on weather and terrain safety conditions.\n"
                    + "4. For questions or support, contact support@trekconnect.com or reach out via your user dashboard.";

            Paragraph termsPara = new Paragraph(terms, FontFactory.getFont(FontFactory.HELVETICA, 7.5f, Color.DARK_GRAY));
            termsPara.setSpacingAfter(15);
            document.add(termsPara);

            Paragraph copyright = new Paragraph("TrekConnect Marketplace Platform • Electronic System Generated Receipt", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7.5f, Color.GRAY));
            copyright.setAlignment(Element.ALIGN_CENTER);
            document.add(copyright);

            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            logger.error("Failed to generate PDF receipt for booking: {}", booking.getId(), ex);
            throw new RuntimeException("Error generating booking receipt PDF: " + ex.getMessage(), ex);
        }
    }

    private void addMetaCell(PdfPTable table, String label, String value, Font labelFont, Font valFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valFont));
        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, String header, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }
}
