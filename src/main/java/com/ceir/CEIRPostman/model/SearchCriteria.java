package com.ceir.CEIRPostman.model;

import com.ceir.CEIRPostman.constants.Datatype;
import com.ceir.CEIRPostman.constants.SearchOperation;

public class SearchCriteria {

	private String key;
	private Object value;
	private SearchOperation searchOperation;
	private Datatype datatype;
	
	public SearchCriteria(String key, Object value, SearchOperation searchOperation, Datatype datatype) {
		this.key = key;
		this.value = value;
		this.searchOperation = searchOperation;
		this.datatype = datatype;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public SearchOperation getSearchOperation() {
		return searchOperation;
	}
	public void setSearchOperation(SearchOperation searchOperation) {
		this.searchOperation = searchOperation;
	}
	public Datatype getDatatype() {
		return datatype;
	}
	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchCriteria [key=").append(key).append(", value=").append(value).append(", searchOperation=")
				.append(searchOperation).append(", datatype=").append(datatype).append("]");
		return builder.toString();
	}
	
}
