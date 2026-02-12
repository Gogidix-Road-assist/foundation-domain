package com.gogidix.rapidassist.orchestration.reporting.infrastructure.reportengine;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Report generation engine supporting multiple output formats.
 */
@Slf4j
@Component
public class ReportEngine {

    private final PdfExporter pdfExporter;
    private final ExcelExporter excelExporter;
    private final CsvExporter csvExporter;

    public ReportEngine(PdfExporter pdfExporter, ExcelExporter excelExporter, CsvExporter csvExporter) {
        this.pdfExporter = pdfExporter;
        this.excelExporter = excelExporter;
        this.csvExporter = csvExporter;
    }

    /**
     * Generate report in specified format
     */
    public byte[] generateReport(Report.ReportType reportType,
                                Report.OutputFormat outputFormat,
                                Map<String, Object> data,
                                String templateLayout) {
        log.info("Generating report: type={}, format={}", reportType, outputFormat);

        try {
            return switch (outputFormat) {
                case PDF -> pdfExporter.generatePdf(reportType, data, templateLayout);
                case EXCEL -> excelExporter.generateExcel(reportType, data, templateLayout);
                case CSV -> csvExporter.generateCsv(reportType, data, templateLayout);
                case HTML -> generateHtml(reportType, data);
                case JSON -> generateJson(data);
            };
        } catch (Exception e) {
            log.error("Failed to generate report", e);
            throw new ReportGenerationException("Failed to generate report: " + e.getMessage(), e);
        }
    }

    /**
     * Save report to file system
     */
    public String saveReportFile(byte[] content, String reportId, Report.OutputFormat format) {
        String extension = switch (format) {
            case PDF -> ".pdf";
            case EXCEL -> ".xlsx";
            case CSV -> ".csv";
            case HTML -> ".html";
            case JSON -> ".json";
        };

        String filePath = "/reports/" + reportId + extension;

        // In production, this would save to cloud storage or file system
        log.info("Saving report file: {}", filePath);

        return filePath;
    }

    /**
     * Delete report file
     */
    public void deleteReportFile(String filePath) {
        log.info("Deleting report file: {}", filePath);
        // Implementation would delete from storage
    }

    private byte[] generateHtml(Report.ReportType reportType, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>").append(reportType).append(" Report</title></head><body>");
        html.append("<h1>").append(reportType).append(" Report</h1>");
        html.append("<p>Generated at: ").append(LocalDateTime.now()).append("</p>");
        html.append("<table border='1'>");

        // Simple HTML table generation
        if (data != null) {
            data.forEach((key, value) -> {
                html.append("<tr><td>").append(key).append("</td><td>").append(value).append("</td></tr>");
            });
        }

        html.append("</table></body></html>");
        return html.toString().getBytes();
    }

    private byte[] generateJson(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"reportType\": \"").append(data.get("reportType")).append("\",");
        json.append("\"generatedAt\": \"").append(LocalDateTime.now()).append("\",");
        json.append("\"data\": {");

        if (data != null) {
            data.forEach((key, value) -> {
                json.append("\"").append(key).append("\": \"").append(value).append("\",");
            });
        }

        if (!data.isEmpty()) {
            json.deleteCharAt(json.length() - 1); // Remove trailing comma
        }

        json.append("}}");
        return json.toString().getBytes();
    }
}
