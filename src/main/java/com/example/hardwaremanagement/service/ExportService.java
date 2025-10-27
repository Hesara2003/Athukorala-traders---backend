package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Export sales report to CSV format
     */
    public String exportSalesReportToCsv(SalesReportDTO salesReport) throws IOException {
        StringWriter stringWriter = new StringWriter();
        
        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT)) {
            // Write header information
            csvPrinter.printRecord("Sales Report");
            csvPrinter.printRecord("Generated:", salesReport.getReportDate().format(DATETIME_FORMATTER));
            csvPrinter.printRecord();
            
            // Write summary information
            csvPrinter.printRecord("SUMMARY");
            csvPrinter.printRecord("Total Revenue", salesReport.getTotalRevenue());
            csvPrinter.printRecord("Total Sales", salesReport.getTotalSales());
            csvPrinter.printRecord("Total Orders", salesReport.getTotalOrders());
            csvPrinter.printRecord("Total Products Sold", salesReport.getTotalProducts());
            csvPrinter.printRecord();
            
            // Write top selling products
            csvPrinter.printRecord("TOP SELLING PRODUCTS");
            csvPrinter.printRecord("Product ID", "Product Name", "Category", "Quantity Sold", "Total Revenue", "Unit Price");
            
            for (ProductSalesDTO product : salesReport.getTopSellingProducts()) {
                csvPrinter.printRecord(
                    product.getProductId(),
                    product.getProductName(),
                    product.getCategory(),
                    product.getQuantitySold(),
                    product.getTotalRevenue(),
                    product.getUnitPrice()
                );
            }
            
            csvPrinter.printRecord();
            
            // Write daily sales breakdown
            csvPrinter.printRecord("DAILY SALES BREAKDOWN");
            csvPrinter.printRecord("Date", "Revenue", "Orders", "Items Sold");
            
            for (DailySalesDTO dailySale : salesReport.getDailySales()) {
                csvPrinter.printRecord(
                    dailySale.getDate().format(DATE_FORMATTER),
                    dailySale.getTotalRevenue(),
                    dailySale.getTotalOrders(),
                    dailySale.getTotalItems()
                );
            }
        }
        
        return stringWriter.toString();
    }

    /**
     * Export inventory report to CSV format
     */
    public String exportInventoryReportToCsv(InventoryReportDTO inventoryReport) throws IOException {
        StringWriter stringWriter = new StringWriter();
        
        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT)) {
            // Write header information
            csvPrinter.printRecord("Inventory Report");
            csvPrinter.printRecord("Generated:", inventoryReport.getReportDate().format(DATETIME_FORMATTER));
            csvPrinter.printRecord();
            
            // Write summary information
            csvPrinter.printRecord("SUMMARY");
            csvPrinter.printRecord("Total Products", inventoryReport.getTotalProducts());
            csvPrinter.printRecord("Total Stock Value", inventoryReport.getTotalStockValue());
            csvPrinter.printRecord("Low Stock Count", inventoryReport.getLowStockCount());
            csvPrinter.printRecord("Out of Stock Count", inventoryReport.getOutOfStockCount());
            csvPrinter.printRecord();
            
            // Write all inventory items
            csvPrinter.printRecord("INVENTORY ITEMS");
            csvPrinter.printRecord("Product ID", "Product Name", "SKU", "Category", "Brand", "Stock Quantity", "Unit Price", "Stock Value", "Status");
            
            for (InventoryItemDTO item : inventoryReport.getInventoryItems()) {
                String status = item.isOutOfStock() ? "OUT OF STOCK" : (item.isLowStock() ? "LOW STOCK" : "IN STOCK");
                csvPrinter.printRecord(
                    item.getProductId(),
                    item.getProductName(),
                    item.getSku(),
                    item.getCategory(),
                    item.getBrand(),
                    item.getCurrentStock(),
                    item.getUnitPrice(),
                    item.getStockValue(),
                    status
                );
            }
            
            csvPrinter.printRecord();
            
            // Write low stock items
            if (!inventoryReport.getLowStockItems().isEmpty()) {
                csvPrinter.printRecord("LOW STOCK ITEMS");
                csvPrinter.printRecord("Product ID", "Product Name", "SKU", "Stock Quantity", "Unit Price");
                
                for (InventoryItemDTO item : inventoryReport.getLowStockItems()) {
                    csvPrinter.printRecord(
                        item.getProductId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getCurrentStock(),
                        item.getUnitPrice()
                    );
                }
            }
            
            csvPrinter.printRecord();
            
            // Write out of stock items
            if (!inventoryReport.getOutOfStockItems().isEmpty()) {
                csvPrinter.printRecord("OUT OF STOCK ITEMS");
                csvPrinter.printRecord("Product ID", "Product Name", "SKU", "Unit Price");
                
                for (InventoryItemDTO item : inventoryReport.getOutOfStockItems()) {
                    csvPrinter.printRecord(
                        item.getProductId(),
                        item.getProductName(),
                        item.getSku(),
                        item.getUnitPrice()
                    );
                }
            }
        }
        
        return stringWriter.toString();
    }

    /**
     * Export sales report to PDF format
     */
    public byte[] exportSalesReportToPdf(SalesReportDTO salesReport) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            // Title
            document.add(new Paragraph("Sales Report")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Generated: " + salesReport.getReportDate().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("\n"));
            
            // Summary section
            document.add(new Paragraph("Summary").setFontSize(14).setBold());
            
            Table summaryTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            summaryTable.addCell(new Cell().add(new Paragraph("Total Revenue")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", salesReport.getTotalRevenue()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Sales")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", salesReport.getTotalSales()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Orders")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(salesReport.getTotalOrders()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Products Sold")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(salesReport.getTotalProducts()))));
            
            document.add(summaryTable);
            document.add(new Paragraph("\n"));
            
            // Top selling products
            document.add(new Paragraph("Top Selling Products").setFontSize(14).setBold());
            
            if (!salesReport.getTopSellingProducts().isEmpty()) {
                Table productsTable = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();
                
                // Header
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Product ID")).setBold());
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Product Name")).setBold());
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Category")).setBold());
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Qty Sold")).setBold());
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Revenue")).setBold());
                productsTable.addHeaderCell(new Cell().add(new Paragraph("Unit Price")).setBold());
                
                // Data rows
                for (ProductSalesDTO product : salesReport.getTopSellingProducts()) {
                    productsTable.addCell(new Cell().add(new Paragraph(product.getProductId())));
                    productsTable.addCell(new Cell().add(new Paragraph(product.getProductName())));
                    productsTable.addCell(new Cell().add(new Paragraph(product.getCategory())));
                    productsTable.addCell(new Cell().add(new Paragraph(String.valueOf(product.getQuantitySold()))));
                    productsTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", product.getTotalRevenue()))));
                    productsTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", product.getUnitPrice()))));
                }
                
                document.add(productsTable);
            }
            
            document.add(new Paragraph("\n"));
            
            // Daily sales breakdown
            document.add(new Paragraph("Daily Sales Breakdown").setFontSize(14).setBold());
            
            if (!salesReport.getDailySales().isEmpty()) {
                Table dailyTable = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                
                // Header
                dailyTable.addHeaderCell(new Cell().add(new Paragraph("Date")).setBold());
                dailyTable.addHeaderCell(new Cell().add(new Paragraph("Revenue")).setBold());
                dailyTable.addHeaderCell(new Cell().add(new Paragraph("Orders")).setBold());
                dailyTable.addHeaderCell(new Cell().add(new Paragraph("Items Sold")).setBold());
                
                // Data rows
                for (DailySalesDTO dailySale : salesReport.getDailySales()) {
                    dailyTable.addCell(new Cell().add(new Paragraph(dailySale.getDate().format(DATE_FORMATTER))));
                    dailyTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", dailySale.getTotalRevenue()))));
                    dailyTable.addCell(new Cell().add(new Paragraph(String.valueOf(dailySale.getTotalOrders()))));
                    dailyTable.addCell(new Cell().add(new Paragraph(String.valueOf(dailySale.getTotalItems()))));
                }
                
                document.add(dailyTable);
            }
        }
        
        return baos.toByteArray();
    }

    /**
     * Export inventory report to PDF format
     */
    public byte[] exportInventoryReportToPdf(InventoryReportDTO inventoryReport) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            // Title
            document.add(new Paragraph("Inventory Report")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Generated: " + inventoryReport.getReportDate().format(DATETIME_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("\n"));
            
            // Summary section
            document.add(new Paragraph("Summary").setFontSize(14).setBold());
            
            Table summaryTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            summaryTable.addCell(new Cell().add(new Paragraph("Total Products")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(inventoryReport.getTotalProducts()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Total Stock Value")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.format("$%d", inventoryReport.getTotalStockValue()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Low Stock Count")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(inventoryReport.getLowStockCount()))));
            summaryTable.addCell(new Cell().add(new Paragraph("Out of Stock Count")));
            summaryTable.addCell(new Cell().add(new Paragraph(String.valueOf(inventoryReport.getOutOfStockCount()))));
            
            document.add(summaryTable);
            document.add(new Paragraph("\n"));
            
            // Low stock items
            if (!inventoryReport.getLowStockItems().isEmpty()) {
                document.add(new Paragraph("Low Stock Items").setFontSize(14).setBold());
                
                Table lowStockTable = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
                
                // Header
                lowStockTable.addHeaderCell(new Cell().add(new Paragraph("Product ID")).setBold());
                lowStockTable.addHeaderCell(new Cell().add(new Paragraph("Product Name")).setBold());
                lowStockTable.addHeaderCell(new Cell().add(new Paragraph("SKU")).setBold());
                lowStockTable.addHeaderCell(new Cell().add(new Paragraph("Stock Qty")).setBold());
                lowStockTable.addHeaderCell(new Cell().add(new Paragraph("Unit Price")).setBold());
                
                // Data rows
                for (InventoryItemDTO item : inventoryReport.getLowStockItems()) {
                    lowStockTable.addCell(new Cell().add(new Paragraph(item.getProductId())));
                    lowStockTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
                    lowStockTable.addCell(new Cell().add(new Paragraph(item.getSku())));
                    lowStockTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCurrentStock()))));
                    lowStockTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getUnitPrice()))));
                }
                
                document.add(lowStockTable);
                document.add(new Paragraph("\n"));
            }
            
            // Out of stock items
            if (!inventoryReport.getOutOfStockItems().isEmpty()) {
                document.add(new Paragraph("Out of Stock Items").setFontSize(14).setBold());
                
                Table outOfStockTable = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
                
                // Header
                outOfStockTable.addHeaderCell(new Cell().add(new Paragraph("Product ID")).setBold());
                outOfStockTable.addHeaderCell(new Cell().add(new Paragraph("Product Name")).setBold());
                outOfStockTable.addHeaderCell(new Cell().add(new Paragraph("SKU")).setBold());
                outOfStockTable.addHeaderCell(new Cell().add(new Paragraph("Unit Price")).setBold());
                
                // Data rows
                for (InventoryItemDTO item : inventoryReport.getOutOfStockItems()) {
                    outOfStockTable.addCell(new Cell().add(new Paragraph(item.getProductId())));
                    outOfStockTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
                    outOfStockTable.addCell(new Cell().add(new Paragraph(item.getSku())));
                    outOfStockTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getUnitPrice()))));
                }
                
                document.add(outOfStockTable);
                document.add(new Paragraph("\n"));
            }
            
            // All inventory items (limited to first 50 items to avoid very long PDFs)
            List<InventoryItemDTO> limitedItems = inventoryReport.getInventoryItems().size() > 50 
                ? inventoryReport.getInventoryItems().subList(0, 50)
                : inventoryReport.getInventoryItems();
                
            if (!limitedItems.isEmpty()) {
                document.add(new Paragraph("Inventory Items" + 
                    (inventoryReport.getInventoryItems().size() > 50 ? " (First 50 items)" : ""))
                    .setFontSize(14).setBold());
                
                Table inventoryTable = new Table(UnitValue.createPercentArray(6)).useAllAvailableWidth();
                
                // Header
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("Product ID")).setBold());
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("Product Name")).setBold());
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("SKU")).setBold());
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("Category")).setBold());
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("Stock Qty")).setBold());
                inventoryTable.addHeaderCell(new Cell().add(new Paragraph("Unit Price")).setBold());
                
                // Data rows
                for (InventoryItemDTO item : limitedItems) {
                    inventoryTable.addCell(new Cell().add(new Paragraph(item.getProductId())));
                    inventoryTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
                    inventoryTable.addCell(new Cell().add(new Paragraph(item.getSku())));
                    inventoryTable.addCell(new Cell().add(new Paragraph(item.getCategory())));
                    inventoryTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCurrentStock()))));
                    inventoryTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getUnitPrice()))));
                }
                
                document.add(inventoryTable);
            }
        }
        
        return baos.toByteArray();
    }
}
