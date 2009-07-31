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
package org.kuali.rice.kim.web.struts.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentCitizenship;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.KimTypeAttributesHelper;
import org.kuali.rice.kim.document.rule.AttributeValidationHelper;
import org.kuali.rice.kim.rule.event.ui.AddGroupEvent;
import org.kuali.rice.kim.rule.event.ui.AddPersonDelegationMemberEvent;
import org.kuali.rice.kim.rule.event.ui.AddPersonDocumentRoleQualifierEvent;
import org.kuali.rice.kim.rule.event.ui.AddRoleEvent;
import org.kuali.rice.kim.rules.ui.PersonDocumentRoleRule;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementPersonDocumentForm;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentAction extends IdentityManagementDocumentActionBase {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionForward forward = null;
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        // accept either the principal name or principal ID, looking up the latter if necessary
        // this allows inquiry links to work even when only the principal name is present
        String principalId = request.getParameter(KIMPropertyConstants.Person.PRINCIPAL_ID);
        String principalName = request.getParameter(KIMPropertyConstants.Person.PRINCIPAL_NAME);
        if ( StringUtils.isBlank(principalId) && StringUtils.isNotBlank(principalName) ) {
        	KimPrincipalInfo principal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
        	if ( principal != null ) {
        		principalId = principal.getPrincipalId();
        	}
        }
        if ( principalId != null ) {
        	personDocumentForm.setPrincipalId(principalId);
        }
        forward = super.execute(mapping, form, request, response);
        
        personDocumentForm.setCanModifyEntity(getUiDocumentService().canModifyEntity(personDocumentForm.getPrincipalId()));
        personDocumentForm.setCanOverrideEntityPrivacyPreferences(getUiDocumentService().canOverrideEntityPrivacyPreferences(personDocumentForm.getPrincipalId()));
		return forward;
    }
    
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
	 */
	@Override
	protected void loadDocument(KualiDocumentFormBase form)
			throws WorkflowException {
		super.loadDocument(form);
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
		IdentityManagementPersonDocument personDoc = personDocumentForm.getPersonDocument();
		populateRoleInformation(personDoc);
	}

	protected void populateRoleInformation( IdentityManagementPersonDocument personDoc ) {
		for (PersonDocumentRole role : personDoc.getRoles()) {
	        KimTypeService kimTypeService = (KimTypeService)KIMServiceLocator.getService(getKimTypeServiceName(role.getKimRoleType()));
			role.setDefinitions(kimTypeService.getAttributeDefinitions(role.getKimTypeId()));
        	// when post again, it will need this during populate
            role.setNewRolePrncpl(new KimDocumentRoleMember());
            for (String key : role.getDefinitions().keySet()) {
            	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
            	//qualifier.setQualifierKey(key);
	        	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
            	role.getNewRolePrncpl().getQualifiers().add(qualifier);
            }
	        role.setAttributeEntry( getUiDocumentService().getAttributeEntries( role.getDefinitions() ) );
		}
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
	 */
	@Override
	protected void createDocument(KualiDocumentFormBase form)
			throws WorkflowException {
		super.createDocument(form);
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        if(StringUtils.isBlank(personDocumentForm.getPrincipalId())){
    		personDocumentForm.getPersonDocument().initializeDocumentForNewPerson();
    		personDocumentForm.setPrincipalId(personDocumentForm.getPersonDocument().getPrincipalId());
        } else {
        	getUiDocumentService().loadEntityToPersonDoc(personDocumentForm.getPersonDocument(), personDocumentForm.getPrincipalId() );
        	populateRoleInformation( personDocumentForm.getPersonDocument() );
        	if(personDocumentForm.getPersonDocument()!=null) 
        		personDocumentForm.getPersonDocument().setIfRolesEditable();
        }
	}
	
	/***
	 * @see org.kuali.rice.kim.web.struts.action.IdentityManagementDocumentActionBase#getActionName()
	 */
	public String getActionName(){
		return KimConstants.KimUIConstants.KIM_PERSON_DOCUMENT_ACTION;
	}

	public ActionForward addAffln(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentAffiliation newAffln = personDocumentForm.getNewAffln();
        newAffln.setDocumentNumber(personDocumentForm.getPersonDocument().getDocumentNumber());
        newAffln.refreshReferenceObject("affiliationType");
        personDocumentForm.getPersonDocument().getAffiliations().add(newAffln);
        personDocumentForm.setNewAffln(new PersonDocumentAffiliation());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
	
    public ActionForward deleteAffln(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getAffiliations().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    public ActionForward addCitizenship(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentCitizenship newCitizenship = personDocumentForm.getNewCitizenship();
        personDocumentForm.getPersonDocument().getCitizenships().add(newCitizenship);
        personDocumentForm.setNewCitizenship(new PersonDocumentCitizenship());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteCitizenship(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getCitizenships().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addEmpInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDOc = personDocumentForm.getPersonDocument();
        PersonDocumentAffiliation affiliation = personDOc.getAffiliations().get(getSelectedLine(request));        
        PersonDocumentEmploymentInfo newempInfo = affiliation.getNewEmpInfo();
        newempInfo.setDocumentNumber(personDOc.getDocumentNumber());
        newempInfo.setVersionNumber(new Long(1));
        affiliation.getEmpInfos().add(newempInfo);
        affiliation.setNewEmpInfo(new PersonDocumentEmploymentInfo());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteEmpInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        String selectedIndexes = getSelectedParentChildIdx(request);
        if (selectedIndexes != null) {
	        String [] indexes = StringUtils.split(selectedIndexes,":");
	        PersonDocumentAffiliation affiliation = personDocumentForm.getPersonDocument().getAffiliations().get(Integer.parseInt(indexes[0]));
	        affiliation.getEmpInfos().remove(Integer.parseInt(indexes[1]));
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    private String getSelectedParentChildIdx(HttpServletRequest request) {
    	String lineNumber = null;
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lineNumber = StringUtils.substringBetween(parameterName, ".line", ".");
        }
        return lineNumber;
    }

    public ActionForward addName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentName newName = personDocumentForm.getNewName();
        newName.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getNames().add(newName);
        personDocumentForm.setNewName(new PersonDocumentName());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteName(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getNames().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentAddress newAddress = personDocumentForm.getNewAddress();
        newAddress.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getAddrs().add(newAddress);
        personDocumentForm.setNewAddress(new PersonDocumentAddress());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getAddrs().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addPhone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentPhone newPhone = personDocumentForm.getNewPhone();
        newPhone.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getPhones().add(newPhone);
        personDocumentForm.setNewPhone(new PersonDocumentPhone());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deletePhone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getPhones().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentEmail newEmail = personDocumentForm.getNewEmail();
        newEmail.setDocumentNumber(personDocumentForm.getDocument().getDocumentNumber());
        personDocumentForm.getPersonDocument().getEmails().add(newEmail);
        personDocumentForm.setNewEmail(new PersonDocumentEmail());        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteEmail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getEmails().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentGroup newGroup = personDocumentForm.getNewGroup();
        if (getKualiRuleService().applyRules(new AddGroupEvent("",personDocumentForm.getPersonDocument(), newGroup))) {
	        GroupImpl groupImpl = (GroupImpl)getUiDocumentService().getMember(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE, newGroup.getGroupId());
	        newGroup.setGroupName(groupImpl.getGroupName());
	        newGroup.setNamespaceCode(groupImpl.getNamespaceCode());
	        newGroup.setKimTypeId(groupImpl.getKimTypeId());
        	personDocumentForm.getPersonDocument().getGroups().add(newGroup);
	        personDocumentForm.setNewGroup(new PersonDocumentGroup());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getGroups().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        PersonDocumentRole newRole = personDocumentForm.getNewRole();
        if (getKualiRuleService().applyRules(new AddRoleEvent("",personDocumentForm.getPersonDocument(), newRole))) {
	        RoleImpl roleImpl = (RoleImpl)getUiDocumentService().getMember(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, newRole.getRoleId());
	        if(!validateRole(newRole.getRoleId(), roleImpl, PersonDocumentRoleRule.ERROR_PATH, "Person")){
	        	return mapping.findForward(RiceConstants.MAPPING_BASIC);
	        }
	        newRole.setRoleName(roleImpl.getRoleName());
	        newRole.setNamespaceCode(roleImpl.getNamespaceCode());
	        newRole.setKimTypeId(roleImpl.getKimTypeId());
	        if(!validateRoleAssignment(personDocumentForm.getPersonDocument(), newRole)){
	        	return mapping.findForward(RiceConstants.MAPPING_BASIC);
	        }
	        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(getKimTypeServiceName(newRole.getKimRoleType()));
	        //AttributeDefinitionMap definitions = kimTypeService.getAttributeDefinitions();
	        // role type populated from form is not a complete record
	        newRole.getKimRoleType().setKimTypeId(newRole.getKimTypeId());
	        newRole.setDefinitions(kimTypeService.getAttributeDefinitions(newRole.getKimTypeId()));
	        KimDocumentRoleMember newRolePrncpl = newRole.getNewRolePrncpl();
	        newRole.refreshReferenceObject("assignedResponsibilities");
	        
	        for (String key : newRole.getDefinitions().keySet()) {
	        	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	        	//qualifier.setQualifierKey(key);
	        	setAttrDefnIdForQualifier(qualifier,newRole.getDefinitions().get(key));
	        	newRolePrncpl.getQualifiers().add(qualifier);
	        }
	        if (newRole.getDefinitions().isEmpty()) {
	        	List<KimDocumentRoleMember> rolePrncpls = new ArrayList<KimDocumentRoleMember>();
	        	setupRoleRspActions(newRole, newRolePrncpl);
	            rolePrncpls.add(newRolePrncpl);
	        	newRole.setRolePrncpls(rolePrncpls);
	        }
	        //newRole.setNewRolePrncpl(newRolePrncpl);
	        newRole.setAttributeEntry( getUiDocumentService().getAttributeEntries( newRole.getDefinitions() ) );
	        personDocumentForm.getPersonDocument().getRoles().add(newRole);
	        personDocumentForm.setNewRole(new PersonDocumentRole());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
	private boolean validateRoleAssignment(IdentityManagementPersonDocument document, PersonDocumentRole newRole){
        boolean rulePassed = true;
        if(!document.validAssignRole(newRole)){
			GlobalVariables.getMessageMap().putError("newRole.roleId", 
					RiceKeyConstants.ERROR_ASSIGN_ROLE, 
					new String[] {newRole.getNamespaceCode(), newRole.getRoleName()});
	        rulePassed = false;
        }
		return rulePassed;
	}

    private void setupRoleRspActions(PersonDocumentRole role, KimDocumentRoleMember rolePrncpl) {
        for (RoleResponsibilityImpl roleResp : role.getAssignedResponsibilities()) {
        	if (getResponsibilityService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
        		KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
        		roleRspAction.setRoleResponsibilityId("*");        		
        		roleRspAction.refreshReferenceObject("roleResponsibility");
        		if(rolePrncpl.getRoleRspActions()==null || rolePrncpl.getRoleRspActions().size()<1){
        			if(rolePrncpl.getRoleRspActions()==null)
        				rolePrncpl.setRoleRspActions(new ArrayList<KimDocumentRoleResponsibilityAction>());
        			 rolePrncpl.getRoleRspActions().add(roleRspAction);
        		}
        	}        	
        }
    }
    
//	private boolean isUniqueRoleRspAction(List<KimDocumentRoleResponsibilityAction> roleRspActions, KimDocumentRoleResponsibilityAction roleRspAction){
//    	if(roleRspActions==null || roleRspAction==null) return false;
//    	for(KimDocumentRoleResponsibilityAction roleRspActionTemp: roleRspActions){
//    		if((StringUtils.isNotEmpty(roleRspActionTemp.getRoleMemberId()) && roleRspActionTemp.getRoleMemberId().equals(roleRspAction.getRoleMemberId())) && 
//    			(StringUtils.isNotEmpty(roleRspActionTemp.getRoleResponsibilityId())	&& roleRspActionTemp.getRoleResponsibilityId().equals(roleRspAction.getRoleResponsibilityId())))
//    			return false;
//    	}
//    	return true;
//    }
	    

    private void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier,AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		qualifier.setKimAttrDefnId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");
    	} else {
    		qualifier.setKimAttrDefnId(((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		qualifier.refreshReferenceObject("kimAttribute");

    	}
    }
    
    public ActionForward deleteRole(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getRoles().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward addRoleQualifier(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDOc = personDocumentForm.getPersonDocument();
        int selectedRoleIdx = getSelectedLine(request);
        PersonDocumentRole role = personDOc.getRoles().get(selectedRoleIdx);
        KimDocumentRoleMember newRolePrncpl = role.getNewRolePrncpl();
    	
    	if (getKualiRuleService().applyRules(new AddPersonDocumentRoleQualifierEvent("",
    			personDOc, newRolePrncpl, role, selectedRoleIdx))) {
        	setupRoleRspActions(role, newRolePrncpl);
    		role.getRolePrncpls().add(newRolePrncpl);
	        role.setNewRolePrncpl(new KimDocumentRoleMember());
	        for (String key : role.getDefinitions().keySet()) {
	        	KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
	        	//qualifier.setQualifierKey(key);
	        	setAttrDefnIdForQualifier(qualifier,role.getDefinitions().get(key));
	        	role.getNewRolePrncpl().getQualifiers().add(qualifier);
	        }
    	}

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    public ActionForward deleteRoleQualifier(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        String selectedIndexes = getSelectedParentChildIdx(request);
        if (selectedIndexes != null) {
	        String [] indexes = StringUtils.split(selectedIndexes,":");
	        PersonDocumentRole role = personDocumentForm.getPersonDocument().getRoles().get(Integer.parseInt(indexes[0]));
	        role.getRolePrncpls().remove(Integer.parseInt(indexes[1]));
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward addDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        IdentityManagementPersonDocument personDocument = (IdentityManagementPersonDocument)personDocumentForm.getPersonDocument();
        RoleDocumentDelegationMember newDelegationMember = personDocumentForm.getNewDelegationMember();
        KimTypeAttributesHelper attrHelper = newDelegationMember.getAttributesHelper();
        if (getKualiRuleService().applyRules(new AddPersonDelegationMemberEvent("", personDocumentForm.getPersonDocument(), newDelegationMember))) {
	        RoleImpl roleImpl = (RoleImpl)getUiDocumentService().getMember(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, newDelegationMember.getRoleImpl().getRoleId());
	        if(!validateRole(newDelegationMember.getRoleImpl().getRoleId(),roleImpl, PersonDocumentRoleRule.ERROR_PATH, "Person")){
	        	return mapping.findForward(RiceConstants.MAPPING_BASIC);
	        }
	        newDelegationMember.setRoleImpl(roleImpl);
	        AttributeDefinition attrDefinition;
	        RoleMemberImpl roleMember = KIMServiceLocator.getUiDocumentService().getRoleMember(newDelegationMember.getRoleMemberId());
	        AttributeSet roleMemberAttributes = (new AttributeValidationHelper()).convertAttributesToMap(roleMember.getAttributes());
	        for (String key: attrHelper.getDefinitions().keySet()) {
	        	RoleDocumentDelegationMemberQualifier qualifier = new RoleDocumentDelegationMemberQualifier();
	        	attrDefinition = attrHelper.getDefinitions().get(key);
	        	qualifier.setKimAttrDefnId(attrHelper.getKimAttributeDefnId(attrDefinition));
	        	qualifier.setAttrVal(attrHelper.getAttributeValue(roleMemberAttributes, attrDefinition.getName()));
	        	newDelegationMember.setMemberId(personDocument.getPrincipalId());
	        	newDelegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
	        	newDelegationMember.getQualifiers().add(qualifier);
	        }
	        //newDelegationMember.setAttributeEntry(getUiDocumentService().getAttributeEntries(newDelegationMember.getAttributesHelper().getDefinitions())));
	        personDocument.getDelegationMembers().add(newDelegationMember);
	        personDocumentForm.setNewDelegationMember(new RoleDocumentDelegationMember());
        }
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    public ActionForward deleteDelegationMember(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm personDocumentForm = (IdentityManagementPersonDocumentForm) form;
        personDocumentForm.getPersonDocument().getDelegationMembers().remove(getLineToDelete(request));
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

	@Override
	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return super.save(mapping, form, request, response);
	}
	
	private String getKimTypeServiceName (KimTypeInfo kimType) {
    	String serviceName = kimType.getKimTypeServiceName();
    	if (StringUtils.isBlank(serviceName)) {
    		serviceName = "kimTypeService";
    	}
    	return serviceName;

	}
	
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IdentityManagementPersonDocumentForm impdForm = (IdentityManagementPersonDocumentForm) form;

        ActionForward forward = this.refreshAfterDelegationMemberRoleSelection(mapping, impdForm, request);
        if (forward != null) {
            return forward;
        }

        return super.refresh(mapping, form, request, response);
	}

    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	return super.performLookup(mapping, form, request, response);
    }
    
    private ActionForward refreshAfterDelegationMemberRoleSelection(ActionMapping mapping, IdentityManagementPersonDocumentForm impdForm, HttpServletRequest request) {
        String refreshCaller = impdForm.getRefreshCaller();

        boolean isRoleLookupable = KimConstants.KimUIConstants.ROLE_LOOKUPABLE_IMPL.equals(refreshCaller);
        boolean isRoleMemberLookupable = KimConstants.KimUIConstants.KIM_DOCUMENT_ROLE_MEMBER_LOOKUPABLE_IMPL.equals(refreshCaller);

        // do not execute the further refreshing logic if the refresh caller is not a lookupable
        if (!isRoleLookupable && !isRoleMemberLookupable) {
            return null;
        }

        //In case of delegation member lookup impdForm.getNewDelegationMemberRoleId() will be populated.
        if(impdForm.getNewDelegationMemberRoleId() == null){
            return null;
        }
        if(isRoleLookupable){
            return renderRoleMemberSelection(mapping, request, impdForm);
        }

        String roleMemberId = request.getParameter(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID);
        if(StringUtils.isNotBlank(roleMemberId)){
            impdForm.getNewDelegationMember().setRoleMemberId(roleMemberId);
            RoleMemberImpl roleMember = getUiDocumentService().getRoleMember(roleMemberId);
            impdForm.getNewDelegationMember().setRoleMemberMemberId(roleMember.getMemberId());
            impdForm.getNewDelegationMember().setRoleMemberMemberTypeCode(roleMember.getMemberTypeCode());
            impdForm.getNewDelegationMember().setRoleMemberName(getUiDocumentService().getMemberName(impdForm.getNewDelegationMember().getRoleMemberMemberTypeCode(), impdForm.getNewDelegationMember().getRoleMemberMemberId()));
            impdForm.getNewDelegationMember().setRoleMemberNamespaceCode(getUiDocumentService().getMemberNamespaceCode(impdForm.getNewDelegationMember().getRoleMemberMemberTypeCode(), impdForm.getNewDelegationMember().getRoleMemberMemberId()));
	        RoleImpl roleImpl = (RoleImpl)getUiDocumentService().getMember(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, impdForm.getNewDelegationMember().getRoleImpl().getRoleId());
	        if(!validateRole(impdForm.getNewDelegationMember().getRoleImpl().getRoleId(), roleImpl, PersonDocumentRoleRule.ERROR_PATH, "Person")){
	        	return mapping.findForward(RiceConstants.MAPPING_BASIC);
	        }
	        impdForm.getNewDelegationMember().setRoleImpl(roleImpl);
        }
        impdForm.setNewDelegationMemberRoleId(null);
        return null;
    }
    
    private ActionForward renderRoleMemberSelection(ActionMapping mapping, HttpServletRequest request, IdentityManagementPersonDocumentForm impdForm) {
        Properties props = new Properties();

        props.put(KNSConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, KimDocumentRoleMember.class.getName());
        props.put(KNSConstants.LOOKUP_ANCHOR, KNSConstants.ANCHOR_TOP_OF_FORM);
        props.put(KNSConstants.LOOKED_UP_COLLECTION_NAME, KimConstants.KimUIConstants.ROLE_MEMBERS_COLLECTION_NAME);

        String conversionPatttern = "{0}" + KNSConstants.FIELD_CONVERSION_PAIR_SEPARATOR + "{0}";
        StringBuilder fieldConversion = new StringBuilder();
        fieldConversion.append(MessageFormat.format(conversionPatttern, 
       		KimConstants.PrimaryKeyConstants.ROLE_ID)).append(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
        fieldConversion.append(MessageFormat.format(conversionPatttern, 
           		KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID)).append(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);

        props.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, fieldConversion);

        props.put(KimConstants.PrimaryKeyConstants.ROLE_ID, impdForm.getNewDelegationMember().getRoleImpl().getRoleId());

        props.put(KNSConstants.RETURN_LOCATION_PARAMETER, this.getReturnLocation(request, mapping));
        props.put(KNSConstants.BACK_LOCATION, this.getReturnLocation(request, mapping));

        props.put(KNSConstants.LOOKUP_AUTO_SEARCH, "Yes");
        props.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.SEARCH_METHOD);

        props.put(KNSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(impdForm));
        props.put(KNSConstants.DOC_NUM, impdForm.getDocument().getDocumentNumber());

        // TODO: how should this forward be handled
        String url = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + KNSConstants.LOOKUP_ACTION, props);

        impdForm.registerEditableProperty("methodToCall");

        return new ActionForward(url, true);
    }

}
