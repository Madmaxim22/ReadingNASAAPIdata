package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Main {
    private static final String APIKEY = "8AYDXb2FgP2ffKuvNjPk7CIExnxkjjclUEi5O4pf";
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=" + APIKEY;

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        List<MediaNASA> posts;
        String name;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            final HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                posts = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
                });
                posts.forEach(System.out::println);

                int idx = posts.get(0).getUrl().replaceAll("\\\\", "/").lastIndexOf("/");
                name = idx >= 0 ? posts.get(0).getUrl().substring(idx + 1) : posts.get(0).getUrl();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            final HttpGet request1 = new HttpGet(posts.get(0).getUrl());
            request1.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = httpClient.execute(request1)) {
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(name))) {
                    out.write(response.getEntity().getContent().readAllBytes());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}