package com.ceir.CEIRPostman.SpecificationBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.domain.Specification;

import com.ceir.CEIRPostman.constants.Datatype;
import com.ceir.CEIRPostman.constants.SearchOperation;
import com.ceir.CEIRPostman.model.SearchCriteria;
import com.ceir.CEIRPostman.model.SearchCriteria;
import com.ceir.CEIRPostman.model.User;
import com.ceir.CEIRPostman.model.UserProfile;
import com.ceir.CEIRPostman.model.Usertype;
import com.ceir.CEIRPostman.util.DbFunctions;


public class GenericSpecificationBuilder<T> {
	private static final Logger logger = LogManager.getLogger(GenericSpecificationBuilder.class);

	private final List<SearchCriteria> params;
	private final List<SearchCriteria> searchParams;
	private final List<SearchCriteria> searchParamsUsertype;
	private final List<SearchCriteria> paramsFeatureUsertype;
	private final List<SearchCriteria> searchParamsUser;
	private final List<SearchCriteria> searchParamsFeature;
	private final String dialect;
	private List<Specification<T>> specifications;

	public GenericSpecificationBuilder(String dialect) {
		params = new ArrayList<>();
		searchParams = new ArrayList<>();
		searchParamsUsertype = new ArrayList<>();
		searchParamsUser = new ArrayList<>();
		paramsFeatureUsertype=new ArrayList<>();
		searchParamsFeature=new ArrayList<>();		
		specifications = new LinkedList<>();
		this.dialect = dialect;
	}

	public final GenericSpecificationBuilder<T> with(SearchCriteria criteria) { 
		params.add(criteria);
		return this;
	}

	public final GenericSpecificationBuilder<T> orSearch(SearchCriteria criteria) { 
		searchParams.add(criteria);
		return this;
	}

	public final GenericSpecificationBuilder<T> orSearchUsertype(SearchCriteria criteria) { 
		searchParamsUsertype.add(criteria);
		return this;
	}

	public final GenericSpecificationBuilder<T> orSearchUser(SearchCriteria criteria) { 
		searchParamsUser.add(criteria);
		return this;
	}
	
	public final GenericSpecificationBuilder<T> orSearchUsertypeMapToFeature(SearchCriteria criteria) { 
		paramsFeatureUsertype.add(criteria);
		return this;
	}
	public final GenericSpecificationBuilder<T> orSearchFeature(SearchCriteria criteria) { 
		searchParamsFeature.add(criteria);
		return this;
	}

	public Specification<T> build() { 
		logger.info("inside build method");
		// convert each of SearchCriteria params to Specification and construct combined specification based on custom rules.
		int j = 0;
		Specification<T> finalSpecification   = null;
		Specification<T> searchSpecification  = null;
		/** Specification added from addSpecification method**/
		logger.info("specifications=   "+specifications);
		logger.info("specifications size: "+specifications.size());
		if(!specifications.isEmpty()) {
			finalSpecification = Specification.where(specifications.get(0));
			for(int i = 1; i<specifications.size() ;i++) {
				finalSpecification = finalSpecification.and(specifications.get(i));
			}
		}
		/***If params list not empty***/
		logger.info("params =  "+params);
		specifications = createSpecifications( params );
		if(!specifications.isEmpty()) {
			if( finalSpecification != null ) {
				j = 0;
			}else {//If no call of addSpecification method
				j = 1;
				finalSpecification = Specification.where(specifications.get(0));
			}
			for(int i = j; i<specifications.size() ;i++) {
				finalSpecification = finalSpecification.and(specifications.get(i));
			}
		}
		/***If searchParams list not empty***/
		specifications = createSpecifications( searchParams );
		if( !specifications.isEmpty()) {
			searchSpecification = specifications.get(0);
			for(int i = 1; i<specifications.size() ;i++) {
				searchSpecification = searchSpecification.or(specifications.get(i));
			}
			if( finalSpecification != null ) {
				finalSpecification = finalSpecification.and( searchSpecification );
			}else {//If no call of addSpecification method
				finalSpecification = Specification.where(searchSpecification);
			}
		}
		
		/***If searchParams list not empty***/
		specifications = joinWithUserOr(searchParamsUser);
		if( !specifications.isEmpty()) {
			searchSpecification = specifications.get(0);
			for(int i = 1; i<specifications.size() ;i++) {
				searchSpecification = searchSpecification.or(specifications.get(i));
			}
			if( finalSpecification != null ) {
				finalSpecification = finalSpecification.and( searchSpecification );
			}else {//If no call of addSpecification method and empty params 
				finalSpecification = Specification.where(searchSpecification);
			}
		}
		
		
		/***If searchParams list not empty***/
		specifications = joinWithUserTypeOr(searchParamsUsertype);
		if( !specifications.isEmpty()) {
			searchSpecification = specifications.get(0);
			for(int i = 1; i<specifications.size() ;i++) {
				searchSpecification = searchSpecification.or(specifications.get(i));
			}
			if( finalSpecification != null ) {
				finalSpecification = finalSpecification.and( searchSpecification );
			}else {//If no call of addSpecification method and empty params 
				finalSpecification = Specification.where(searchSpecification);
			}
		}

		
		return finalSpecification;
	}

	public void addSpecification(Specification<T> specification) { 
		specifications.add(specification);
	}

	private List<Specification<T>> createSpecifications( List<SearchCriteria> criterias){
		List<Specification<T>> specifications = new ArrayList<Specification<T>>();
		try {
			for(SearchCriteria searchCriteria : criterias) {
				specifications.add((root, query, cb)-> {
					// Path<Tuple> tuple = root.<Tuple>get(searchCriteria);
					if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
							&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
						return cb.greaterThan(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
							&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
						return cb.lessThan(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.INT.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()), (int)searchCriteria.getValue());
					} 
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.INTEGER.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()),searchCriteria.getValue());
					} 
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
					}
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.DOUBLE.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()), (Double)searchCriteria.getValue());
					}
					else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
							&& Datatype.DOUBLE.equals(searchCriteria.getDatatype())) {
						return cb.equal(root.get(searchCriteria.getKey()), (Double)searchCriteria.getValue());
					}
					else if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
							&& Datatype.DATE.equals(searchCriteria.getDatatype())){
						Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, root.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
						return cb.greaterThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
							&& Datatype.DATE.equals(searchCriteria.getDatatype())){
						Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, root.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
						return cb.lessThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
							&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
						return cb.notEqual(root.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
					}
					else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
							&& Datatype.INT.equals(searchCriteria.getDatatype())) {
						return cb.notEqual(root.get(searchCriteria.getKey()), (Integer)searchCriteria.getValue());
					}else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
							&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
						return cb.notEqual(root.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
					}
					else if(SearchOperation.LIKE.equals(searchCriteria.getSearchOperation())
							&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
						return cb.like(cb.lower(root.get(searchCriteria.getKey()).as( String.class )), "%"+((String)searchCriteria.getValue()).toLowerCase()+"%");
					}
					else if(SearchOperation.LIKE.equals(searchCriteria.getSearchOperation())
							&& Datatype.DOUBLE.equals(searchCriteria.getDatatype())) {
						return cb.like(root.get(searchCriteria.getKey()), "%"+(Double.parseDouble(searchCriteria.getValue().toString()))+"%");
					}
						else {
						return null;
					}
				});
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return specifications;
	}


	public Specification<UserProfile> joinWithUser(SearchCriteria searchCriteria){
		logger.info("inside join with user and data is: "+searchCriteria);
		return (root, query, cb) -> { 
			Join<UserProfile, User> user = root.join("user".intern());
			if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.greaterThan(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.lessThan(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.INT.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), (int)searchCriteria.getValue());
			} 
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.INTEGER.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()),searchCriteria.getValue());
			} 
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
			}
			else if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.DATE.equals(searchCriteria.getDatatype())){
				Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, user.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
				return cb.greaterThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.DATE.equals(searchCriteria.getDatatype())){
				Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, user.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
				return cb.lessThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.INT.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), (Integer)searchCriteria.getValue());
			}else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
			}else {
				return null;
			}

		};
	}

	public Specification<User> joinWithUserType(SearchCriteria searchCriteria){
		logger.info("inside join with usertype and data is: "+searchCriteria);
		return (root, query, cb) -> { 
			Join<User, Usertype> user = root.join("usertype".intern());
			if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.greaterThan(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.lessThan(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.INT.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), (int)searchCriteria.getValue());
			} 
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.INTEGER.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()),searchCriteria.getValue());
			} 
			else if(SearchOperation.EQUALITY.equals(searchCriteria.getSearchOperation())
					&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
				return cb.equal(user.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
			}
			else if(SearchOperation.GREATER_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.DATE.equals(searchCriteria.getDatatype())){
				Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, user.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
				return cb.greaterThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.LESS_THAN.equals(searchCriteria.getSearchOperation())
					&& Datatype.DATE.equals(searchCriteria.getDatatype())){
				Expression<String> dateStringExpr = cb.function(DbFunctions.getDate(dialect), String.class, user.get(searchCriteria.getKey()), cb.literal(DbFunctions.getDateFormat(dialect)));
				return cb.lessThanOrEqualTo(cb.lower(dateStringExpr), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
			}
			else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.INT.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), (Integer)searchCriteria.getValue());
			}else if(SearchOperation.NEGATION.equals(searchCriteria.getSearchOperation())
					&& Datatype.LONG.equals(searchCriteria.getDatatype())) {
				return cb.notEqual(user.get(searchCriteria.getKey()), (Long)searchCriteria.getValue());
			}else {
				return null;
			}

		};
	}
	public Specification<UserProfile> joinWithMultiple(SearchCriteria searchCriteria){
		return (root, query, cb) -> {
			Join<UserProfile, User> addresses = root.join("user".intern());
			Join<User, Usertype> userdetails = addresses.join("usertype".intern());	
			return cb.equal(userdetails.get(searchCriteria.getKey()), searchCriteria.getValue().toString());
		}; 
	}

		private List<Specification<T>> joinWithUserOr( List<SearchCriteria> criterias){
			List<Specification<T>> specifications = new ArrayList<Specification<T>>();
			try {
				for(SearchCriteria searchCriteria : criterias) {
					specifications.add((root, query, cb)-> {
		logger.info("inside join with user and data is: "+searchCriteria);

			Join<UserProfile, User> user = root.join("user".intern());
			if(SearchOperation.LIKE.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.like(user.get(searchCriteria.getKey()), "%"+(String)searchCriteria.getValue()+"%");
			}
			else {
				return null;
			}

					});
				}
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return specifications;
		}
		
		private List<Specification<T>> joinWithUserTypeOr( List<SearchCriteria> criterias){
			List<Specification<T>> specifications = new ArrayList<Specification<T>>();
			try {
				for(SearchCriteria searchCriteria : criterias) {
					specifications.add((root, query, cb)-> {
		logger.info("inside join with user and data is: "+searchCriteria);
		Join<UserProfile, User> addresses = root.join("user".intern());
		Join<User, Usertype> usertypedetails = addresses.join("usertype".intern());	
			if(SearchOperation.LIKE.equals(searchCriteria.getSearchOperation())
					&& Datatype.STRING.equals(searchCriteria.getDatatype())) {
				return cb.like(usertypedetails.get(searchCriteria.getKey()), "%"+(String)searchCriteria.getValue()+"%");
			}
			else {
				return null;
			}

					});
				}
			}catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return specifications;
		}
		
		
			public Specification<T>  inQuery(String key,List<Integer> status){
			return (root, query, cb) -> {
				logger.info("In query save ");
				Join<UserProfile, User> user = root.join("user".intern());
			//	Join<User, Userrole> user = users.join("userRole".intern());
				//return cb.in(user.get(key)).value(status);
				return user.get(key).in(status);
			};
		}
		
//		public Specification<T> in(SearchCriteria searchCriteria, ArrayList<Long> status){
//			return (root, query, cb) -> {
//				logger.info("inside In query save ");
//				logger.info("key= "+searchCriteria.getKey());
//					//return cb.in(root.get(searchCriteria.getKey())).value(status);
//					return root.get(searchCriteria.getKey()).in(status);
//			};
//		}
		
		public Specification<T>  in(String key,List<Long> status){
			return (root, query, cb) -> {
				logger.info("In query save ");
				//Join<UserProfile, User> user = root.join("user".intern());
			//	Join<User, Userrole> user = users.join("userRole".intern());
				//return cb.in(user.get(key)).value(status);
				return root.get(key).in(status);
			};
		}
	
	
}

