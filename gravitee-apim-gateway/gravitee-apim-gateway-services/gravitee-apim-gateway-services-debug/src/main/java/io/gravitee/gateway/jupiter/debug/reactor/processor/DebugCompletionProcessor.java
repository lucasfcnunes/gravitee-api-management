/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.jupiter.debug.reactor.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.Api;
import io.gravitee.definition.model.HttpResponse;
import io.gravitee.definition.model.PolicyScope;
import io.gravitee.definition.model.debug.DebugMetrics;
import io.gravitee.definition.model.debug.PreprocessorStep;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.debug.definition.DebugApi;
import io.gravitee.gateway.jupiter.api.ExecutionPhase;
import io.gravitee.gateway.jupiter.api.context.RequestExecutionContext;
import io.gravitee.gateway.jupiter.core.processor.Processor;
import io.gravitee.gateway.jupiter.core.processor.Processor;
import io.gravitee.gateway.jupiter.debug.policy.steps.DebugStep;
import io.gravitee.gateway.jupiter.debug.reactor.context.DebugRequestExecutionContext;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.EventRepository;
import io.gravitee.repository.management.model.ApiDebugStatus;
import io.gravitee.repository.management.model.Event;
import io.reactivex.Completable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DebugCompletionProcessor implements Processor {

    private final Logger LOGGER = LoggerFactory.getLogger(DebugCompletionProcessor.class);
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public DebugCompletionProcessor(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getId() {
        return "processor-debug-completion";
    }

    @Override
    public Completable execute(final RequestExecutionContext ctx) {
        return Completable.fromRunnable(() -> {
            final DebugRequestExecutionContext debugContext = (DebugRequestExecutionContext) ctx;
            final DebugApi debugApiComponent = (DebugApi) debugContext.getComponent(Api.class);

            Event event = null;

            try {
                event = eventRepository.findById(debugApiComponent.getEventId()).orElseThrow(TechnicalException::new);
                final io.gravitee.definition.model.debug.DebugApi debugApi = computeDebugApiEventPayload(debugContext, debugApiComponent);

                event.setPayload(objectMapper.writeValueAsString(debugApi));
                updateEvent(event, ApiDebugStatus.SUCCESS);
            } catch (JsonProcessingException | TechnicalException e) {
                LOGGER.error("Error occurs while saving debug event", e);
                failEvent(event);
            }
        });
    }

    private io.gravitee.definition.model.debug.DebugApi computeDebugApiEventPayload(
        DebugRequestExecutionContext debugContext,
        DebugApi debugApiComponent
    ) {
        final io.gravitee.definition.model.debug.DebugApi debugApi = convert(debugApiComponent);
        PreprocessorStep preprocessorStep = createPreprocessorStep(debugContext);
        debugApi.setPreprocessorStep(preprocessorStep);
        debugApi.setDebugSteps(convert(debugContext.getDebugSteps()));

        // TODO
        //        HttpResponse response = createResponse(
        //            debugContext.response().headers(),
        //            debugContext.response().status(),
        //            ((VertxHttpServerResponseDebugDecorator) debugContext.response()).getBuffer()
        //        );
        //        debugApi.setResponse(response);

        //        HttpResponse invokerResponse = createResponse(
        //            debugContext.getInvokerResponse().getHeaders(),
        //            debugContext.getInvokerResponse().getStatus(),
        //            debugContext.getInvokerResponse().getBuffer()
        //        );
        //        debugApi.setBackendResponse(invokerResponse);

        debugApi.setMetrics(createMetrics(debugContext.request().metrics()));

        return debugApi;
    }

    private DebugMetrics createMetrics(io.gravitee.reporter.api.http.Metrics requestMetrics) {
        final DebugMetrics metrics = new DebugMetrics();
        if (requestMetrics != null) {
            metrics.setApiResponseTimeMs(requestMetrics.getApiResponseTimeMs());
            metrics.setProxyLatencyMs(requestMetrics.getProxyLatencyMs());
            metrics.setProxyResponseTimeMs(requestMetrics.getProxyResponseTimeMs());
        }
        return metrics;
    }

    private PreprocessorStep createPreprocessorStep(DebugRequestExecutionContext debugContext) {
        final PreprocessorStep preprocessorStep = new PreprocessorStep();
        preprocessorStep.setAttributes(debugContext.getInitialAttributes());
        preprocessorStep.setHeaders(convertHeaders(debugContext.getInitialHeaders()));
        return preprocessorStep;
    }

    private HttpResponse createResponse(HttpHeaders httpHeaders, int statusCode, Buffer bodyBuffer) {
        HttpResponse response = new HttpResponse();
        Map<String, List<String>> headers = convertHeaders(httpHeaders);
        response.setHeaders(headers);
        response.statusCode(statusCode);
        response.setBody(bodyBuffer.toString());
        return response;
    }

    Map<String, List<String>> convertHeaders(HttpHeaders headersMultimap) {
        Map<String, List<String>> headers = new HashMap<>();
        if (headersMultimap != null) {
            headersMultimap.forEach(e -> headers.put(e.getKey(), headersMultimap.getAll(e.getKey())));
        }
        return headers;
    }

    private void failEvent(Event debugEvent) {
        try {
            if (debugEvent != null) {
                updateEvent(debugEvent, ApiDebugStatus.ERROR);
            }
        } catch (TechnicalException e) {
            LOGGER.error("Error when updating event {} with ERROR status", debugEvent.getId());
        }
    }

    private void updateEvent(Event debugEvent, ApiDebugStatus apiDebugStatus) throws TechnicalException {
        debugEvent.getProperties().put(Event.EventProperties.API_DEBUG_STATUS.getValue(), apiDebugStatus.name());
        eventRepository.update(debugEvent);
    }

    private io.gravitee.definition.model.debug.DebugApi convert(DebugApi content) {
        io.gravitee.definition.model.debug.DebugApi debugAPI = new io.gravitee.definition.model.debug.DebugApi();
        debugAPI.setName(content.getName());
        debugAPI.setId(content.getId());
        debugAPI.setDefinitionVersion(content.getDefinitionVersion());
        debugAPI.setResponse(content.getResponse());
        debugAPI.setRequest(content.getRequest());
        debugAPI.setFlowMode(content.getFlowMode());
        debugAPI.setFlows(content.getFlows());
        debugAPI.setPathMappings(content.getPathMappings());
        debugAPI.setPlans(content.getPlans());
        debugAPI.setPaths(content.getPaths());
        debugAPI.setServices(content.getServices());
        debugAPI.setProxy(content.getProxy());
        debugAPI.setProperties(content.getProperties());
        debugAPI.setResources(content.getResources());
        debugAPI.setServices(content.getServices());
        debugAPI.setResponseTemplates(content.getResponseTemplates());
        return debugAPI;
    }

    private List<io.gravitee.definition.model.debug.DebugStep> convert(List<DebugStep<?>> debugSteps) {
        return debugSteps.stream().map(this::convert).collect(Collectors.toList());
    }

    private io.gravitee.definition.model.debug.DebugStep convert(DebugStep<?> ds) {
        final io.gravitee.definition.model.debug.DebugStep debugStep = new io.gravitee.definition.model.debug.DebugStep();
        debugStep.setPolicyInstanceId(ds.getId());
        debugStep.setPolicyId(ds.getPolicyId());
        debugStep.setDuration(ds.elapsedTime().toNanos());
        debugStep.setStatus(ds.getStatus());
        debugStep.setCondition(ds.getCondition());
        debugStep.setError(ds.getError());
        debugStep.setStage(ds.getFlowPhase());
        if (ds.getExecutionPhase() == ExecutionPhase.REQUEST) {
            debugStep.setScope(PolicyScope.ON_REQUEST);
        } else if (ds.getExecutionPhase() == ExecutionPhase.RESPONSE) {
            debugStep.setScope(PolicyScope.ON_RESPONSE);
        }

        Map<String, Object> result = new HashMap<>();
        ds
            .getDiff()
            .forEach((key, value) -> {
                if (DebugStep.DIFF_KEY_HEADERS.equals(key)) {
                    // Headers are converted to Map<String, List<String>>
                    result.put(DebugStep.DIFF_KEY_HEADERS, convertHeaders((HttpHeaders) value));
                } else if (DebugStep.DIFF_KEY_BODY_BUFFER.equals(key)) {
                    // Body is converted from Buffer to String
                    result.put(DebugStep.DIFF_KEY_BODY, value.toString());
                } else {
                    // Everything else is kept as is
                    result.put(key, value);
                }
            });

        debugStep.setResult(result);
        return debugStep;
    }
}
