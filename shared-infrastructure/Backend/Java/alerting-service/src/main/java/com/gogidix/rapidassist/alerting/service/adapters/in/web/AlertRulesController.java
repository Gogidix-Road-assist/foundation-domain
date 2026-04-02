package com.gogidix.rapidassist.alerting.service.adapters.in.web;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.in.ListAlertRulesQuery;
import com.gogidix.rapidassist.alerting.service.domain.port.in.UpsertAlertRuleCommand;
import com.gogidix.rapidassist.alerting.service.infrastructure.web.UpsertAlertRuleRequest;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alert-rules")
@Tag(name = "Alert Rules", description = "APIs for managing alert rules")
public class AlertRulesController {

    private final UpsertAlertRuleCommand upsert;
    private final ListAlertRulesQuery list;

    public AlertRulesController(UpsertAlertRuleCommand upsert, ListAlertRulesQuery list) {
        this.upsert = upsert;
        this.list = list;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create or update an alert rule", description = "Upserts an alert rule. Creates new rule if it doesn't exist, otherwise updates existing rule. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Alert rule accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication")
    })
    public void upsert(@Valid @RequestBody UpsertAlertRuleRequest request) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        upsert.upsert(new AlertRule(
                ctx.tenantId(),
                request.getRuleId(),
                request.getName(),
                request.getSeverity(),
                request.isEnabled(),
                request.getConditions(),
                Instant.now()
        ));
    }

    @GetMapping
    @Operation(summary = "List alert rules", description = "Retrieves a list of alert rules for the tenant. Requires valid tenantId in JWT context.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved alert rules"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid authentication")
    })
    public List<AlertRule> list(
            @Parameter(description = "Maximum number of results to return")
            @RequestParam(defaultValue = "100") int limit) {
        RequestContext ctx = RequestContextHolder.get().orElse(null);
        if (ctx == null || ctx.tenantId() == null || ctx.tenantId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return list.list(ctx.tenantId(), limit);
    }
}
