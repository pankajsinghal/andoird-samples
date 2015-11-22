package com.example.contactseditor.database.table;

public class CountryCode {
	private int id;
	private String iso2;
	private String iso3;
	private String countryName;
	private String dailingCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIso2() {
		return iso2;
	}

	public void setIso2(String iso2) {
		this.iso2 = iso2;
	}

	public String getIso3() {
		return iso3;
	}

	public void setIso3(String iso3) {
		this.iso3 = iso3;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getDailingCode() {
		return dailingCode;
	}

	public void setDailingCode(String dailingCode) {
		this.dailingCode = dailingCode;
	}

	public String getValues(){

		return id+" , "+iso2+" , "+iso3+" , "+countryName+" , "+dailingCode;
	}
	@Override
	public String toString() {
		return getCountryName()+" ("+getIso2()+"/"+getIso3()+") : "+getDailingCode();
	}
	
}
