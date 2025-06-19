package com.tometracker;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.BooksRequestInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleBooksServiceFactory {
    private static final String APPLICATION_NAME = "MyBooksApp/1.0";
    @Value("${gbook.api.key}")
    private static String API_KEY;

    @Bean
    public Books googleBooksClient() throws Exception {
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        return new Books.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                gsonFactory,
                null
        )
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();
    }
}
