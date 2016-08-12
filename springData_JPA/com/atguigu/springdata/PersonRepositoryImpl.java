package com.atguigu.springdata;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.atguigu.springdata.Person;
import com.atguigu.springdata.PersonDao;


//测试自定义 Repository,既添加自自定义方法，需要PersionRepository继承自定义的接口PersionDao,并提供同名的实现类，PersionRepositoryImpl
public class PersonRepositoryImpl implements PersonDao {

	@PersistenceContext
	private EntityManager entityManager;    //可以使用原生的JPA
	
	@Override
	public void test() {
		Person person = entityManager.find(Person.class, 168);
		System.out.println("-->" + person);
	}

}
