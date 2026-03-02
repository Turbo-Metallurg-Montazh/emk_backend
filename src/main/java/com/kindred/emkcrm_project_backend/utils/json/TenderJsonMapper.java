package com.kindred.emkcrm_project_backend.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPost;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteMarkersPage;
import com.kindred.emkcrm_project_backend.db.entities.favoritesEntity.FavoriteTendersPage;
import com.kindred.emkcrm_project_backend.db.entities.foundTendersEntity.FoundTender;
import com.kindred.emkcrm_project_backend.db.entities.foundTendersEntity.FoundTenders;
import com.kindred.emkcrm_project_backend.db.entities.purchaseResultsEntity.PurchaseResult;
import com.kindred.emkcrm_project_backend.db.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

@Component
public class TenderJsonMapper {

    private static final int MAX_RAW_ITEM_JSON_LENGTH = 100_000;
    private static final StreamReadConstraints EXTERNAL_STREAM_READ_CONSTRAINTS = StreamReadConstraints.builder()
            .maxNestingDepth(200)
            .maxStringLength(1_000_000)
            .build();

    private final ObjectMapper objectMapper;
    private final ObjectReader tenderReader;
    private final ObjectReader purchaseResultReader;
    private final ObjectReader favoriteTendersPageReader;
    private final ObjectReader favoriteMarkersPageReader;
    private final ObjectReader objectNodeReader;
    private final ObjectWriter nonNullWriter;

    public TenderJsonMapper() {
        this.objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        this.objectMapper.getFactory().setStreamReadConstraints(EXTERNAL_STREAM_READ_CONSTRAINTS);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);

        this.tenderReader = this.objectMapper.readerFor(Tender.class);
        this.purchaseResultReader = this.objectMapper.readerFor(PurchaseResult.class);
        this.favoriteTendersPageReader = this.objectMapper.readerFor(FavoriteTendersPage.class);
        this.favoriteMarkersPageReader = this.objectMapper.readerFor(FavoriteMarkersPage.class);
        this.objectNodeReader = this.objectMapper.readerFor(ObjectNode.class);

        ObjectMapper nonNullMapper = this.objectMapper.copy();
        nonNullMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        this.nonNullWriter = nonNullMapper.writer();
    }

    public String serializeFilter(FindTendersPost findTendersPost) throws JsonProcessingException {
        Objects.requireNonNull(findTendersPost, "findTendersPost must not be null");
        return nonNullWriter.writeValueAsString(findTendersPost);
    }

    public String patchSearchPayload(String sourceJson, String dateFromInstant, String dateToInstant, int pageNumber)
            throws JsonProcessingException {
        if (sourceJson == null || sourceJson.isBlank()) {
            throw new BadRequestException("json filter must not be blank");
        }

        ObjectNode payload = objectNodeReader.readValue(sourceJson);
        payload.put("DateTimeFrom", dateFromInstant);
        payload.put("DateTimeTo", dateToInstant);
        payload.put("PageNumber", pageNumber);

        return objectMapper.writeValueAsString(payload);
    }

    public FoundTenders readFoundTenders(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new BadRequestException("found tenders payload must not be blank");
        }

        JsonNode root = objectMapper.readTree(payload);
        FoundTenders foundTenders = objectMapper.treeToValue(root, FoundTenders.class);
        JsonNode itemsNode = root.path("Items");

        ArrayList<FoundTender> items = new ArrayList<>();
        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                FoundTender tender = objectMapper.treeToValue(itemNode, FoundTender.class);
                tender.setOtherInformation(compactRawItem(itemNode.toString()));
                items.add(tender);
            }
        }

        foundTenders.setFoundTenders(items);
        return foundTenders;
    }

    public Tender readTender(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new BadRequestException("tender payload must not be blank");
        }
        return tenderReader.readValue(payload);
    }

    public PurchaseResult readPurchaseResult(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new BadRequestException("purchase result payload must not be blank");
        }
        return purchaseResultReader.readValue(payload);
    }

    public FavoriteTendersPage readFavoriteTendersPage(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new BadRequestException("favorites payload must not be blank");
        }
        return favoriteTendersPageReader.readValue(payload);
    }

    public FavoriteMarkersPage readFavoriteMarkersPage(String payload) throws JsonProcessingException {
        if (payload == null || payload.isBlank()) {
            throw new BadRequestException("markers payload must not be blank");
        }
        return favoriteMarkersPageReader.readValue(payload);
    }

    public String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize JSON");
        }
    }

    private String compactRawItem(String rawItemJson) {
        if (rawItemJson == null || rawItemJson.length() <= MAX_RAW_ITEM_JSON_LENGTH) {
            return rawItemJson;
        }
        return rawItemJson.substring(0, MAX_RAW_ITEM_JSON_LENGTH);
    }
}
