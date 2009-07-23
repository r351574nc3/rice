/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAddressInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityAffiliationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmailInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityExternalIdentifierInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPhoneInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityPrivacyPreferencesInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityPrivacyPreferencesImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.reference.dto.AddressTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmailTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityNameTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.PhoneTypeInfo;
import org.kuali.rice.kim.bo.reference.impl.AddressTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.AffiliationTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.CitizenshipStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmailTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentStatusImpl;
import org.kuali.rice.kim.bo.reference.impl.EmploymentTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EntityNameTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.EntityTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.ExternalIdentifierTypeImpl;
import org.kuali.rice.kim.bo.reference.impl.PhoneTypeImpl;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Base implementation of the identity (entity) service.  This version assumes the KimEntity
 * and related data is located within the KIM database. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityServiceImpl implements IdentityService, IdentityUpdateService {

	private BusinessObjectService businessObjectService;

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfo(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
		KimEntityImpl entity = getEntityImpl( entityId );
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo( entity );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(String principalId) {
		KimEntityImpl entity = getEntityByPrincipalId(principalId);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityDefaultInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(String principalName) {
		KimEntityImpl entity = getEntityByPrincipalName(principalName);
		if ( entity == null ) {
			return null;
		}
		return convertEntityImplToDefaultInfo(entity);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
		 Map<String,Object> criteria = new HashMap<String,Object>(3);
         criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName);
         criteria.put(KIMPropertyConstants.Principal.PASSWORD, password);
         criteria.put(KIMPropertyConstants.Principal.ACTIVE, true);
         Collection<KimPrincipalImpl> principals = (Collection<KimPrincipalImpl>)getBusinessObjectService().findMatching(KimPrincipalImpl.class, criteria);
         if (!principals.isEmpty()) {
             return new KimPrincipalInfo( principals.iterator().next() );
         }
         return null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntityDefaultInfo(Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
			Map<String,String> searchCriteria, boolean unbounded) {
		Collection baseResults = null; 
		if ( unbounded ) {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria );
		} else {
			baseResults = KNSServiceLocator.getLookupService().findCollectionBySearch( KimEntityImpl.class, searchCriteria );
		}
		List<KimEntityDefaultInfo> results = new ArrayList<KimEntityDefaultInfo>( baseResults.size() );
		for ( KimEntityImpl entity : (Collection<KimEntityImpl>)baseResults ) {
			results.add( convertEntityImplToDefaultInfo( entity ) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimEntityDefaultInfo>( results, ((CollectionIncomplete<KimEntityDefaultInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getMatchingEntityCount(java.util.Map)
	 */
	public int getMatchingEntityCount(Map<String,String> searchCriteria) {
		return getBusinessObjectService().countMatching( KimEntityImpl.class, searchCriteria );
	}
	
	protected KimEntityDefaultInfo convertEntityImplToDefaultInfo( KimEntityImpl entity ) {
		KimEntityDefaultInfo info = new KimEntityDefaultInfo();
		info.setEntityId( entity.getEntityId() );
		info.setActive( entity.isActive() );
		ArrayList<KimPrincipalInfo> principalInfo = new ArrayList<KimPrincipalInfo>( entity.getPrincipals().size() );
		info.setPrincipals( principalInfo );
		for ( KimPrincipalImpl p : entity.getPrincipals() ) {
			principalInfo.add( new KimPrincipalInfo( p ) );
		}
		KimEntityPrivacyPreferencesInfo privacy = null;
		if ( ObjectUtils.isNotNull( entity.getPrivacyPreferences() ) ) {
            privacy = new KimEntityPrivacyPreferencesInfo(entity.getPrivacyPreferences());
        }

		info.setPrivacyPreferences(privacy);
		info.setDefaultName( new KimEntityNameInfo( entity.getDefaultName() ) );
		ArrayList<KimEntityEntityTypeDefaultInfo> entityTypesInfo = new ArrayList<KimEntityEntityTypeDefaultInfo>( entity.getEntityTypes().size() );
		info.setEntityTypes( entityTypesInfo );
		for ( KimEntityEntityType entityEntityType : entity.getEntityTypes() ) {
			KimEntityEntityTypeDefaultInfo typeInfo = new KimEntityEntityTypeDefaultInfo();
			typeInfo.setEntityTypeCode( entityEntityType.getEntityTypeCode() );
			typeInfo.setDefaultAddress( new KimEntityAddressInfo( entityEntityType.getDefaultAddress()) );
			typeInfo.setDefaultEmailAddress( new KimEntityEmailInfo( entityEntityType.getDefaultEmailAddress()) );
			typeInfo.setDefaultPhoneNumber( new KimEntityPhoneInfo( entityEntityType.getDefaultPhoneNumber()) );
			entityTypesInfo.add( typeInfo );
		}
		
		ArrayList<KimEntityAffiliationInfo> affInfo = new ArrayList<KimEntityAffiliationInfo>( entity.getAffiliations().size() );
		info.setAffiliations( affInfo );
		for ( KimEntityAffiliation aff : entity.getAffiliations() ) {
			affInfo.add( new KimEntityAffiliationInfo( aff ) );
			if ( aff.isActive() && aff.isDefault() ) {
				info.setDefaultAffiliation( affInfo.get( affInfo.size() - 1 ) );
			}
		}
		info.setPrimaryEmployment( new KimEntityEmploymentInformationInfo( entity.getPrimaryEmployment() ) );
		ArrayList<KimEntityExternalIdentifierInfo> idInfo = new ArrayList<KimEntityExternalIdentifierInfo>( entity.getExternalIdentifiers().size() );
		info.setExternalIdentifiers( idInfo );
		for ( KimEntityExternalIdentifier id : entity.getExternalIdentifiers() ) {
			idInfo.add( new KimEntityExternalIdentifierInfo( id ) );
		}
		return info;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityPrivacyPreferences(java.lang.String)
	 */
	public KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, entityId);
		return new KimEntityPrivacyPreferencesInfo( (KimEntityPrivacyPreferences)getBusinessObjectService().findByPrimaryKey(KimEntityPrivacyPreferencesImpl.class, criteria) );
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipal(java.lang.String)
	 */
	public KimPrincipalInfo getPrincipal(String principalId) {
		KimPrincipalImpl principal = getPrincipalImpl( principalId );
		if ( principal == null ) {
			return null;
		}
		return new KimPrincipalInfo( getPrincipalImpl( principalId ) );
	}
	
	public KimPrincipalImpl getPrincipalImpl(String principalId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
		return (KimPrincipalImpl)getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
	}

	public KimEntityImpl getEntityImpl(String entityId) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, entityId);
        KimEntityImpl entityImpl = (KimEntityImpl)getBusinessObjectService().findByPrimaryKey(KimEntityImpl.class, criteria);
        if(entityImpl!=null)
        	entityImpl.refresh();
        return entityImpl;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntitys(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	protected List<KimEntityImpl> lookupEntitys(Map<String, String> searchCriteria) {
		return new ArrayList(KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded( KimEntityImpl.class, searchCriteria ));
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#lookupEntityIds(java.util.Map)
	 */
	public List<String> lookupEntityIds(Map<String,String> searchCriteria) {
		List<KimEntityImpl> entities = lookupEntitys( searchCriteria );
		List<String> entityIds = new ArrayList<String>( entities.size() );
		for ( KimEntityImpl entity : entities ) {
			entityIds.add( entity.getEntityId() );
		}
		return entityIds;
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalByPrincipalName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public KimPrincipalInfo getPrincipalByPrincipalName(String principalName) {
		if ( StringUtils.isBlank(principalName) ) {
			return null;
		}
		Map<String,Object> criteria = new HashMap<String,Object>(1);
        criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName.toLowerCase());
        Collection<KimPrincipalImpl> principals = (Collection<KimPrincipalImpl>)getBusinessObjectService().findMatching(KimPrincipalImpl.class, criteria);
        if (!principals.isEmpty() && principals.size() == 1) {
            return new KimPrincipalInfo( principals.iterator().next() );
        }
        return null;
    }

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalName(java.lang.String)
	 */
	protected KimEntityImpl getEntityByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
        return getEntityByKeyValue("principals." + KIMPropertyConstants.Principal.PRINCIPAL_NAME, principalName.toLowerCase());
	}

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityByPrincipalId(java.lang.String)
	 */
	protected KimEntityImpl getEntityByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
        return getEntityByKeyValue("principals." + KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityIdByPrincipalId(java.lang.String)
	 */
	public String getEntityIdByPrincipalId(String principalId) {
		if ( StringUtils.isBlank( principalId ) ) {
			return null;
		}
		KimPrincipalImpl principal = getPrincipalImpl(principalId);		
		return principal != null ? principal.getEntityId() : null;
    }

	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getEntityIdByPrincipalName(java.lang.String)
	 */
	public String getEntityIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		KimPrincipalInfo principal = getPrincipalByPrincipalName(principalName);		
		return principal != null ? principal.getEntityId() : null;
    }
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getPrincipalIdByPrincipalName(java.lang.String)
	 */
	public String getPrincipalIdByPrincipalName(String principalName) {
		if ( StringUtils.isBlank( principalName ) ) {
			return null;
		}
		KimPrincipalInfo principal = getPrincipalByPrincipalName( principalName );
		return principal != null ? principal.getPrincipalId() : null;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getDefaultNamesForEntityIds(java.util.List)
	 */
	public Map<String, KimEntityNameInfo> getDefaultNamesForEntityIds(List<String> entityIds) {
		// TODO - Use a DAO
		Map<String, KimEntityNameInfo> result = new HashMap<String, KimEntityNameInfo>(entityIds.size());
		
		for(String s : entityIds) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, s);
			criteria.put("dflt", "Y");
			
			KimEntityNameImpl name = (KimEntityNameImpl) getBusinessObjectService().findByPrimaryKey(KimEntityNameImpl.class, criteria);
			
			result.put(s, new KimEntityNameInfo( name ) );
		}
		
		return result;
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityService#getDefaultNamesForPrincipalIds(java.util.List)
	 */
	public Map<String, KimEntityNamePrincipalNameInfo> getDefaultNamesForPrincipalIds(List<String> principalIds) {
		// TODO - use a dao?
		// TODO - what if principal not found, NullPointerException
		Map<String, KimEntityNamePrincipalNameInfo> result = new HashMap<String, KimEntityNamePrincipalNameInfo>(principalIds.size());
		
		for(String s : principalIds) {
			KimEntityNamePrincipalNameInfo namePrincipal = new KimEntityNamePrincipalNameInfo();
			
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, s);
			KimPrincipalImpl principal = (KimPrincipalImpl) getBusinessObjectService().findByPrimaryKey(KimPrincipalImpl.class, criteria);
			
			namePrincipal.setPrincipalName(principal.getPrincipalName());
			
			criteria.clear();
			criteria.put(KIMPropertyConstants.Entity.ENTITY_ID, principal.getEntityId());
			criteria.put("dflt", "Y");
			KimEntityNameImpl name = (KimEntityNameImpl) getBusinessObjectService().findByPrimaryKey(KimEntityNameImpl.class, criteria);
			
			namePrincipal.setDefaultEntityName( new KimEntityNameInfo( name ) );
			
			result.put(s, namePrincipal);
		}
		
		return result;
	}
	
	/**
	 * Generic helper method for performing a lookup through the business object service.
	 */
	@SuppressWarnings("unchecked")
	protected KimEntityImpl getEntityByKeyValue(String key, String value) {
		Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(key, value);
        Collection<KimEntityImpl> entities = (Collection<KimEntityImpl>)getBusinessObjectService().findMatching(KimEntityImpl.class, criteria);
        if (entities.size() >= 1) {
        	return entities.iterator().next();
        }
		return null;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	public AddressTypeInfo getAddressType( String code ) {
		AddressTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(AddressTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}
	
	public AffiliationTypeInfo getAffiliationType( String code ) {
		AffiliationTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(AffiliationTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public CitizenshipStatusInfo getCitizenshipStatus( String code ) {
		CitizenshipStatusImpl impl = getBusinessObjectService().findBySinglePrimaryKey(CitizenshipStatusImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public EmailTypeInfo getEmailType( String code ) {
		EmailTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(EmailTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public EmploymentStatusInfo getEmploymentStatus( String code ) {
		EmploymentStatusImpl impl = getBusinessObjectService().findBySinglePrimaryKey(EmploymentStatusImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public EmploymentTypeInfo getEmploymentType( String code ) {
		EmploymentTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(EmploymentTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public EntityNameTypeInfo getEntityNameType( String code ) {
		EntityNameTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(EntityNameTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public EntityTypeInfo getEntityType( String code ) {
		EntityTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(EntityTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}

	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code ) {
		ExternalIdentifierTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(ExternalIdentifierTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}
	
	public PhoneTypeInfo getPhoneType( String code ) {
		PhoneTypeImpl impl = getBusinessObjectService().findBySinglePrimaryKey(PhoneTypeImpl.class, code);
		if ( impl == null ) {
			return null;
		}
		return impl.toInfo();
	}
	
//	protected static Map<String,ExternalIdentifierTypeInfo> externalIdentifierTypeInfoCache = new HashMap<String, ExternalIdentifierTypeInfo>();
//	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code ) {
//		if ( !externalIdentifierTypeInfoCache.containsKey(code) ) {
//			Map<String,String> pk = new HashMap<String, String>(1);
//			pk.put("code", code);
//			ExternalIdentifierTypeImpl impl = (ExternalIdentifierTypeImpl)getBusinessObjectService().findByPrimaryKey(ExternalIdentifierTypeImpl.class, pk);
//			if ( impl != null ) {
//				externalIdentifierTypeInfoCache.put(code, impl.toInfo());
//			}
//		}
//		return externalIdentifierTypeInfoCache.get(code);
//	}
	
}
