/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.lookup;

import static java.util.Collections.sort;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.KimAttributeDataComparator;
import org.kuali.rice.kim.dao.KimGroupDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.keyvalues.IndicatorValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.lookup.keyvalues.KimAttributeValuesFinder;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupLookupableHelperServiceImpl  extends KimLookupableHelperServiceImpl {

	// need this so kimtypeId value can be retained in 'rows'
	// 1st pass populate the grprows
	// 2nd pass for jsp, no populate, so return the existing one.
    private static String KIM_TYPE_ID_PROPERTY_NAME = "kimTypeId";
	private List<Row> grpRows = new ArrayList<Row>();
	private List<Row> attrRows = new ArrayList<Row>();
	private KimGroupDao groupDao;
	private String typeId = "";
	private AttributeDefinitionMap attrDefinitions;
	private Map<String, String> groupTypeValuesCache = new HashMap<String, String>();

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
    	GroupImpl groupImpl = (GroupImpl) bo;
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        if(allowsNewOrCopyAction(KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_TYPE_NAME)){
        	anchorHtmlDataList.add(getEditGroupUrl(groupImpl));	
        }
    	return anchorHtmlDataList;
    }
    
    protected HtmlData getEditGroupUrl(GroupImpl groupImpl) {
    	String href = "";

        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.DOC_HANDLER_METHOD);
        parameters.put(KNSConstants.PARAMETER_COMMAND, KEWConstants.INITIATE_COMMAND);
        parameters.put(KNSConstants.DOCUMENT_TYPE_NAME, KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_TYPE_NAME);
        parameters.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupImpl.getGroupId());
        if (StringUtils.isNotBlank(getReturnLocation())) {
        	parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation());	 
		}
        href = UrlFactory.parameterizeUrl(KimCommonUtils.getKimBasePath()+KimConstants.KimUIConstants.KIM_GROUP_DOCUMENT_ACTION, parameters);
        
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, 
        		KNSConstants.DOC_HANDLER_METHOD, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        return anchorHtmlData;
    }

    /**
     * Converts GroupInfo objects to GroupImpl objects.
     * 
     * @param  fieldValues  names and values returned by the Group Lookup screen
     * @return  groupImplList  a list of GroupImpl objects
     * @see  KimCommonUtils#copyInfoToGroup(GroupInfo, GroupImpl)
     */
    @Override
    public List<GroupImpl> getSearchResults(java.util.Map<String,String> fieldValues)  {
    	List<? extends Group> groupInfoObjs = KIMServiceLocator.getGroupService().lookupGroups(fieldValues);
    	List<GroupImpl> groupImplList = new ArrayList<GroupImpl>();
    	for(Group g : groupInfoObjs){    		
    		GroupImpl impl = new GroupImpl();
    		impl = KimCommonUtils.copyInfoToGroup(g, impl);
    		impl.setGroupAttributes(KimCommonUtils.copyInfoAttributesToGroupAttributes(g.getAttributes(), g.getGroupId(), g.getKimTypeId()));
    		groupImplList.add(impl);    		
    	}
    	return groupImplList;
    }

    @Override
    public boolean checkForAdditionalFields(Map fieldValues) {
        List<Row> attributeRows = setupAttributeRows(fieldValues);
        if (attributeRows.isEmpty()) {
            setAttrRows(attributeRows);
        } else if (CollectionUtils.isEmpty(getAttrRows())) {
            setAttrRows(attributeRows);
        }
        if (getAttrRows().size() > 0) {
            return true;
        }
        return false;
    }


	@Override
	public List<Row> getRows() {
		if (getGrpRows().isEmpty()) {
			List<Row> rows = super.getRows();
			List<Row> returnRows = new ArrayList<Row>();
			for (Row row : rows) {
				for (int i = row.getFields().size() - 1; i >= 0; i--) {
					Field field = row.getFields().get(i);
					if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME)) {
						Field typeField = new Field();
						typeField.setFieldLabel("Type");
						typeField.setPropertyName(KIM_TYPE_ID_PROPERTY_NAME);
						typeField.setFieldValidValues(getGroupTypeOptions());
						typeField.setFieldType(Field.DROPDOWN);
						row.getFields().set(i, typeField);
					}
				}
				returnRows.add(row);
			}
			// principalName
			Field typeField = new Field();
			typeField.setFieldLabel("Principal Name");
			typeField.setPropertyName(KIMPropertyConstants.Person.PRINCIPAL_NAME);
			typeField.setFieldType(Field.TEXT);
			typeField.setMaxLength(40);
			typeField.setSize(20);
			typeField.setQuickFinderClassNameImpl("org.kuali.rice.kim.bo.Person");
			typeField.setFieldConversions( "principalName:principalName" );
			typeField.setLookupParameters( "principalName:principalName" );
			// Identify the best spot to insert the "Principal Name" search field. Note that the code below assumes that the final row of the
			// group search fields is not a row with hidden fields; if this ever becomes the case in the future, this fix may need to
			// be modified accordingly.
			List<Field> fields = (returnRows.isEmpty()) ? new ArrayList<Field>() : returnRows.get(returnRows.size() - 1).getFields();
			if (!fields.isEmpty() && fields.get(fields.size() - 1).getFieldType().equals(Field.BLANK_SPACE)) {
				// If the last row in the list has a BLANK_SPACE field coming after any non-BLANK_SPACE fields, add the new field to that row.
				int insertLoc = fields.size() - 1;
				while (insertLoc >= 0 && fields.get(insertLoc).getFieldType().equals(Field.BLANK_SPACE)) {
					insertLoc--;
				}
				fields.set(insertLoc + 1, typeField);
				returnRows.get(returnRows.size() - 1).setFields(fields);
			} else {
				// Otherwise, add a new row containing that field.
				int fieldLen = fields.size();
				fields = new ArrayList<Field>();
				fields.add(typeField);
				for (int i = 1; i < fieldLen; i++) {
					Field blankSpace = new Field();
					blankSpace.setFieldType(Field.BLANK_SPACE);
					blankSpace.setPropertyName(Field.BLANK_SPACE);
					fields.add(blankSpace);
				}
				returnRows.add(new Row(fields));
			}
			
			setGrpRows(returnRows);
		}
		if (getAttrRows().isEmpty()) {
			setAttrDefinitions(new AttributeDefinitionMap());
			return getGrpRows();
		} else {
			List<Row> fullRows = new ArrayList<Row>();
			fullRows.addAll(getGrpRows());
			fullRows.addAll(getAttrRows());
			return fullRows;
		}

	}


	@Override
	public List<Column> getColumns() {
		List<Column> columns =  super.getColumns();
        for (Row row : attrRows) {
            for (Field field : row.getFields()) {
                Column newColumn = new Column();
                newColumn.setColumnTitle(field.getFieldLabel());
                newColumn.setMaxLength(getColumnMaxLength(field.getPropertyName()));
                newColumn.setPropertyName(field.getPropertyName());
                newColumn.setFormatter((Formatter) field.getFormatter());
                columns.add(newColumn);
            }
        }
        return columns;
	}

    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));
        Collection displayList;

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();
            if(element instanceof PersistableBusinessObject){
                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
            }

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);

            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            //Fix for JIRA - KFSMI-2417
            if("".equals(actionUrls)){
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
                Object prop = null;
                boolean skipPropTypeCheck = false;
                if (col.getPropertyName().matches("\\w+\\.\\d+$")) {
                    String id = col.getPropertyName().substring(col.getPropertyName().lastIndexOf('.') + 1); //.split("\\d+$"))[1];
                    prop = ((GroupImpl)element).getGroupAttributeValueById(id);
                }
                if (prop == null) {
                    prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                } else {
                    skipPropTypeCheck = true;
                }

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if ( propClass == null /*&& !skipPropTypeCheck*/) {
                    try {
                        propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                        propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }

                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                    formatter = new CollectionFormatter();
                    }

                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                        if (col.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME)) {
                            propValue = groupTypeValuesCache.get(prop.toString());
                        }
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));

                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            if(element instanceof PersistableBusinessObject){
                row.setObjectId((((PersistableBusinessObject)element).getObjectId()));
            }


            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

	private List<KeyLabelPair> getGroupTypeOptions() {
		List<KeyLabelPair> options = new ArrayList<KeyLabelPair>();
		options.add(new KeyLabelPair("", ""));

		Collection<KimTypeInfo> kimGroupTypes = KIMServiceLocator.getTypeInfoService().getAllTypes();
		// get the distinct list of type IDs from all groups in the system
        for (KimTypeInfo kimType : kimGroupTypes) {
            if (KimTypeLookupableHelperServiceImpl.hasGroupTypeService(kimType) && groupTypeValuesCache.get(kimType.getKimTypeId()) == null) {
                String value = kimType.getNamespaceCode().trim() + KNSConstants.FIELD_CONVERSION_PAIR_SEPARATOR + kimType.getName().trim();
                options.add(new KeyLabelPair(kimType.getKimTypeId(), value));
            }
        }
        Collections.sort(options, new Comparator<KeyLabelPair>() {
           public int compare(KeyLabelPair k1, KeyLabelPair k2) {
               return k1.getLabel().compareTo(k2.getLabel());
           }
        });
		return options;
	}

	private List<Row> setupAttributeRows(Map fieldValues) {
		List<Row> returnRows = new ArrayList<Row>();
		for (Row row : getGrpRows()) {
			Field field = (Field) row.getFields().get(0);
			if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME) && StringUtils.isNotBlank(field.getPropertyValue())) {
				if (!StringUtils.isBlank(getTypeId()) || !getTypeId().equals(field.getPropertyValue())) {
					setTypeId(field.getPropertyValue());
					setAttrRows(new ArrayList<Row>());
					KimTypeInfo kimType = getTypeInfoService().getKimType( field.getPropertyValue() );
					KimTypeService kimTypeService = KimCommonUtils.getKimTypeService(kimType);
			        AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions(kimType.getKimTypeId());
			        setAttrDefinitions(definitions);
		            for (AttributeDefinition definition  : definitions.values()) {
				        List<Field> fields = new ArrayList<Field>();
						Field typeField = new Field();

						String attrDefnId = getAttrDefnId(definition);
						typeField.setFieldLabel(definition.getLabel());
						typeField.setPropertyName(definition.getName()+"."+attrDefnId);
						typeField.setPropertyValue(fieldValues.get(typeField.getPropertyName()));
						if (definition.getControl().isSelect()) {
					        try {
					            KeyValuesFinder finder = (KeyValuesFinder) ClassLoaderUtils.getClass(definition.getControl().getValuesFinderClass()).newInstance();
					            // need to initialize KIM Values Finder before retrieving values
					            if (finder instanceof KimAttributeValuesFinder) {
					            	((KimAttributeValuesFinder) finder).setKimTypeId(field.getPropertyValue());
					            	((KimAttributeValuesFinder) finder).setKimAttributeName(definition.getName());
					            }
						        typeField.setFieldValidValues(finder.getKeyValues());
						        typeField.setFieldType(Field.DROPDOWN);
					        }
					        catch (InstantiationException e) {
					            throw new RuntimeException(e.getMessage());
					        }
					        catch (IllegalAccessException e) {
					            throw new RuntimeException(e.getMessage());
					        }
						} else if (definition.getControl().isText()){
							typeField.setMaxLength(definition.getMaxLength());
							if (definition.getControl().getSize() != null) {
							    typeField.setSize(definition.getControl().getSize());
							}
						    typeField.setFieldType(Field.TEXT);
						} else if (definition.getControl().isRadio()) {
						    try {
                                KeyValuesFinder finder = (KeyValuesFinder) ClassLoaderUtils.getClass(definition.getControl().getValuesFinderClass()).newInstance();
                                typeField.setFieldValidValues(finder.getKeyValues());
                                typeField.setFieldType(Field.RADIO);
                            }
                            catch (InstantiationException e) {
                                throw new RuntimeException(e.getMessage());
                            }
                            catch (IllegalAccessException e) {
                                throw new RuntimeException(e.getMessage());
                            }
						} else if (definition.getControl().isCheckbox()) {
						    KeyValuesFinder finder = (KeyValuesFinder)new IndicatorValuesFinder();
                            typeField.setFieldValidValues(finder.getKeyValues());
                            typeField.setFieldType(Field.RADIO);
						    //typeField.setFieldType(Field.CHECKBOX);
						} else if (definition.getControl().isHidden()) {
						    typeField.setFieldType(Field.HIDDEN);
						} else if (definition.getControl().isLookupReadonly()) {
						    typeField.setFieldType(Field.LOOKUP_READONLY);
						} else if (definition.getControl().isTextarea()) {
						    typeField.setMaxLength(definition.getMaxLength());
                            if (definition.getControl().getSize() != null) {
                                typeField.setSize(definition.getControl().getSize());
                            }
                            typeField.setFieldType(Field.TEXT_AREA);
						}
						fields.add(typeField);
						returnRows.add(new Row(fields));
		            }
				} else {
					return getAttrRows();
				}
			} else if (field.getPropertyName().equals(KIM_TYPE_ID_PROPERTY_NAME) && StringUtils.isBlank(field.getPropertyValue())) {
				setTypeId("");
			}
		}
		return returnRows;
	}

    private String getAttrDefnId(AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
    	} else {
    		return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();

    	}
    }

	public List<Row> getGrpRows() {
		return this.grpRows;
	}

	public void setGrpRows(List<Row> grpRows) {
		this.grpRows = grpRows;
	}

	public KimGroupDao getGroupDao() {
		return this.groupDao;
	}

	public void setGroupDao(KimGroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public AttributeDefinitionMap getAttrDefinitions() {
		return this.attrDefinitions;
	}

	public void setAttrDefinitions(AttributeDefinitionMap attrDefinitions) {
		this.attrDefinitions = attrDefinitions;
	}

	public List<Row> getAttrRows() {
		return this.attrRows;
	}

	public void setAttrRows(List<Row> attrRows) {
		this.attrRows = attrRows;
	}

	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

    @Override
    public void performClear(LookupForm lookupForm) {
        super.performClear(lookupForm);
        this.attrRows = new ArrayList<Row>();
    }

}