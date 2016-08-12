package com.atguigu.springdata;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




/**
 * 1.applicationContext.xml 中   ，5.配置SpringData ,编写PersonRepsotory接口，只需要声明一个接口，一个方法就可以。
 *
 * 2.  Repository 是一个空接口. 即是一个标记接口，
 * 		若我们定义的接口继承了 Repository, 则该接口会被 IOC 容器识别为一个 Repository Bean.纳入到 IOC 容器中. 进而可以在该接口中定义满足一定规范的方法. 
 * 		接口纳入Spring-IOC 容器中，其实际是Spring会帮助提供实现类。Spring中获取的Bean实际上是一个代理
 * 3.  实际上, 也可以通过 @RepositoryDefinition 注解来替代继承 Repository 接口
 */



/**
	基础的 Repository 提供了最基本的数据访问功能，其几个子接口则扩展了一些功能。它们的继承关系如下： 
	Repository： 仅仅是一个标识，表明任何继承它的均为仓库接口类
	CrudRepository： 继承 Repository，实现了一组 CRUD 相关的方法 
	PagingAndSortingRepository： 继承 CrudRepository，实现了一组分页排序相关的方法 
	JpaRepository： 继承 PagingAndSortingRepository，实现一组 JPA 规范相关的方法 
	自定义的 XxxxRepository 需要继承 JpaRepository，这样的 XxxxRepository 接口就具备了通用的数据访问控制层的能力。
	JpaSpecificationExecutor： 不属于Repository体系，实现一组 JPA Criteria 查询相关的方法 

 */



/**
 * 在 Repository 子接口中声明方法
 * 1. 不是随便声明的. 而需要符合一定的规范,有关键字(很麻烦，使用JPQL更好。)
 * 2. 查询方法以 find | read | get 开头
 * 3. 涉及条件查询时，条件的属性用条件关键字连接
 * 4. 要注意的是：条件属性以首字母大写。
 * 5. 支持属性的级联查询. 若当前类有符合条件的属性, 则优先使用, 而不使用级联属性. Persion->addressId;   
 *    若需要使用级联属性, 则属性之间使用 _ 进行连接. Persion,Address.id
 */


/**
 * 使用关键字的查询：简单，名字会比较长，带子查询的复杂sql无法做到，所以就该使用JPQL,在@Query()
 *
 */

//@RepositoryDefinition(domainClass=Person.class,idClass=Integer.class)
public interface PersonRepository   extends  JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person>, PersonDao//  extends JpaRepository<Person, Integer>,JpaSpecificationExecutor<Person>      // extends PagingAndSortingRepository<Person, Integer>   // extends CrudRepository<Person, Integer>   // extends Repository<Person	, Integer> 
{
	//测试方法，先手动在数据表中插入一条数据,获取
	//根据lastName来获取对应的Person
	//编写测试类方法
	Person getByLastName(String lastName);
	
	
	
	//Where lastName like ?% and id<?  ，编写测试方法
	List<Person> getByLastNameStartingWithAndIdLessThan(String lastName,Integer id);
	
	
	//Where lastName like ?% and id<?  ，编写测试方法
	List<Person> getByLastNameEndingWithAndIdLessThan(String lastName,Integer id);
	
	
	
	//WHERE email IN (?, ?, ?) AND birth < ?
	List<Person> getByEmailInAndBirthLessThan(List<String> emails, Date birth);
	
	
	
	//WHERE person.address.id > ?   级联属性,左外连接，级联查询,当Person中有重复的addressId属性，不加下划线查的是   where  person0_.ADD_ID>?
	List<Person> getByAddressIdGreaterThan(Integer id);
	
	//WHERE person.address.id > ?   级联属性,左外连接，级联查询
	List<Person> getByAddress_IdGreaterThan(Integer id);
	
	
	
	
	//Spring关键字无法子查询
	//查询 id 值最大的那个 Person
	//使用 @Query 注解可以自定义 JPQL 语句以实现更灵活的查询
	@Query("SELECT p FROM Person p WHERE p.id = (SELECT max(p2.id) FROM Person p2)")
	Person getMaxIdPerson();
	
	
	

	//为 @Query 注解传递参数的方式1: 使用占位符. 
	@Query("SELECT p FROM Person p WHERE p.lastName = ?1 AND p.email = ?2")
	List<Person> testQueryAnnotationParams1(String lastName, String email);
	
	

	
	
	
	//为 @Query 注解传递参数的方式2: 命名参数的方式. 
	@Query("SELECT p FROM Person p WHERE p.lastName = :lastName AND p.email = :email")
	List<Person> testQueryAnnotationParams2(@Param("email") String email, @Param("lastName") String lastName);
	
	
	
	//SpringData 允许在占位符上添加 %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %?1% OR p.email LIKE %?2%")
	List<Person> testQueryAnnotationLikeParam(String lastName, String email);
	
	
	
	//SpringData 允许在命名参数上添加 %%. 
	@Query("SELECT p FROM Person p WHERE p.lastName LIKE %:lastName% OR p.email LIKE %:email%")
	List<Person> testQueryAnnotationLikeParam2(@Param("email") String email, @Param("lastName") String lastName);
	
	
	
	
	

	//设置 nativeQuery=true 即可以使用原生的 SQL 查询
	@Query(value="SELECT count(id) FROM JPA_PERSON", nativeQuery=true)
	long getTotalCount();
	
	
	
	
	//-------------------------------------------------------------------------
	
	//可以通过自定义的@Query(SQL) 的 JPQL 完成 UPDATE 和 DELETE 操作. 但不能INSERT
	//在 @Query 注解中编写 JPQL 语句, 但必须使用 @Modifying 进行修饰. 以通知 SpringData, 这是一个 UPDATE 或 DELETE 操作
	//UPDATE 或 DELETE 操作需要使用事务, 此时需要定义 Service 层. 在 Service 层的方法上添加事务操作. @Transactional  
	//默认情况下, SpringData 的每个方法上有事务, 但都是一个只读事务. 他们不能完成修改操作!
	@Modifying
	@Query("UPDATE Person p SET p.email = :email WHERE id = :id")
	void updatePersonEmail(@Param("email") String email,@Param("id") Integer id);
	
	
	
	
	//--------------------------------------CrudRepository---  详见SpringDataTest.--------------------------------
		//测试CrudRepository  - 修改类继承于 extends CrudRepository<Person, Integer> 
		//测试  CrudRepository.save方法
	   //自带 <S extends T> Iterable<S> save(Iterable<S> entities);
		
	
	
	
	//------------------------------------------------------------------------------------
			// 测试简单分页， extends PagingAndSortingRepository<Person, Integer>  
			
			//测试   extends JpaRepository<Person, Integer> ，saveAndFlush()
	
			//测试   extends JpaRepository<Person, Integer>,JpaSpecificationExecutor<Person>    ,完成带查询条件的查询分页
	
			//测试自定义 Repository,既添加自自定义方法，需要PersionRepository继承自定义的接口PersionDao,并提供同名的实现类，PersionRepositoryImpl
}


























