package com.cocoiland.pricehistory.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    // URL and API key
    String serverUrl = "https://elastic-search-server-url.com";
    String apiKey = "apiKey";

    @Bean
    public ElasticsearchClient elasticsearchClient() {

        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        // And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);
        return esClient;
    }
}









//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.message.BasicHeader;
//import org.elasticsearch.client.RestClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//@Configuration
//public class ElasticsearchConfig {
//
//    // URL and API key
//    String serverUrl = "https://my-deployment-7cbb54.es.ap-south-1.aws.elastic-cloud.com";
//    String apiKey = "Z01XcC00b0J0Y0VTOTNPNWhWSlQ6bkR4c2E2Ym5TLTY4UFlDZDNkZjRUZw==";
//
//    @Bean
//    public ElasticsearchClient elasticsearchClient() {
//        // Create the low-level client
//        RestClient restClient = RestClient
//                .builder(HttpHost.create(serverUrl))
//                .setDefaultHeaders(new Header[]{
//                        new BasicHeader("Authorization", "ApiKey " + apiKey)
//                })
//                .build();
//
//        // Create the transport with a Jackson mapper
//        ElasticsearchTransport transport = new RestClientTransport(
//                restClient, new JacksonJsonpMapper());
//
//        // And create the API client
////        return new ElasticsearchClient(transport);
//
//
//
//        RestClient rrestClient = RestClient.builder(
//                HttpHost.create(serverUrl)).build();
//
//        JacksonJsonpMapper jsonMapper = new JacksonJsonpMapper();
//
//        ElasticsearchTransport elasticsearchTransport =
//                new RestClientTransport(restClient, jsonMapper);
//
//    }
//}
//
//



























//package com.cocoiland.pricehistory.config;
//
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import org.apache.http.Header;
//import org.apache.http.HttpHost;
//import org.apache.http.message.BasicHeader;
//import org.elasticsearch.client.RestClient;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class ElasticsearchConfig {
//    // URL and API key
////    String serverUrl = "https://localhost:9200";
//    String serverUrl = "https://my-deployment-7cbb54.es.ap-south-1.aws.elastic-cloud.com";
//    String apiKey = "VVhidjk0b0JwaGNFaW5pMkU2bUc6LUlkekRFSGFTMW10RDhfc3psb3FTQQ";
//
//    // Create the low-level client
//    RestClient restClient = RestClient
//            .builder(HttpHost.create(serverUrl))
//            .setDefaultHeaders(new Header[]{
//                    new BasicHeader("Authorization", "ApiKey " + apiKey)
//            })
//            .build();
//
//    // Create the transport with a Jackson mapper
//    ElasticsearchTransport transport = new RestClientTransport(
//            restClient, new JacksonJsonpMapper());
//
//    // And create the API client
//    ElasticsearchClient esClient = new ElasticsearchClient(transport);
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
