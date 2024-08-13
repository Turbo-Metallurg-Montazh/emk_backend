package com.kindred.emkcrm_project_backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kindred.emkcrm_project_backend.entities.findTendersPostEntity.FindTendersPostEntity;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTender;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTenders;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.utils.deserializers_JSON.FoundTendersDeserializer;
import com.kindred.emkcrm_project_backend.utils.serializers_JSON.FindTendersPostDataSerializer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.kindred.emkcrm_project_backend.config.Constants.*;

public abstract class FindTenders {


    public static FoundTendersArray findTenders(ArrayList<String> text, Boolean strictSearch, Boolean attachments, ArrayList<String> exclude, ArrayList<String> regionIds, ArrayList<Integer> categoryIds, ArrayList<Integer> purchaseStatuses, ArrayList<Integer> laws, ArrayList<String> includeInns, ArrayList<String> excludeInns, ArrayList<Integer> procedures, ArrayList<Integer> electronicPlaces, Integer maxPriceFrom, Integer maxPriceTo, Boolean maxPriceNone, Boolean advance44, Boolean advance223, Integer smp, String dateFromInstant, String dateToInstant, int fromPage, int toPage) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate();

        // Заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(API_KEY_HEADER, API_KEY);

        Instant instant = Instant.parse(dateFromInstant);
        Date fromDate = Date.from(instant);

        instant = Instant.parse(dateToInstant);
        Date toDate = Date.from(instant);

        FindTendersPostEntity findTendersPostEntity = new FindTendersPostEntity(text, strictSearch, attachments, exclude, regionIds, categoryIds, purchaseStatuses, laws, includeInns, excludeInns, procedures, electronicPlaces, maxPriceFrom, maxPriceTo, maxPriceNone, advance44, advance223, smp, dateFromInstant, dateToInstant, fromPage);

        // Создание объекта HttpEntity с заголовками и данными
        HttpEntity<String> requestEntity = new HttpEntity<>(FindTendersPostDataSerializer.jsonData(findTendersPostEntity), headers);

        // Отправка POST-запроса и получение ответа
        String response = restTemplate.postForObject(FIND_TENDERS_URL, requestEntity, String.class);

        // Вывод ответа от сервера
        System.out.println("Ответ от сервера: " + response);

        FoundTendersArray foundTendersArray = new FoundTendersArray();
        FoundTenders foundTenders = FoundTendersDeserializer.deserialize(response);
        ArrayList<FoundTender> foundTenderArrayList = foundTenders.getFoundTenders();

        for (fromPage = 1; fromPage <= (foundTenders.getTotalCount() / ITEMS_ON_PAGE) && fromPage < toPage; fromPage++) {

            findTendersPostEntity.setPageNumber(fromPage);
            // Создание объекта HttpEntity с заголовками и данными
            requestEntity = new HttpEntity<>(FindTendersPostDataSerializer.jsonData(findTendersPostEntity), headers);

            // Отправка POST-запроса и получение ответа
            response = restTemplate.postForObject(FIND_TENDERS_URL, requestEntity, String.class);

            foundTenderArrayList.addAll(FoundTendersDeserializer.deserialize(response).getFoundTenders());
            System.out.println("Ответ от сервера: " + response);

        }
        foundTenders.setFoundTenders(foundTenderArrayList);
        foundTendersArray.setFoundTenders(foundTenders);
        foundTendersArray.setTendersDownloadCount(foundTendersArray.getFoundTenders().getFoundTenders().size());
        foundTendersArray.setFromDate(fromDate);
        foundTendersArray.setToDate(toDate);
        foundTendersArray.setTotalPagesCount(foundTenders.getTotalCount());
        return foundTendersArray;
    }

}
