package org.rainbow.test.service;

import org.rainbow.test.model.Person;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author K
 * @date 2021/3/17  14:23
 */
public interface PersonService {

    /**
     * create Index
     *
     * @param index elasticsearch index name
     */
    void createIndex(String index);

    /**
     * delete Index
     *
     * @param index elasticsearch index name
     */
    void deleteIndex(String index);

    /**
     * insert document source
     *
     * @param index elasticsearch index name
     * @param list  data source
     */
    void insert(String index, List<Person> list);

    /**
     * update document source
     *
     * @param index elasticsearch index name
     * @param list  data source
     */
    void update(String index, List<Person> list);

    /**
     * delete document source
     *
     * @param person delete data source and allow null object
     */
    void delete(String index, @Nullable Person person);

    /**
     * search all doc records
     *
     * @param index elasticsearch index name
     * @return person list
     */
    List<Person> searchList(String index);

    /**
     * 模糊查询
     *
     * @param index elasticsearch index name
     * @param condition 条件
     * @return person list
     */
    List<Person> fuzzySearchList(String index, Person condition);
}
