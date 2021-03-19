package org.rainbow.elasticsearch.service.base;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.ParseContext;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.rainbow.elasticsearch.annotation.EsField;
import org.rainbow.elasticsearch.config.ElasticsearchProperties;
import org.rainbow.elasticsearch.enums.EsFieldType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author K
 * @date 2021/3/17  11:56
 */
@Slf4j
public abstract class BaseElasticsearchService<V> {

    @Resource
    protected RestHighLevelClient client;

    @Resource
    private ElasticsearchProperties elasticsearchProperties;

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引
     */
    protected boolean indexExists(String index) {
        boolean exists = false;
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        try {
            exists = client.indices().exists(getIndexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 异步创建Elasticsearch索引
     *
     * @param index Elasticsearch索引
     */
    protected void createIndexRequest(String index, Class<V> clazz) {
        try {
            CreateIndexRequest request = new CreateIndexRequest(index);
            request.settings(Settings.builder().put("index.number_of_shards", elasticsearchProperties.getIndex().getNumberOfShards())
                    .put("index.number_of_replicas", elasticsearchProperties.getIndex().getNumberOfReplicas()));
            request.mapping(generateBuilder(clazz));
            CreateIndexResponse createIndexResponse = client.indices().create(request, COMMON_OPTIONS);

            log.info(" whether all of the nodes have acknowledged the request : {}", createIndexResponse.isAcknowledged());
            log.info(" Indicates whether the requisite number of shard copies were started for each shard in the index before timing out :{}", createIndexResponse.isShardsAcknowledged());
        } catch (IOException e) {
            throw new ElasticsearchException("创建索引 {" + index + "} 失败");
        }
    }

    /**
     * 构建索引映射
     *
     * @param clazz 要构建索引的类
     * @return {@link XContentBuilder}
     */
    public XContentBuilder generateBuilder(Class clazz) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        builder.startObject("properties");
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            if (f.isAnnotationPresent(EsField.class)) {
                EsField declaredAnnotation = f.getDeclaredAnnotation(EsField.class);
                if (declaredAnnotation.type() == EsFieldType.OBJECT) {
                    Class<?> type = f.getType();
                    Field[] df2 = type.getDeclaredFields();
                    builder.startObject(f.getName());
                    builder.startObject("properties");
                    // 遍历该对象中的所有属性
                    for (Field f2 : df2) {
                        if (f2.isAnnotationPresent(EsField.class)) {
                            // 获取注解
                            EsField declaredAnnotation2 = f2.getDeclaredAnnotation(EsField.class);
                            builder.startObject(f2.getName());
                            builder.field("type", declaredAnnotation2.type().getType());
                            if (declaredAnnotation2.type() == EsFieldType.TEXT) {
                                builder.field("analyzer", declaredAnnotation2.analyzer().getType());
                            }
                            builder.endObject();
                        }
                    }
                    builder.endObject();
                    builder.endObject();
                } else {
                    builder.startObject(f.getName());
                    builder.field("type", declaredAnnotation.type().getType());
                    if (declaredAnnotation.type() == EsFieldType.TEXT) {
                        builder.field("analyzer", declaredAnnotation.analyzer().getType());
                    }
                    builder.endObject();
                }
            }
        }
        builder.endObject();
        builder.endObject();
        return builder;
    }

    /**
     * 删除 elasticsearch 索引
     *
     * @param index elasticsearch 索引
     */
    protected void deleteIndexRequest(String index) {
        DeleteIndexRequest deleteIndexRequest = buildDeleteIndexRequest(index);
        try {
            client.indices().delete(deleteIndexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + index + "} 失败");
        }
    }

    /**
     * 构建删除索引
     *
     * @param index elasticsearch 索引
     * @return 删除索引
     */
    private static DeleteIndexRequest buildDeleteIndexRequest(String index) {
        return new DeleteIndexRequest(index);
    }

    /**
     * 构建索引请求
     *
     * @param index  elasticsearch索引名
     * @param id     请求对象id
     * @param object 请求对象
     * @return {@link org.elasticsearch.action.index.IndexRequest}
     */
    protected static IndexRequest buildIndexRequest(String index, String id, Object object) {
        return new IndexRequest(index).id(id).source(BeanUtil.beanToMap(object), XContentType.JSON);
    }

    /**
     * 执行修改请求
     *
     * @param index  elasticsearch索引名
     * @param id     Document id
     * @param object 请求对象
     */
    protected void updateRequest(String index, String id, Object object) {
        UpdateRequest updateRequest = new UpdateRequest(index, id).doc(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            client.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException("更新索引 {" + index + "} 数据 {" + object + "} 失败");
        }
    }

    /**
     * 执行删除请求
     *
     * @param index elasticsearch index name
     * @param id    Document id
     */
    protected void deleteRequest(String index, String id) {
        try {
            DeleteRequest deleteRequest = new DeleteRequest(index, id);
            client.delete(deleteRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            throw new ElasticsearchException("删除索引 {" + index + "} 数据id {" + id + "} 失败");
        }
    }

    /**
     * 查询所有
     *
     * @param index elasticsearch index name
     * @return {@link SearchResponse}
     */
    protected SearchResponse search(String index) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = client.search(searchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

    /**
     * 模糊查询
     *
     * @param index     elasticsearch index name
     * @param condition 条件对象
     * @return {@link MultiSearchResponse}
     */
    protected MultiSearchResponse fuzzySearch(String index, V condition) {
        MultiSearchResponse response = null;
        MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
        Map<String, Object> conditionMap = BeanUtil.beanToMap(condition, false, true);
        if (CollectionUtil.isNotEmpty(conditionMap)) {
            conditionMap.forEach((k, v) -> {
                SearchRequest searchRequest = new SearchRequest();
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchQuery(k, v));
                searchRequest.source(searchSourceBuilder);
                searchRequest.indices(index);
                multiSearchRequest.add(searchRequest);
            });
        }
        try {
            response = client.msearch(multiSearchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
