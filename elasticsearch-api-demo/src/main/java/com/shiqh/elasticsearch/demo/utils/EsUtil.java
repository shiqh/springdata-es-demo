package com.shiqh.elasticsearch.demo.utils;

import com.alibaba.fastjson.JSON;
import com.shiqh.elasticsearch.demo.model.EsEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class EsUtil {


    @Value("${es.host}")
    public String host;

    @Value("${es.port}")
    public int port;

    @Value("${es.scheme}")
    public String scheme;

    public static final String INDEX_NAME = "demo";

    private static final String CREATE_INDEX = "{\n" +
            "\"product\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"userId\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\":\"text\"" +
            "      },\n" +
            "      \"url\":{\n" +
            "        \"type\":\"text\"\n" +
            "      }\n" +
            "    }\n" +
            "    }\n" +
            "  }";

    private static RestHighLevelClient client = null;


    @PostConstruct
    public void init() {
        try {
            if (client != null) {
                client.close();
            }
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
//            if (this.indexExist(INDEX_NAME)) {
//                return;
//            }
//            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
//            request.settings(Settings.builder()
//                    .put("index.number_of_shards", 3)
//                    .put("index.number_of_replicas", 2)
//            );
//            request.mapping(CREATE_INDEX, XContentType.JSON);
//            CreateIndexResponse res = client.indices().create(request, RequestOptions.DEFAULT);
//            if (!res.isAcknowledged()) {
//                throw new RuntimeException("???????????????");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Description: ????????????index????????????
     *
     * @param index index???
     * @return boolean
     * @author fanxb
     * @date 2019/7/24 14:57
     */
    public boolean indexExist(String index) throws Exception {
        GetIndexRequest request = new GetIndexRequest(index);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * Description: ??????/??????????????????
     *
     * @param index  index
     * @param entity ??????
     * @author fanxb
     * @date 2019/7/24 15:02
     */
    public void insertOrUpdateOne(String index, EsEntity entity) {
        IndexRequest request = new IndexRequest(index);
        request.id(entity.getId());
        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: ??????????????????
     *
     * @param index index
     * @param list  ???????????????
     * @author fanxb
     * @date 2019/7/24 17:38
     */
    public void insertBatch(String index, List<EsEntity> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(index).id(item.getId())
                .source(JSON.toJSONString(item.getData()), XContentType.JSON)));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: ????????????
     *
     * @param index  index
     * @param idList ???????????????
     * @author fanxb
     * @date 2019/7/25 14:24
     */
    public <T> void deleteBatch(String index, Collection<T> idList) {
        BulkRequest request = new BulkRequest(index, "_doc");
        idList.forEach(item -> request.add(new DeleteRequest(item.toString())));
        try {
            client.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: ??????
     *
     * @param index   index
     * @param builder ????????????
     * @param c       ???????????????
     * @return java.util.ArrayList
     * @author fanxb
     * @date 2019/7/25 13:46
     */
    public <T> List<T> search(String index, SearchSourceBuilder builder, Class<T> c) {
        SearchRequest request = new SearchRequest(index);
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<T> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                res.add(JSON.parseObject(hit.getSourceAsString(), c));
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: ??????index
     *
     * @param index index
     * @return void
     * @author fanxb
     * @date 2019/7/26 11:30
     */
    public void deleteIndex(String index) {
        try {
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: delete by query
     *
     * @param index   index
     * @param builder builder
     * @author fanxb
     * @date 2019/7/26 15:16
     */
    public void deleteByQuery(String index, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(builder);
        //????????????????????????,?????????10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
