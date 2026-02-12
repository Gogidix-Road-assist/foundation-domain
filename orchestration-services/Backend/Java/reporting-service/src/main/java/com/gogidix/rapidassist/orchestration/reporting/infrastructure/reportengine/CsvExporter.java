package com.gogidix.rapidassist.orchestration.reporting.infrastructure.reportengine;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * CSV report exporter
 */
@Slf4j
@Component
public class CsvExporter {

    public byte[] generateCsv(Report.ReportType reportType, Map<String, Object> data, String templateLayout) {
        log.info("Generating CSV report: {}", reportType);

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Key,Value\n");
        csvContent.append("Report Type,").append(reportType).append("\n");
        csvContent.append("Generated At,").append(java.time.LocalDateTime.now()).append("\n");

        if (data != null) {
            data.forEach((key, value) -> csvContent.append(key).append(",").append(value).append("\n"));
        }

        return csvContent.toString().getBytes();
    }
}
