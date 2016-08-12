package com.atguigu.springdata;


//测试自定义 Repository,既添加自自定义方法，需要PersionRepository继承自定义的接口PersionDao,并提供同名的实现类，PersionRepositoryImpl
public interface PersonDao {
	
	 void test();
}
