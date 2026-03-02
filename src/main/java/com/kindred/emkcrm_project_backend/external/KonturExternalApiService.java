package com.kindred.emkcrm_project_backend.external;

import com.kindred.emkcrm_project_backend.config.KonturApiProperties;
import com.kindred.emkcrm_project_backend.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class KonturExternalApiService {

    private static final String LIMIT_GROUPS_PATH = "/external/v1/limitGroups";
    private static final String TEST_PURCHASE_PATH = "/external/v1/testpurchases/{id}";
    private static final String PURCHASE_PATH = "/external/v1/purchases/{id}";
    private static final String RESULTS_PATH = "/external/v1/results/{id}";
    private static final String SEARCH_PATH = "/external/v1/search";
    private static final String TEST_FAVORITES_PATH = "/external/v1/testfavorites";
    private static final String FAVORITES_PATH = "/external/v1/favorites";
    private static final String MARKERS_PATH = "/external/v1/markers";

    private final RestTemplate restTemplate;
    private final KonturApiProperties konturApiProperties;

    public KonturExternalApiService(KonturApiProperties konturApiProperties) {
        this.konturApiProperties = konturApiProperties;
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> getLimitGroups() {
        return exchange(HttpMethod.GET, LIMIT_GROUPS_PATH, null, null, null, String.class);
    }

    public ResponseEntity<String> getTestPurchaseById(String id) {
        return exchange(HttpMethod.GET, TEST_PURCHASE_PATH, null, null, Map.of("id", id), String.class);
    }

    public ResponseEntity<String> getPurchaseById(String id) {
        return exchange(HttpMethod.GET, PURCHASE_PATH, null, null, Map.of("id", id), String.class);
    }

    // Backward compatibility for existing internal callers.
    public ResponseEntity<String> getPurchaseByIdRaw(String id) {
        return getPurchaseById(id);
    }

    public ResponseEntity<String> getPurchaseResultsById(String id) {
        return exchange(HttpMethod.GET, RESULTS_PATH, null, null, Map.of("id", id), String.class);
    }

    public ResponseEntity<String> searchPurchases(String rawSearchPayload) {
        return exchange(HttpMethod.POST, SEARCH_PATH, null, rawSearchPayload, null, String.class);
    }

    // Backward compatibility for existing internal callers.
    public ResponseEntity<String> searchPurchasesRaw(String rawSearchPayload) {
        return searchPurchases(rawSearchPayload);
    }

    public ResponseEntity<String> getTestFavorites(
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            List<String> markersNames,
            Integer page
    ) {
        int pageNumber = page == null ? 0 : page;
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        query.add("dateFrom", dateFrom.toString());
        addIfPresent(query, "dateTo", dateTo == null ? null : dateTo.toString());
        addAllIfPresent(query, "markersNames", markersNames);
        query.add("page", String.valueOf(pageNumber));

        return exchange(HttpMethod.GET, TEST_FAVORITES_PATH, query, null, null, String.class);
    }

    public ResponseEntity<String> getFavorites(
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            List<String> markersNames,
            Integer page,
            Integer sortOrder
    ) {
        int pageNumber = page == null ? 0 : page;
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        query.add("dateFrom", dateFrom.toString());
        addIfPresent(query, "dateTo", dateTo == null ? null : dateTo.toString());
        addAllIfPresent(query, "markersNames", markersNames);
        query.add("page", String.valueOf(pageNumber));
        addIfPresent(query, "sortOrder", sortOrder == null ? null : String.valueOf(sortOrder));

        return exchange(HttpMethod.GET, FAVORITES_PATH, query, null, null, String.class);
    }

    public ResponseEntity<String> getMarkers() {
        return exchange(HttpMethod.GET, MARKERS_PATH, null, null, null, String.class);
    }

    private <T> ResponseEntity<T> exchange(
            HttpMethod method,
            String path,
            MultiValueMap<String, String> queryParams,
            Object body,
            Map<String, ?> pathVariables,
            Class<T> responseType
    ) {
        try {
            URI uri = buildUri(path, queryParams, pathVariables);
            HttpEntity<?> requestEntity = buildRequestEntity(body);
            return restTemplate.exchange(uri, method, requestEntity, responseType);
        } catch (HttpStatusCodeException e) {
            log.warn("External API call failed: method={}, path={}, status={}", method, path, e.getStatusCode());
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (RestClientException e) {
            throw new ServiceUnavailableException("Failed to call external Kontur API");
        }
    }

    private HttpEntity<?> buildRequestEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(konturApiProperties.apiKeyHeader(), konturApiProperties.apiKey());
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (body != null) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        return new HttpEntity<>(body, headers);
    }

    private URI buildUri(String path, MultiValueMap<String, String> queryParams, Map<String, ?> pathVariables) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(konturApiProperties.baseUrl()).path(path);

        if (queryParams != null) {
            queryParams.forEach((key, values) -> {
                if (!values.isEmpty()) {
                    uriBuilder.queryParam(key, values.toArray());
                }
            });
        }

        if (pathVariables == null || pathVariables.isEmpty()) {
            return uriBuilder.build(true).toUri();
        }

        return uriBuilder.buildAndExpand(pathVariables).encode().toUri();
    }

    private void addIfPresent(MultiValueMap<String, String> query, String key, String value) {
        if (value != null && !value.isBlank()) {
            query.add(key, value);
        }
    }

    private void addAllIfPresent(MultiValueMap<String, String> query, String key, List<String> values) {
        if (values != null && !values.isEmpty()) {
            query.put(key, values);
        }
    }
}
