package com.gogidix.rapidassist.logging.aggregation.service.domain.port.in;

import com.gogidix.rapidassist.logging.aggregation.service.domain.model.LogEvent;

public interface IngestLogEventCommand {

    void ingest(LogEvent event);
}
