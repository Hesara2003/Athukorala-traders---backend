package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.InventoryReportDTO;
import com.example.hardwaremanagement.dto.SalesReportDTO;
import com.example.hardwaremanagement.service.ReportsService;
import com.example.hardwaremanagement.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;
    
    @Autowired
    private ExportService exportService;

    /**
     * Get sales report for a specific date range
     * @param startDate Start date for the report (format: yyyy-MM-dd)
     * @param endDate End date for the report (format: yyyy-MM-dd)
     * @return SalesReportDTO containing sales analytics
     */
    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> getSalesReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get sales report for the last 7 days
     * @return SalesReportDTO containing last week's sales analytics
     */
    @GetMapping("/sales/weekly")
    public ResponseEntity<SalesReportDTO> getWeeklySalesReport() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6); // Last 7 days including today
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get sales report for the last 30 days
     * @return SalesReportDTO containing last month's sales analytics
     */
    @GetMapping("/sales/monthly")
    public ResponseEntity<SalesReportDTO> getMonthlySalesReport() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29); // Last 30 days including today
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get sales report for the current year
     * @return SalesReportDTO containing current year's sales analytics
     */
    @GetMapping("/sales/yearly")
    public ResponseEntity<SalesReportDTO> getYearlySalesReport() {
        try {
            LocalDate startDate = LocalDate.now().withDayOfYear(1); // First day of current year
            LocalDate endDate = LocalDate.now();
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get current inventory report
     * @return InventoryReportDTO containing inventory analytics
     */
    @GetMapping("/inventory")
    public ResponseEntity<InventoryReportDTO> getInventoryReport() {
        try {
            InventoryReportDTO report = reportsService.generateInventoryReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get low stock items report
     * @return List of items that are low in stock
     */
    @GetMapping("/inventory/low-stock")
    public ResponseEntity<?> getLowStockReport() {
        try {
            InventoryReportDTO report = reportsService.generateInventoryReport();
            return ResponseEntity.ok(report.getLowStockItems());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get out of stock items report
     * @return List of items that are out of stock
     */
    @GetMapping("/inventory/out-of-stock")
    public ResponseEntity<?> getOutOfStockReport() {
        try {
            InventoryReportDTO report = reportsService.generateInventoryReport();
            return ResponseEntity.ok(report.getOutOfStockItems());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get summary of all reports
     * @return Combined summary of sales and inventory
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getReportsSummary() {
        try {
            // Get last 30 days sales report
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29);
            SalesReportDTO salesReport = reportsService.generateSalesReport(startDate, endDate);
            
            // Get inventory report
            InventoryReportDTO inventoryReport = reportsService.generateInventoryReport();
            
            // Create summary object
            ReportsSummary summary = new ReportsSummary();
            summary.setSalesReport(salesReport);
            summary.setInventoryReport(inventoryReport);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Inner class for combined summary
    public static class ReportsSummary {
        private SalesReportDTO salesReport;
        private InventoryReportDTO inventoryReport;

        public SalesReportDTO getSalesReport() {
            return salesReport;
        }

        public void setSalesReport(SalesReportDTO salesReport) {
            this.salesReport = salesReport;
        }

        public InventoryReportDTO getInventoryReport() {
            return inventoryReport;
        }

        public void setInventoryReport(InventoryReportDTO inventoryReport) {
            this.inventoryReport = inventoryReport;
        }
    }
    
    // ======================== EXPORT ENDPOINTS ========================
    
    /**
     * Export sales report to CSV format
     * @param startDate Start date for the report (format: yyyy-MM-dd)
     * @param endDate End date for the report (format: yyyy-MM-dd)
     * @return CSV file containing sales report data
     */
    @GetMapping("/sales/export/csv")
    public ResponseEntity<String> exportSalesReportToCsv(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            String csvContent = exportService.exportSalesReportToCsv(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", 
                String.format("sales-report-%s-to-%s.csv", startDate, endDate));
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export sales report to PDF format
     * @param startDate Start date for the report (format: yyyy-MM-dd)
     * @param endDate End date for the report (format: yyyy-MM-dd)
     * @return PDF file containing sales report data
     */
    @GetMapping("/sales/export/pdf")
    public ResponseEntity<byte[]> exportSalesReportToPdf(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            // Validate date range
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().build();
            }
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            byte[] pdfContent = exportService.exportSalesReportToPdf(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("sales-report-%s-to-%s.pdf", startDate, endDate));
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export weekly sales report to CSV format
     * @return CSV file containing weekly sales report data
     */
    @GetMapping("/sales/weekly/export/csv")
    public ResponseEntity<String> exportWeeklySalesReportToCsv() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            String csvContent = exportService.exportSalesReportToCsv(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "weekly-sales-report.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export weekly sales report to PDF format
     * @return PDF file containing weekly sales report data
     */
    @GetMapping("/sales/weekly/export/pdf")
    public ResponseEntity<byte[]> exportWeeklySalesReportToPdf() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            byte[] pdfContent = exportService.exportSalesReportToPdf(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "weekly-sales-report.pdf");
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export monthly sales report to CSV format
     * @return CSV file containing monthly sales report data
     */
    @GetMapping("/sales/monthly/export/csv")
    public ResponseEntity<String> exportMonthlySalesReportToCsv() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29);
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            String csvContent = exportService.exportSalesReportToCsv(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "monthly-sales-report.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export monthly sales report to PDF format
     * @return PDF file containing monthly sales report data
     */
    @GetMapping("/sales/monthly/export/pdf")
    public ResponseEntity<byte[]> exportMonthlySalesReportToPdf() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(29);
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            byte[] pdfContent = exportService.exportSalesReportToPdf(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "monthly-sales-report.pdf");
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export yearly sales report to CSV format
     * @return CSV file containing yearly sales report data
     */
    @GetMapping("/sales/yearly/export/csv")
    public ResponseEntity<String> exportYearlySalesReportToCsv() {
        try {
            LocalDate startDate = LocalDate.now().withDayOfYear(1);
            LocalDate endDate = LocalDate.now();
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            String csvContent = exportService.exportSalesReportToCsv(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "yearly-sales-report.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export yearly sales report to PDF format
     * @return PDF file containing yearly sales report data
     */
    @GetMapping("/sales/yearly/export/pdf")
    public ResponseEntity<byte[]> exportYearlySalesReportToPdf() {
        try {
            LocalDate startDate = LocalDate.now().withDayOfYear(1);
            LocalDate endDate = LocalDate.now();
            
            SalesReportDTO report = reportsService.generateSalesReport(startDate, endDate);
            byte[] pdfContent = exportService.exportSalesReportToPdf(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "yearly-sales-report.pdf");
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export inventory report to CSV format
     * @return CSV file containing inventory report data
     */
    @GetMapping("/inventory/export/csv")
    public ResponseEntity<String> exportInventoryReportToCsv() {
        try {
            InventoryReportDTO report = reportsService.generateInventoryReport();
            String csvContent = exportService.exportInventoryReportToCsv(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "inventory-report.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Export inventory report to PDF format
     * @return PDF file containing inventory report data
     */
    @GetMapping("/inventory/export/pdf")
    public ResponseEntity<byte[]> exportInventoryReportToPdf() {
        try {
            InventoryReportDTO report = reportsService.generateInventoryReport();
            byte[] pdfContent = exportService.exportInventoryReportToPdf(report);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "inventory-report.pdf");
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
