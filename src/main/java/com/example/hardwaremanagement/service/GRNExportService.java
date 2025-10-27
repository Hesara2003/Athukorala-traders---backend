package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.GoodsReceiptNote;
import com.example.hardwaremanagement.model.GRNItem;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GRNExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(240, 240, 240);

    /**
     * Generate PDF for a single GRN
     */
    public byte[] generateGRNPdf(GoodsReceiptNote grn) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add company header
        addCompanyHeader(document);

        // Add GRN title
        Paragraph title = new Paragraph("GOODS RECEIPT NOTE")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20)
                .setMarginBottom(20);
        document.add(title);

        // Add GRN details
        addGRNDetails(document, grn);

        // Add items table
        addItemsTable(document, grn);

        // Add summary
        addSummary(document, grn);

        // Add footer
        addFooter(document);

        document.close();
        return baos.toByteArray();
    }

    /**
     * Generate Excel for multiple GRNs
     */
    public byte[] generateGRNExcel(List<GoodsReceiptNote> grns) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("GRN Report");

        // Create header style
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "GRN ID", "PO ID", "Supplier ID", "Received By", 
                "Received Date", "Status", "Total Items", "Notes"
        };

        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data rows
        int rowNum = 1;
        for (GoodsReceiptNote grn : grns) {
            Row row = sheet.createRow(rowNum++);

            org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
            cell0.setCellValue(grn.getId());
            cell0.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
            cell1.setCellValue(grn.getPurchaseOrderId());
            cell1.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
            cell2.setCellValue(grn.getSupplierId());
            cell2.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
            cell3.setCellValue(grn.getReceivedBy());
            cell3.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
            cell4.setCellValue(grn.getReceivedDate().format(DATE_FORMATTER));
            cell4.setCellStyle(dateStyle);

            org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
            cell5.setCellValue(grn.getStatus());
            cell5.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
            cell6.setCellValue(grn.getItems().size());
            cell6.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell7 = row.createCell(7);
            cell7.setCellValue(grn.getNotes() != null ? grn.getNotes() : "");
            cell7.setCellStyle(dataStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    /**
     * Generate detailed Excel with items for a single GRN
     */
    public byte[] generateDetailedGRNExcel(GoodsReceiptNote grn) throws Exception {
        Workbook workbook = new XSSFWorkbook();

        // Sheet 1: GRN Details
        Sheet detailSheet = workbook.createSheet("GRN Details");
        addGRNDetailsToExcel(detailSheet, grn, workbook);

        // Sheet 2: Items
        Sheet itemsSheet = workbook.createSheet("Items");
        addItemsToExcel(itemsSheet, grn, workbook);

        // Write to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    // ==================== Private Helper Methods ====================

    private void addCompanyHeader(Document document) {
        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Company name and details
        Cell leftCell = new Cell()
                .add(new Paragraph("Athukorala Traders").setFontSize(18).setBold())
                .add(new Paragraph("Hardware Management System").setFontSize(10))
                .add(new Paragraph("123 Main Street, City, Country").setFontSize(9))
                .add(new Paragraph("Tel: +1 234 567 890 | Email: info@athukorala.com").setFontSize(9))
                .setBorder(Border.NO_BORDER);

        // Date
        Cell rightCell = new Cell()
                .add(new Paragraph("Date: " + java.time.LocalDateTime.now().format(DATE_FORMATTER))
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.TOP);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addGRNDetails(Document document, GoodsReceiptNote grn) {
        Table table = new Table(new float[]{1, 2, 1, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(20);

        // Header
        addDetailRow(table, "GRN ID:", grn.getId(), "Status:", grn.getStatus(), true);
        addDetailRow(table, "PO ID:", grn.getPurchaseOrderId(), "Supplier ID:", grn.getSupplierId(), false);
        addDetailRow(table, "Received By:", grn.getReceivedBy(), "Received Date:", 
                     grn.getReceivedDate().format(DATE_FORMATTER), false);

        if (grn.getNotes() != null && !grn.getNotes().isEmpty()) {
            Cell labelCell = new Cell(1, 1)
                    .add(new Paragraph("Notes:").setBold())
                    .setBackgroundColor(LIGHT_GRAY);
            Cell valueCell = new Cell(1, 3)
                    .add(new Paragraph(grn.getNotes()));
            table.addCell(labelCell);
            table.addCell(valueCell);
        }

        document.add(table);
    }

    private void addDetailRow(Table table, String label1, String value1, 
                              String label2, String value2, boolean isFirst) {
        DeviceRgb bgColor = isFirst ? LIGHT_GRAY : new DeviceRgb(255, 255, 255);

        table.addCell(new Cell().add(new Paragraph(label1).setBold())
                .setBackgroundColor(bgColor));
        table.addCell(new Cell().add(new Paragraph(value1)));
        table.addCell(new Cell().add(new Paragraph(label2).setBold())
                .setBackgroundColor(bgColor));
        table.addCell(new Cell().add(new Paragraph(value2)));
    }

    private void addItemsTable(Document document, GoodsReceiptNote grn) {
        document.add(new Paragraph("Items Received").setFontSize(14).setBold().setMarginBottom(10));

        Table table = new Table(new float[]{1, 3, 2, 2, 2, 2, 3});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header row
        String[] headers = {"#", "Product Name", "Product ID", "Ordered", "Received", "Condition", "Remarks"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(HEADER_COLOR)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }

        // Data rows
        int itemNum = 1;
        for (GRNItem item : grn.getItems()) {
            DeviceRgb bgColor = (itemNum % 2 == 0) ? LIGHT_GRAY : new DeviceRgb(255, 255, 255);

            table.addCell(new Cell().add(new Paragraph(String.valueOf(itemNum++)))
                    .setBackgroundColor(bgColor).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(item.getProductName() != null ? item.getProductName() : "N/A"))
                    .setBackgroundColor(bgColor));
            table.addCell(new Cell().add(new Paragraph(item.getProductId()))
                    .setBackgroundColor(bgColor));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getOrderedQuantity())))
                    .setBackgroundColor(bgColor).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getReceivedQuantity())))
                    .setBackgroundColor(bgColor).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(item.getCondition()))
                    .setBackgroundColor(bgColor).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(item.getRemarks() != null ? item.getRemarks() : ""))
                    .setBackgroundColor(bgColor));
        }

        document.add(table);
    }

    private void addSummary(Document document, GoodsReceiptNote grn) {
        document.add(new Paragraph("\n"));
        
        int totalOrdered = grn.getItems().stream()
                .mapToInt(GRNItem::getOrderedQuantity)
                .sum();
        int totalReceived = grn.getItems().stream()
                .mapToInt(GRNItem::getReceivedQuantity)
                .sum();

        Table summaryTable = new Table(2);
        summaryTable.setWidth(UnitValue.createPercentValue(40));
        summaryTable.setMarginLeft(60f);

        summaryTable.addCell(new Cell().add(new Paragraph("Total Items:").setBold())
                .setBackgroundColor(LIGHT_GRAY));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(grn.getItems().size()))));

        summaryTable.addCell(new Cell().add(new Paragraph("Total Ordered:").setBold())
                .setBackgroundColor(LIGHT_GRAY));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalOrdered))));

        summaryTable.addCell(new Cell().add(new Paragraph("Total Received:").setBold())
                .setBackgroundColor(LIGHT_GRAY));
        summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalReceived))));

        document.add(summaryTable);
    }

    private void addFooter(Document document) {
        document.add(new Paragraph("\n\n"));
        
        Table signatureTable = new Table(3);
        signatureTable.setWidth(UnitValue.createPercentValue(100));

        Cell cell1 = new Cell()
                .add(new Paragraph("_________________\nReceived By"))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);

        Cell cell2 = new Cell()
                .add(new Paragraph("_________________\nVerified By"))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);

        Cell cell3 = new Cell()
                .add(new Paragraph("_________________\nApproved By"))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER);

        signatureTable.addCell(cell1);
        signatureTable.addCell(cell2);
        signatureTable.addCell(cell3);

        document.add(signatureTable);

        // Add page number
        document.add(new Paragraph("\nGenerated by Athukorala Hardware Management System")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20));
    }

    // ==================== Excel Helper Methods ====================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("mmm dd, yyyy hh:mm"));
        return style;
    }

    private void addGRNDetailsToExcel(Sheet sheet, GoodsReceiptNote grn, Workbook workbook) {
        CellStyle labelStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        labelStyle.setFont(font);
        labelStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        labelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("GOODS RECEIPT NOTE");
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        rowNum++; // Empty row

        // Details
        addExcelDetailRow(sheet, rowNum++, "GRN ID:", grn.getId(), labelStyle);
        addExcelDetailRow(sheet, rowNum++, "Purchase Order ID:", grn.getPurchaseOrderId(), labelStyle);
        addExcelDetailRow(sheet, rowNum++, "Supplier ID:", grn.getSupplierId(), labelStyle);
        addExcelDetailRow(sheet, rowNum++, "Received By:", grn.getReceivedBy(), labelStyle);
        addExcelDetailRow(sheet, rowNum++, "Received Date:", grn.getReceivedDate().format(DATE_FORMATTER), labelStyle);
        addExcelDetailRow(sheet, rowNum++, "Status:", grn.getStatus(), labelStyle);
        if (grn.getNotes() != null && !grn.getNotes().isEmpty()) {
            addExcelDetailRow(sheet, rowNum++, "Notes:", grn.getNotes(), labelStyle);
        }

        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void addExcelDetailRow(Sheet sheet, int rowNum, String label, String value, CellStyle labelStyle) {
        Row row = sheet.createRow(rowNum);
        org.apache.poi.ss.usermodel.Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        org.apache.poi.ss.usermodel.Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
    }

    private void addItemsToExcel(Sheet sheet, GoodsReceiptNote grn, Workbook workbook) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // Header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"#", "Product ID", "Product Name", "Ordered Qty", "Received Qty", "Condition", "Remarks"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowNum = 1;
        int itemNum = 1;
        for (GRNItem item : grn.getItems()) {
            Row row = sheet.createRow(rowNum++);

            org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
            cell0.setCellValue(itemNum++);
            cell0.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
            cell1.setCellValue(item.getProductId());
            cell1.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
            cell2.setCellValue(item.getProductName() != null ? item.getProductName() : "N/A");
            cell2.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
            cell3.setCellValue(item.getOrderedQuantity());
            cell3.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
            cell4.setCellValue(item.getReceivedQuantity());
            cell4.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
            cell5.setCellValue(item.getCondition());
            cell5.setCellStyle(dataStyle);

            org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
            cell6.setCellValue(item.getRemarks() != null ? item.getRemarks() : "");
            cell6.setCellStyle(dataStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
