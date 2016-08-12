package com.atguigu.springdata;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//需要applicationContext.xml中配置自动扫描的包,纳入ICO容器
@Service   
public class PersonService {
	
	@Autowired
	PersonRepository personRepository;

	
	@Transactional  //测试PersonRespository中update方法，当使用@Modifying注解后还出错，提示需要事务，故在此添加事务支持。
	public void updatePersonEmail(String email,Integer id){
		System.out.println(personRepository);
		personRepository.updatePersonEmail(email,id);
	}
	
	
	
	
	//测试 PersonRepository,继承CrudRepsoitory.save()
	@Transactional
	public void savePersons(List<Person> persons){
		personRepository.save(persons);
	}
	
	
	
	
}
