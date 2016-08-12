package com.atguigu.springdata.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

import com.atguigu.springdata.Person;
import com.atguigu.springdata.PersonRepository;
import com.atguigu.springdata.PersonService;

public class SpringDataTest {
	
	private ApplicationContext ctx = null;
	PersonRepository personRepository = null;
	private PersonService personService = null;
	
	
	{
		ctx= new ClassPathXmlApplicationContext("applicationContext.xml");
		personRepository = ctx.getBean(PersonRepository.class);
		personService = ctx.getBean(PersonService.class);
	}

	
	
	/*测试dataSources*/
	@Test
	public void testDataSources() throws SQLException {
		DataSource dataSource = ctx.getBean(DataSource.class);
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
	}

	
	
	
	/*测试EntityManagerFactory,需要建立一个持久化类，验证数据库是否生成表*/
	@Test
	public void testJpa(){
		
	}
	
	
	
	
	/*测试PersonRepository中getByLastName*/
	//方法1.PersonRepository  -》   extends Repository<Person	, Integer> 
	//方法2.PersonRepository  -》  @RepositoryDefinition(domainClass=Person.class,idClass=Integer.class)
	@Test
	public void testGetByLastName(){
		PersonRepository personRepository = ctx.getBean(PersonRepository.class);
		
		System.out.println(personRepository.getClass().getName());  //由于声明的是一个接口，具体由spring实现，所以，是个代理
		
		Person person = personRepository.getByLastName("AA");
		System.out.println(person.getBirth());
	}
	
	
	
	
	//测试PersonRepsoitory自定义的方法，使用Spring提供的关键字
	@Test
	public void testKeywords(){
		
		List<Person> persons = personRepository.getByLastNameStartingWithAndIdLessThan("BB", 3);
		System.out.println(persons);
		
		 persons = personRepository.getByLastNameEndingWithAndIdLessThan("CC", 5);
		System.out.println(persons);
		
		
		persons = personRepository.getByEmailInAndBirthLessThan(Arrays.asList("AA@atguigu.com", "FF@atguigu.com", "SS@atguigu.com"), new Date());
		System.out.println(persons.size());
	}
	
	
	
	
	
	//测试PersonRepsoitory自定义的方法，Person 中加入Address ，级联属性,左外连接，级联查询，Person中有重复的addressId情况下
		@Test
		public void testKeywords2(){
			List<Person>persons = personRepository.getByAddressIdGreaterThan(1); //不加下划线查询 的是person中的addressID
						persons = personRepository.getByAddress_IdGreaterThan(1); //加下划线，查询的是Address.id
			System.out.println(persons);
		}
		
//-------------------@Query(SQL)----------------------------------------------------------------------------------------------------		
		
		//测试 PersonRepsoitory子查询， Jpql,@Query("SQL语句"),如果Persion.addressId 为int类型则发生 将null赋值给int的错误，应该改为Integer
		@Test
		public void testQueryAnnotation(){
			Person person = personRepository.getMaxIdPerson();
			System.out.println(person);
		}
		
		
		
		
		//为 @Query 注解传递参数的方式1: 使用占位符. 
		@Test
		public void testQueryAnnotationParams1(){
			List<Person> persons = personRepository.testQueryAnnotationParams1("AA", "aa@atguigu.com");
			System.out.println(persons);
		}
		
	
		//为 @Query 注解传递参数的方式2: 命名参数的方式. 
		@Test
		public void testQueryAnnotationParams2(){
			List<Person> persons = personRepository.testQueryAnnotationParams2("aa@atguigu.com", "AA");
			System.out.println(persons);
		}
		
		
		
		
		
		//测试@Query(SQL语句)中LIKE,1.若sql没有使用%%,调用方法需要传递%，2.可以在@query()中使用%%。3在@Query中%%既可以使用在占位符上，也可以使用在命名参数上
		@Test
		public void testQueryAnnotationLikeParam(){
//			List<Person> persons = personRepository.testQueryAnnotationLikeParam("%A%", "%bb%");
//			System.out.println(persons.size());
			
//			List<Person> persons = personRepository.testQueryAnnotationLikeParam("A", "bb");
//			System.out.println(persons.size());
			
			List<Person> persons = personRepository.testQueryAnnotationLikeParam2("bb", "A");
			System.out.println(persons.size());
		}
		
		
		
		
		//使用原生的SQL查询，需要在@Query( native=true)
		@Test
		public void testNativeQuery(){
			long count = personRepository.getTotalCount();
			System.out.println(count);
		}

//-------------------@Modifying()----------------------------------------------------------------------------------------------------		
		
	
		
		
		//测试PersonRespository中updateEmail方法，
		//当使用@Modifying注解后还出错，提示需要事务，故新建PersonService,并添加事务支持。
		@Test
		public void testModifying(){
//			personRepository.updatePersonEmail("mmmm@atguigu.com",1);  //默认@Query(SQL语句)完成Select,要 UPDATE 和 DELETE 操作.需要添加@Modifying，并再Service中添加事务。不支持INSERT
			personService.updatePersonEmail("mmmm@atguigu.com", 1);
		}
		

		
		
		//测试 PersonRepository,继承CrudRepsoitory.save()
		@Test
		public void testCrudReposiory(){
			
			List<Person> persons = new ArrayList<>();
			
			for(int i = 'a'; i <= 'z'; i++){
				Person person = new Person();
				person.setAddressId(i + 1);
				person.setBirth(new Date());
				person.setEmail((char)i + "" + (char)i + "@atguigu.com");
				person.setLastName((char)i + "" + (char)i);
				
				persons.add(person);
			}
			
			personService.savePersons(persons);
		}

		
		
	//------------------------------------------------------------------------------------------------------------------------------------
		// 测试简单分页，不能完成带查询条件的分页， PersonRepository  extends PagingAndSortingRepository<Person, Integer>  
		@Test
		public void testPagingAndSortingRespository1(){
			//pageNo 从 0 开始. 
			int pageNo = 3 - 1;
			int pageSize = 5;
			//Pageable 接口通常使用的其 PageRequest 实现类. 其中封装了需要分页的信息
			
			Pageable pageable = new PageRequest(pageNo, pageSize);
			Page<Person> page = personRepository.findAll(pageable);
			
			System.out.println("总记录数: " + page.getTotalElements());
			System.out.println("当前第几页: " + (page.getNumber() + 1));
			System.out.println("总页数: " + page.getTotalPages());
			System.out.println("当前页面的 List: " + page.getContent());
			System.out.println("当前页面的记录数: " + page.getNumberOfElements());
		}
		
	 
		
		//上面的基础上，测试分页的排序
		@Test
		public void testPagingAndSortingRespository2(){
			//pageNo 从 0 开始. 
			int pageNo = 6 - 1;
			int pageSize = 5;
			//Pageable 接口通常使用的其 PageRequest 实现类. 其中封装了需要分页的信息
			//排序相关的. Sort 封装了排序的信息
			//Order 是具体针对于某一个属性进行升序还是降序. 
			Order order1 = new Order(Direction.DESC, "id");
			Order order2 = new Order(Direction.ASC, "email");
			Sort sort = new Sort(order1, order2);
			
			Pageable pageable = new PageRequest(pageNo, pageSize, sort);
			Page<Person> page = personRepository.findAll(pageable);
			
			System.out.println("总记录数: " + page.getTotalElements());
			System.out.println("当前第几页: " + (page.getNumber() + 1));
			System.out.println("总页数: " + page.getTotalPages());
			System.out.println("当前页面的 List: " + page.getContent());
			System.out.println("当前页面的记录数: " + page.getNumberOfElements());
		}
		
	 
		
	
		
		//------------------------------------------------------
			//测试 JpaRepository,要求PersonRepository继承 JpaRepository
		@Test
		public void testJpaRepository(){
			Person person = new Person();
			person.setBirth(new Date());
			person.setEmail("xy@atguigu.com");
			person.setLastName("xyz");
			//若传入的是一个游离对象, 即传入的对象有 OID. 
			//1. 若在 EntityManager 缓存中没有该对象
			//2. 若在数据库中也没有对应的记录
			//3. JPA 会创建一个新的对象, 然后把当前游离对象的属性复制到新创建的对象中
			//4. 对新创建的对象执行 insert 操作. 
			person.setId(29); //同JPA.merage()JpaTest.
			
			Person person2 = personRepository.saveAndFlush(person);
			
			System.out.println(person == person2); //不为同一个对象，有一个对象值的复制。
		}
		

		
	 
	//--------------------------------JpaSpecificationExecutor--------------------------------------------------------------------
		// 测试简单分页，能完成带查询条件的分页，  extends JpaRepository<Person, Integer>,JpaSpecificationExecutor<Person>  
		
	/**
	 * 目标: 实现带查询条件的分页. id > 5 的条件
	 * 
	 * 调用 JpaSpecificationExecutor 的     Page<T> findAll(Specification<T> spec, Pageable pageable);
	 * Specification: 封装了 JPA Criteria 查询的查询条件
	 * Pageable: 封装了请求分页的信息: 例如 pageNo, pageSize, Sort
	 */
	@Test
	public void testJpaSpecificationExecutor(){
		int pageNo = 3 - 1;
		int pageSize = 5;
		PageRequest pageable = new PageRequest(pageNo, pageSize);
		
		//通常使用 Specification 的匿名内部类
		Specification<Person> specification = new Specification<Person>() { 
			/**
			 * @param *root: 代表查询的实体类. 
			 * @param query: 可以从中可到 Root 对象, 即告知 JPA Criteria 查询要查询哪一个实体类. 还可以来添加查询条件, 还可以结合 EntityManager 对象得到最终查询的 TypedQuery 对象. 
			 * @param *cb: CriteriaBuilder 对象. 用于创建 Criteria 相关对象的工厂. 当然可以从中获取到 Predicate 对象
			 * @return: *Predicate 类型, 代表一个查询条件. 
			 */
			@Override
			public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Path path = root.get("id"); //使用要查询的实体类root导航到要查询的属性
				Predicate predicate = cb.gt(path, 5);  //创建查询条件，可以从CriteriaBuilder获取，
				return predicate;
			}
			
		};
		
		Page<Person> page = personRepository.findAll(specification, pageable);
		
		System.out.println("总记录数: " + page.getTotalElements());
		System.out.println("当前第几页: " + (page.getNumber() + 1));  //字符串相加
		System.out.println("总页数: " + page.getTotalPages());
		System.out.println("当前页面的 List: " + page.getContent());
		System.out.println("当前页面的记录数: " + page.getNumberOfElements());
	}
	


	
	/**
		//测试为某个Repository添加自定义方法，如给PersonRepository添加一个自定义方法。
		 1.自己定义个接口 ：PersonDao，添加需要的方法声明
		 2.使Repository,如PersonRepository继承刚才自己新建的接口，PersonDao
		 3.创建 PersonDao的实现类，并实现方法。PersonRepsotoryImpl implements PersonDao，注意，名字固定要在PersonRepository后面见Impl
	 * */
		@Test
		public void testCustomRepositoryMethod(){
			personRepository.test();
		}

	
		
}
