package ru.netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static final String REMOTE_SERVICE_URL =
            "https://api.nasa.gov/planetary/apod?api_key=gfPVNIqxWTPCUcN7biaaLNlUyy8weukXS3EdfcBC";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);

        CloseableHttpResponse response = httpClient.execute(request);

        PictureOfDay pictureOfDay = mapper.readValue(response.getEntity().getContent(),
                PictureOfDay.class);

        response = httpClient.execute(new HttpGet(pictureOfDay.getUrl()));

        byte[] img = response.getEntity().getContent().readAllBytes();
        String fileName = new File(pictureOfDay.getUrl()).getName();

        try (FileOutputStream out = new FileOutputStream(fileName);
             BufferedOutputStream bos = new BufferedOutputStream(out)) {
            bos.write(img, 0, img.length);
        }
    }
}
