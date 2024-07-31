package com.kindred.emkcrm_project_backend;

import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTender;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTenders;
import com.kindred.emkcrm_project_backend.entities.foundTendersEntity.FoundTendersArray;
import com.kindred.emkcrm_project_backend.entities.tenderEntity.Tender;
import com.kindred.emkcrm_project_backend.utils.GetTender;
import com.kindred.emkcrm_project_backend.utils.deserializers_JSON.FoundTendersDeserializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import static com.kindred.emkcrm_project_backend.config.Constants.*;

public class RestTemplateExample {

    public static void main(String[] args) throws JsonProcessingException {

        Tender tender = GetTender.getTenderInfo("014020000452100021");
        System.out.println(tender.getUpdatedDatetime().toString());
        System.out.println(tender.getInitialSum().getPrice());
        System.out.println(tender.getId());






        RestTemplate restTemplate = new RestTemplate();

        // Заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(API_KEY_HEADER, API_KEY);


        String dateFromInstant = "2011-12-30T07:43:31.681Z";
        Instant instant = Instant.parse(dateFromInstant);
        Date fromDate = Date.from(instant);

        String dateToInstant = "2031-12-30T07:43:31.681Z";
        instant = Instant.parse(dateToInstant);
        Date toDate = Date.from(instant);


        int pageCount = 0;

        // Данные для отправки
        String postData = "{\n" +
                "    \n" +
                "    \"DateTimeFrom\": \"" + dateFromInstant + "\",\n" +
                "    \"DateTimeTo\": \"" + dateToInstant + "\",\n" +
                "    \"PageNumber\": \"" + pageCount + "\"\n" +
                "}";

        // Создание объекта HttpEntity с заголовками и данными
        HttpEntity<String> requestEntity = new HttpEntity<>(postData, headers);

        // Отправка POST-запроса и получение ответа
        String response = restTemplate.postForObject(FIND_TENDERS_URL, requestEntity, String.class);

        // Вывод ответа от сервера
        System.out.println("Ответ от сервера: " + response);

        FoundTendersArray foundTendersArray = new FoundTendersArray();
        FoundTenders foundTenders = FoundTendersDeserializer.deserialize(response);
        ArrayList<FoundTender> foundTenderArrayList = foundTenders.getFoundTenders();

        for (pageCount = 1; pageCount <= (foundTenders.getTotalCount()/ITEMS_ON_PAGE) && pageCount < MAX_PAGE_COUNT; pageCount++) {
            postData = "{\n" +
                    "    \n" +
                    "    \"DateTimeFrom\": \"" + dateFromInstant + "\",\n" +
                    "    \"DateTimeTo\": \"" + dateToInstant + "\",\n" +
                    "    \"PageNumber\": \"" + pageCount + "\"\n" +
                    "}";

            // Создание объекта HttpEntity с заголовками и данными
            requestEntity = new HttpEntity<>(postData, headers);

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
    }
}