package org.rainbow.test.service.impl;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.rainbow.elasticsearch.service.base.BaseElasticsearchService;
import org.rainbow.test.model.Person;
import org.rainbow.test.service.PersonService;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author K
 * @date 2021/3/17  14:25
 */
@Service
public class PersonServiceImpl extends BaseElasticsearchService<Person> implements PersonService {

    @Override
    public void createIndex(String index) {
        createIndexRequest(index, Person.class);
    }

    @Override
    public void deleteIndex(String index) {
        deleteIndexRequest(index);
    }

    @Override
    public void insert(String index, List<Person> list) {
        try {
            list.forEach(person -> {
                IndexRequest request = buildIndexRequest(index, String.valueOf(person.getId()), person);
                try {
                    client.index(request, COMMON_OPTIONS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(String index, List<Person> list) {
        list.forEach(person -> {
            updateRequest(index, String.valueOf(person.getId()), person);
        });
    }

    @Override
    public void delete(String index, @Nullable Person person) {
        if (ObjectUtils.isEmpty(person)) {
            // 如果person 对象为空，则删除全量
            searchList(index).forEach(p -> {
                deleteRequest(index, String.valueOf(p.getId()));
            });
        } else {
            deleteRequest(index, String.valueOf(person.getId()));
        }
    }

    @Override
    public List<Person> searchList(String index) {
        SearchResponse searchResponse = search(index);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Person> personList = new ArrayList<>();
        Arrays.asList(hits).forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Person person = BeanUtil.mapToBean(sourceAsMap, Person.class, true);
            personList.add(person);
        });
        return personList;
    }

    @Override
    public List<Person> fuzzySearchList(String index, Person condition) {
        List<Person> personList = new ArrayList<>();
        MultiSearchResponse response = fuzzySearch(index, condition);
        response.forEach(r -> {
            SearchResponse resp = r.getResponse();
            Arrays.asList(resp.getHits().getHits()).forEach(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Person person = BeanUtil.mapToBean(sourceAsMap, Person.class, true);
                personList.add(person);
            });
        });
        return personList;
    }
}
