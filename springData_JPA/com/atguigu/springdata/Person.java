package com.atguigu.springdata;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;


/**
 * 1.持久化类，测试EntityManager是否在Spring配置文件中配置成功。
 * 
 * 
 */

@Entity
@Table(name="JPA_PERSON")
public class Person {

	private Integer id;
	private String lastName;

	private String email;
	private Date birth;

	
	
	private Address address;  //支持属性的级联查询. 若当前类有符合条件的属性, 则优先使用, 而不使用级联属性. 
	
	private Integer addressId;  //测试PersonRepository中。。getByAddressIdGreaterThan，getByAddress_IdGreaterThan

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}


	
	//多个pserson 对应一个地址
	@JoinColumn(name="address_id")
	@ManyToOne  
	public Address getAddress() {
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
	}
	
	
	public void setId(Integer id) {
		this.id = id;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public Date getBirth() {
		return birth;
	}


	public void setBirth(Date birth) {
		this.birth = birth;
	}


	@Column(name="ADD_ID")
	public Integer getAddressId() {
		return addressId;
	}


	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}


	@Override
	public String toString() {
		return "Person [id=" + id + ", lastName=" + lastName + ", email=" + email + ", birth=" + birth + ", addressId="
				+ addressId + "]";
	}



	
	
	
}
