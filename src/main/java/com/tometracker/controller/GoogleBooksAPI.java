package com.tometracker.controller;

import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.Volumes;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/gbooks")
public class GoogleBooksAPI {
    private final Books googleBooksClient;

    public GoogleBooksAPI(Books googleBooksClient) {
        this.googleBooksClient = googleBooksClient;
    }

    @PostMapping("/")
    public String search(@RequestBody String searchBook) throws IOException {
        String query = "intitle:" + searchBook;
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
}
