package com.gogidix.rapidassist.alerting.service.domain.port.in;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertEvent;

public interface IngestAlertEventCommand {

    void ingest(AlertEvent event);
}
