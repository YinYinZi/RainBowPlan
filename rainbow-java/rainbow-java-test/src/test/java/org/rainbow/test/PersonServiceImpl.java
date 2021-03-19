package org.rainbow.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.test.model.Person;
import org.rainbow.test.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author K
 * @date 2021/3/17  14:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonServiceImpl {

    @Autowired
    private PersonService personService;

    /**
     * 测试删除索引
     */
    @Test
    public void deleteIndexTest() {
        personService.deleteIndex("person");
    }

    /**
     * 测试创建索引
     */
    @Test
    public void createIndexTest() {
        personService.createIndex("person");
    }

    @Test
    public void insertTest() {
        List<Person> list = new ArrayList<>();
        list.add(Person.builder().age(11).birthday(new Date()).country("US").id(1L).name("男子偷上万元发红包求交女友 被抓获时仍然单身").remark("test1").build());
        list.add(Person.builder().age(22).birthday(new Date()).country("CN").id(2L).name("16岁少女为结婚“变”22岁 7年后想离婚被法院拒绝").remark("test1").build());
        list.add(Person.builder().age(33).birthday(new Date()).country("US").id(3L).name("深圳女孩骑车逆行撞奔驰 遭索赔被吓哭(图)").remark("test1").build());
        list.add(Person.builder().age(44).birthday(new Date()).country("CN").id(4L).name("为什么国内的街道招牌用的都是红黄配").remark("test1").build());
        list.add(Person.builder().age(55).birthday(new Date()).country("US").id(5L).name("女人对护肤品比对男票好？网友神怼").remark("test2").build());
        //list.add(Person.builder().age(33).birthday(new Date()).country("ID").id(6L).name("呵呵").remark("test3").build());

        personService.insert("person", list);
    }

    @Test
    public void updateTest() {
        Person person = Person.builder().age(33).birthday(new Date()).country("ID_update").id(3L).name("深圳女孩骑车逆行撞奔驰 遭索赔被吓哭(图)").remark("").build();
        List<Person> list = new ArrayList<>();
        list.add(person);
        personService.update("person", list);
    }

    @Test
    public void deleteTest() {
        personService.delete("person", Person.builder().id(2L).build());
    }

    @Test
    public void searchListTest() {
        List<Person> personList = personService.searchList("person");
        System.out.println(personList);
    }

    @Test
    public void fuzzySearchListTest() {
        Person condition = new Person();
        condition.setName("16岁少女结婚好还是单身好？");
        List<Person> personList = personService.fuzzySearchList("person", condition);
        System.out.println(personList);
    }

}
