package com.shiqh.elasticsearch.demo;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

/**
 * @author shiqianghai
 * @date 2021/10/1 11:47
 */
public class TestEs {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(
                        "192.168.1.17",
                        9200
                ))
        );
//
//        BulkRequest bulkRequest = new BulkRequest();
//
//
//        for (int i = 0; i < 3; i++) {
//            User user = new User("name1" + i, "gender" + i, "address" + i);
//            IndexRequest indexRequest = new IndexRequest("user")
//                    .id(String.valueOf(i))
//                    .source(JSONObject.toJSONString(user), XContentType.JSON);
//
//            bulkRequest.add(indexRequest);
//        }
//
//
//        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);


        SearchRequest searchRequest = new SearchRequest("user");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        QueryBuilder queryBuilder = new IdsQueryBuilder().addIds("0", "1", "2");
//        MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must();

        RangeQueryBuilder age = QueryBuilders.rangeQuery("age");
        age.gt("100");
        boolQueryBuilder.filter(age);


        searchRequest.source(searchSourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits().getHits()) {
            System.out.println(hit.getSourceAsString());
        }

        restHighLevelClient.close();
    }
}