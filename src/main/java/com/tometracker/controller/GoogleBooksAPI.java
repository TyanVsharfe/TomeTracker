package com.tometracker.controller;

import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/gbooks")
public class GoogleBooksAPI {
    private final Books googleBooksClient;

    public GoogleBooksAPI(Books googleBooksClient) {
        this.googleBooksClient = googleBooksClient;
    }

    @PostMapping("")
    public String search(@RequestBody String searchBook) throws IOException {
        String decodedString = URLDecoder.decode(searchBook, StandardCharsets.UTF_8);
        System.out.println(decodedString);
        //String query = "intitle:" + decodedString;
        String query = decodedString;
        Books.Volumes.List volumesList = googleBooksClient.volumes().list(query);
        volumesList.setOrderBy("relevance");
        //volumesList.setLangRestrict("ru");

        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {
            System.out.println("Книги не найдены.");
            return "";
        }

        for (Volume volume : volumes.getItems()) {
            System.out.println(volume.getSaleInfo().getRetailPrice()); // Текущая цена
            System.out.println(volume.getSaleInfo().getBuyLink() + "\n--------");
            System.out.println();

            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
            System.out.println("Название: " + volumeInfo.getTitle());
            System.out.println("Авторы: " + volumeInfo.getAuthors());
            System.out.println("Описание: " + volumeInfo.getDescription());
            System.out.println(volumeInfo.getContentVersion() + "\n" + volumeInfo.getPublishedDate()
                    + "\n" + volumeInfo.getAverageRating() + "\n" + volumeInfo.getPageCount()
                    + "\n" + volumeInfo.getLanguage() + "\n" + volumeInfo.getPrintType()
                    + "\n" + volumeInfo.getPrintedPageCount() + "\n" + volumeInfo.getCategories());

            if (volumeInfo.getIndustryIdentifiers() != null) {
                for (Volume.VolumeInfo.IndustryIdentifiers identifiers : volumeInfo.getIndustryIdentifiers()) {
                    System.out.println(identifiers);
                }
            }
            System.out.println("-------------------------");
        }

        return new Gson().toJson(volumes);
    }

    @GetMapping("/{gBookId}")
    public String getBookById(@PathVariable String gBookId) throws IOException {
        String decodedVolumeId = URLDecoder.decode(gBookId, StandardCharsets.UTF_8);
        System.out.println("Полученный volumeId: " + decodedVolumeId);

        Books.Volumes.Get volumeGet = googleBooksClient.volumes().get(decodedVolumeId);
        Volume volume = volumeGet.execute();

        if (volume == null) {
            System.out.println("Книга не найдена.");
            return "";
        }

        Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
        System.out.println("Название: " + volumeInfo.getTitle());
        System.out.println("Авторы: " + volumeInfo.getAuthors());
        System.out.println("Описание: " + volumeInfo.getDescription());
        System.out.println(volumeInfo.getContentVersion() + "\n" + volumeInfo.getPublishedDate()
                + "\n" + volumeInfo.getAverageRating() + "\n" + volumeInfo.getPageCount()
                + "\n" + volumeInfo.getLanguage() + "\n" + volumeInfo.getPrintType()
                + "\n" + volumeInfo.getPrintedPageCount() + "\n" + volumeInfo.getCategories());

        if (volumeInfo.getIndustryIdentifiers() != null) {
            for (Volume.VolumeInfo.IndustryIdentifiers identifiers : volumeInfo.getIndustryIdentifiers()) {
                System.out.println(identifiers);
            }
        }
        System.out.println("-------------------------");

        return new Gson().toJson(volume);
    }

    @GetMapping("/v2/{gBookId}")
    public String getBookByIdV2(@PathVariable String gBookId) throws IOException {
        String decodedVolumeId = URLDecoder.decode(gBookId, StandardCharsets.UTF_8);
        System.out.println("Полученный volumeId: " + decodedVolumeId);

        String bookUrl = "https://content-books.googleapis.com/books/v1/volumes/" + decodedVolumeId;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(bookUrl, String.class);

        Volume volume;

        if (response.getStatusCode().is2xxSuccessful()) {
            volume = googleBooksClient.getJsonFactory().fromString(response.getBody(), Volume.class);
        } else {
            throw new RuntimeException("Failed to fetch book volume: " + response.getStatusCode());
        }

        if (volume == null) {
            System.out.println("Книга не найдена.");
            return "";
        }

        Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
        System.out.println("Название: " + volumeInfo.getTitle());
        System.out.println("Авторы: " + volumeInfo.getAuthors());
        System.out.println("Описание: " + volumeInfo.getDescription());
        System.out.println(volumeInfo.getContentVersion() + "\n" + volumeInfo.getPublishedDate()
                + "\n" + volumeInfo.getAverageRating() + "\n" + volumeInfo.getPageCount()
                + "\n" + volumeInfo.getLanguage() + "\n" + volumeInfo.getPrintType()
                + "\n" + volumeInfo.getPrintedPageCount() + "\n" + volumeInfo.getCategories());

        if (volumeInfo.getIndustryIdentifiers() != null) {
            for (Volume.VolumeInfo.IndustryIdentifiers identifiers : volumeInfo.getIndustryIdentifiers()) {
                System.out.println(identifiers);
            }
        }
        System.out.println("-------------------------");

        return new Gson().toJson(volume);
    }

}
