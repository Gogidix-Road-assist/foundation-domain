package com.gogidix.rapidassist.orchestration.reporting.infrastructure.reportengine;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Excel report exporter
 */
@Slf4j
@Component
public class ExcelExporter {

    public byte[] generateExcel(Report.ReportType reportType, Map<String, Object> data, String templateLayout) {
        log.info("Generating Excel report: {}", reportType);

        // In production, this would use Apache POI
        StringBuilder excelContent = new StringBuilder();
        excelContent.append("Report Type,").append(reportType).append("\n");
        excelContent.append("Generated At,").append(java.time.LocalDateTime.now()).append("\n");

        if (data != null) {
            data.forEach((key, value) -> excelContent.append(key).append(",").append(value).append("\n"));
        }

        return excelContent.toString().getBytes();
    }
}
