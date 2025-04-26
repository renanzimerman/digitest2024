// Created by Renan Zimerman Leite on 2023-10-04

package com.example;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This task expects you to create an implementation of a Rest API client.
 * Your code should call the API endpoint related below.
 * After receiving the JSON Response, print out how many records exists for each gender
 * API endpoint => https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda 
 * 
 * >>> Bonus <<<
 * Generate a CSV file containing many records exists for each gender and save this file to AWS S3 Bucket
 * The filename need to contains your entire name, separated by uderscore. Example: john_lennon.csv
 * AWS S3 bucket name => interview-digiage
 * The credentials you can find in Coodesh platform or ask via e-mail for recrutamento@digiage.com.br
 */
public class RESTAPIClientTask {
    public static void main(String[] args) throws Exception {
       String url = "https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda";

       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(url))
               .header("Content-Type", "application/json")
               .GET()
               .build();

               HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.body());

                Map<String, Integer> genderCount = new HashMap<>();
                for (JsonNode record : root) {
                    String gender = record.get("gender").asText();
                    genderCount.put(gender, genderCount.getOrDefault(gender, 0) + 1);
                }

                String filename = "renanzimermanleite.csv"; 
                File csv = new File(filename);

                try (FileWriter writer = new FileWriter(csv)) {
                    writer.write("Genero,Quantidade\n");
                    for (Map.Entry<String, Integer> entry : genderCount.entrySet()) {
                        writer.write(entry.getKey() + "," + entry.getValue() + "\n");
                    }
                }

                String accessKey = "AKIAU7BHLOLBKPZTHAP2";
                String secretKey = "OLBDHAT62RJ50dwl98J IbOWKL9LQxtOBYqNMQ9TY";
                String bucketName = "interview-digiage";

                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withRegion(Regions.US_WEST_2)
                        .build();
                s3Client.putObject(new PutObjectRequest(bucketName, filename, csv));
                System.out.println("File uploaded to S3: " + filename);
            }
    // API endpoint => https://3ospphrepc.execute-api.us-west-2.amazonaws.com/prod/RDSLambda 
}
