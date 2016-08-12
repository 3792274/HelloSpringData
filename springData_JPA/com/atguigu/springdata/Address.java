package com.atguigu.springdata;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="JPA_ADDRESSES")
@Entity
public class Address {

	private Integer id;
	private String province;
	private String city;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Address(String province, String city) {
		super();
		this.province = province;
		this.city = city;
	}

	public Address() {
		super();
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", province=" + province + ", city=" + city + "]";
	}

	
	
	
}
