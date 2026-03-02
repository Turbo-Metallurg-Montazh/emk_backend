package com.kindred.emkcrm_project_backend.services.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteMarker;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteMarkersPage;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteTender;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteTendersPage;
import com.kindred.emkcrm_project_backend.db.repositories.FoundTenderRepository;
import com.kindred.emkcrm_project_backend.db.repositories.FavoriteMarkerRepository;
import com.kindred.emkcrm_project_backend.db.repositories.FavoriteTenderRepository;
import com.kindred.emkcrm_project_backend.db.repositories.PurchaseResultRepository;
import com.kindred.emkcrm_project_backend.db.repositories.TenderRepository;
import com.kindred.emkcrm_project_backend.db.entities.foundTendersEntity.FoundTender;
import com.kindred.emkcrm_project_backend.db.entities.foundTendersEntity.FoundTenders;
import com.kindred.emkcrm_project_backend.db.entities.purchaseResultsEntity.PurchaseResult;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.TenderSourceType;
import com.kindred.emkcrm_project_backend.exception.ServiceUnavailableException;
import com.kindred.emkcrm_project_backend.external.KonturExternalApiService;
import com.kindred.emkcrm_project_backend.utils.json.TenderJsonMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;

@Service
@Transactional
public class ExternalApiDataExportService {

    private static final int MAX_JSON_PAYLOAD_SIZE = 10 * 1024 * 1024;

    private final KonturExternalApiService konturExternalApiService;
    private final TenderJsonMapper tenderJsonMapper;
    private final TenderRepository tenderRepository;
    private final PurchaseResultRepository purchaseResultRepository;
    private final FavoriteTenderRepository favoriteTenderRepository;
    private final FavoriteMarkerRepository favoriteMarkerRepository;
    private final FoundTenderRepository foundTenderRepository;

    public ExternalApiDataExportService(
            KonturExternalApiService konturExternalApiService,
            TenderJsonMapper tenderJsonMapper,
            TenderRepository tenderRepository,
            PurchaseResultRepository purchaseResultRepository,
            FavoriteTenderRepository favoriteTenderRepository,
            FavoriteMarkerRepository favoriteMarkerRepository,
            FoundTenderRepository foundTenderRepository
    ) {
        this.konturExternalApiService = konturExternalApiService;
        this.tenderJsonMapper = tenderJsonMapper;
        this.tenderRepository = tenderRepository;
        this.purchaseResultRepository = purchaseResultRepository;
        this.favoriteTenderRepository = favoriteTenderRepository;
        this.favoriteMarkerRepository = favoriteMarkerRepository;
        this.foundTenderRepository = foundTenderRepository;
    }

    public Tender exportPurchaseById(String id) {
        String body = requireBody(konturExternalApiService.getPurchaseById(id), "purchaseById");
        Tender payload = parseTender(body, "purchaseById");
        return upsertTender(payload, body, TenderSourceType.PROD, id);
    }

    public Tender exportTestPurchaseById(String id) {
        String body = requireBody(konturExternalApiService.getTestPurchaseById(id), "testPurchaseById");
        Tender payload = parseTender(body, "testPurchaseById");
        return upsertTender(payload, body, TenderSourceType.TEST, id);
    }

    public PurchaseResult exportPurchaseResultsById(String id) {
        String body = requireBody(konturExternalApiService.getPurchaseResultsById(id), "purchaseResultsById");
        PurchaseResult payload = parsePurchaseResult(body, "purchaseResultsById");
        return upsertPurchaseResult(payload, body, id);
    }

    public int exportSearchResults(String rawSearchPayload) {
        String body = requireBody(konturExternalApiService.searchPurchases(rawSearchPayload), "searchPurchases");
        FoundTenders payload = parseFoundTenders(body, "searchPurchases");
        return upsertFoundTenders(payload);
    }

    public int exportFavorites(
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            java.util.List<String> markersNames,
            Integer page,
            Integer sortOrder
    ) {
        String body = requireBody(
                konturExternalApiService.getFavorites(dateFrom, dateTo, markersNames, page, sortOrder),
                "favorites"
        );
        FavoriteTendersPage payload = parseFavoriteTendersPage(body, "favorites");
        return upsertFavoriteTenders(payload, TenderSourceType.PROD);
    }

    public int exportTestFavorites(
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            java.util.List<String> markersNames,
            Integer page
    ) {
        String body = requireBody(
                konturExternalApiService.getTestFavorites(dateFrom, dateTo, markersNames, page),
                "testFavorites"
        );
        FavoriteTendersPage payload = parseFavoriteTendersPage(body, "testFavorites");
        return upsertFavoriteTenders(payload, TenderSourceType.TEST);
    }

    public int exportMarkers() {
        String body = requireBody(konturExternalApiService.getMarkers(), "markers");
        FavoriteMarkersPage payload = parseFavoriteMarkersPage(body, "markers");
        return upsertFavoriteMarkers(payload);
    }

    private Tender upsertTender(
            Tender payload,
            String rawPayload,
            TenderSourceType sourceType,
            String fallbackId
    ) {
        String purchaseId = firstNonBlank(payload.getId(), fallbackId);
        Tender entity = tenderRepository
                .findByIdAndSourceType(purchaseId, sourceType)
                .orElseGet(Tender::new);

        entity.setId(purchaseId);
        entity.setSourceType(sourceType);
        entity.setUpdatedDatetime(payload.getUpdatedDatetime());
        entity.setInitialSum(payload.getInitialSum());
        entity.setNotificationType(payload.getNotificationType());
        entity.setNotificationPlacingWay(payload.getNotificationPlacingWay());
        entity.setAuctionDateTime(payload.getAuctionDateTime());
        entity.setDocs(payload.getDocs());
        entity.setEtpLink(payload.getEtpLink());
        entity.setEisLink(payload.getEisLink());
        entity.setLink(payload.getLink());
        entity.setOrganizer(payload.getOrganizer());
        entity.setCancelReason(payload.getCancelReason());
        entity.setPlannedPublishDate(payload.getPlannedPublishDate());
        entity.setNotificationNumber(payload.getNotificationNumber());
        entity.setTitle(payload.getTitle());
        entity.setSmp(payload.isSmp());
        entity.setPublicationDateTimeUTC(payload.getPublicationDateTimeUTC());
        entity.setApplicationDeadline(payload.getApplicationDeadline());
        entity.setCommissionDeadline(payload.getCommissionDeadline());
        entity.setContactPerson(payload.getContactPerson());
        entity.setLots(payload.getLots());
        entity.setPayloadJson(rawPayload);

        return tenderRepository.save(entity);
    }

    private PurchaseResult upsertPurchaseResult(PurchaseResult payload, String rawPayload, String fallbackId) {
        String purchaseId = firstNonBlank(payload.getPurchaseId(), fallbackId);
        PurchaseResult entity = purchaseResultRepository.findByPurchaseId(purchaseId)
                .orElseGet(PurchaseResult::new);

        entity.setPurchaseId(purchaseId);
        entity.setLink(payload.getLink());
        entity.setProtocols(payload.getProtocols());
        entity.setContractProjects(payload.getContractProjects());
        entity.setContracts(payload.getContracts());
        entity.setProtocolsCount(sizeOf(payload.getProtocols()));
        entity.setContractProjectsCount(sizeOf(payload.getContractProjects()));
        entity.setContractsCount(sizeOf(payload.getContracts()));
        entity.setPayloadJson(rawPayload);
        return purchaseResultRepository.save(entity);
    }

    private int upsertFoundTenders(FoundTenders payload) {
        ArrayList<FoundTender> items = payload.getFoundTenders();
        if (items == null || items.isEmpty()) {
            return 0;
        }
        foundTenderRepository.saveAll(items);
        return items.size();
    }

    private int upsertFavoriteTenders(FavoriteTendersPage payload, TenderSourceType sourceType) {
        ArrayList<FavoriteTender> items = payload.getItems();
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (FavoriteTender item : items) {
            String purchaseId = firstNonBlank(item.getPurchaseId(), "");
            if (purchaseId.isBlank()) {
                continue;
            }
            String markerName = firstNonBlank(item.getMarkerName(), "");

            FavoriteTender entity = favoriteTenderRepository
                    .findByPurchaseIdAndSourceTypeAndMarkerName(purchaseId, sourceType, markerName)
                    .orElseGet(FavoriteTender::new);

            entity.setPurchaseId(purchaseId);
            entity.setSourceType(sourceType);
            entity.setMarkerName(markerName);
            entity.setPageNumber(payload.getPageNumber());
            entity.setTotalCount(payload.getTotalCount());
            entity.setPayloadJson(tenderJsonMapper.writeJson(item));
            favoriteTenderRepository.save(entity);
            count++;
        }
        return count;
    }

    private int upsertFavoriteMarkers(FavoriteMarkersPage payload) {
        ArrayList<FavoriteMarker> items = payload.getItems();
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (FavoriteMarker item : items) {
            String markerId = item.getMarkerId();
            if (markerId == null || markerId.isBlank()) {
                continue;
            }

            FavoriteMarker entity = favoriteMarkerRepository.findByMarkerId(markerId)
                    .orElseGet(FavoriteMarker::new);

            entity.setMarkerId(markerId);
            entity.setName(item.getName());
            entity.setPayloadJson(tenderJsonMapper.writeJson(item));
            favoriteMarkerRepository.save(entity);
            count++;
        }
        return count;
    }

    private String requireBody(ResponseEntity<String> response, String operationName) {
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().isBlank()) {
            throw new ServiceUnavailableException(
                    String.format("External API request failed for %s. Status: %s", operationName, response.getStatusCode())
            );
        }
        return response.getBody();
    }

    private Tender parseTender(String payload, String operationName) {
        try {
            validatePayload(payload, operationName);
            return tenderJsonMapper.readTender(payload);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException("Failed to parse response from external API for " + operationName);
        }
    }

    private PurchaseResult parsePurchaseResult(String payload, String operationName) {
        try {
            validatePayload(payload, operationName);
            return tenderJsonMapper.readPurchaseResult(payload);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException("Failed to parse response from external API for " + operationName);
        }
    }

    private FavoriteTendersPage parseFavoriteTendersPage(String payload, String operationName) {
        try {
            validatePayload(payload, operationName);
            return tenderJsonMapper.readFavoriteTendersPage(payload);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException("Failed to parse response from external API for " + operationName);
        }
    }

    private FavoriteMarkersPage parseFavoriteMarkersPage(String payload, String operationName) {
        try {
            validatePayload(payload, operationName);
            return tenderJsonMapper.readFavoriteMarkersPage(payload);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException("Failed to parse response from external API for " + operationName);
        }
    }

    private FoundTenders parseFoundTenders(String payload, String operationName) {
        try {
            validatePayload(payload, operationName);
            return tenderJsonMapper.readFoundTenders(payload);
        } catch (JsonProcessingException e) {
            throw new ServiceUnavailableException("Failed to parse response from external API for " + operationName);
        }
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    private static <T> Integer sizeOf(java.util.List<T> list) {
        return list == null ? 0 : list.size();
    }

    private void validatePayload(String payload, String operationName) {
        if (payload == null || payload.isBlank()) {
            throw new ServiceUnavailableException("Empty payload from external API for " + operationName);
        }
        if (payload.length() > MAX_JSON_PAYLOAD_SIZE) {
            throw new ServiceUnavailableException("Payload is too large for operation " + operationName);
        }
    }
}
