package com.gogidix.rapidassist.waf.policy.service.adapters.in.web;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.waf.policy.service.domain.model.WafDecision;
import com.gogidix.rapidassist.waf.policy.service.domain.port.in.EvaluateWafRequestCommand;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/waf")
public class WafPolicyController {

    private final EvaluateWafRequestCommand evaluateWafRequestCommand;

    public WafPolicyController(EvaluateWafRequestCommand evaluateWafRequestCommand) {
        this.evaluateWafRequestCommand = evaluateWafRequestCommand;
    }

    @PostMapping("/evaluate")
    @ResponseStatus(HttpStatus.OK)
    public WafDecisionResponse evaluate(@RequestBody(required = false) EvaluateWafRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        Map<String, Object> payload = request == null || request.request() == null ? Map.of() : request.request();
        WafDecision decision = evaluateWafRequestCommand.evaluate(tenantId, payload);

        return new WafDecisionResponse(
                decision.allowed(),
                decision.ruleId(),
                decision.action(),
                decision.reason(),
                decision.details()
        );
    }
}
