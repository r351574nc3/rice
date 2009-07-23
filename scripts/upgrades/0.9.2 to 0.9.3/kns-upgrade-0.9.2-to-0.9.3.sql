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
------------------ KNS UPGRADE -----------------------
alter table SH_NTE_T modify NTE_AUTH_ID VARCHAR2(30)
/

ALTER TABLE FS_ADHOC_RTE_ACTN_RECP_T DROP CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TP1
/
ALTER TABLE FS_ADHOC_RTE_ACTN_RECP_T
ADD (CONSTRAINT FS_ADHOC_RTE_ACTN_RECP_TP1 PRIMARY KEY (
      ACTN_RQST_RECP_TYP_CD,ACTN_RQST_CD,ACTN_RQST_RECP_ID,FDOC_NBR ) )
/

-- SEQUENCES CREATE --
CREATE SEQUENCE LOCK_ID_SEQ INCREMENT BY 1 START WITH 2000
/

-- PESSIMISTIC LOCK CREATE --
CREATE TABLE KNS_PESSIMISTIC_LOCK_T (
        LOCK_ID                        NUMBER(14) CONSTRAINT KNS_PESSIMISTIC_LOCK_TN1 NOT NULL, 
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT KNS_PESSIMISTIC_LOCK_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT KNS_PESSIMISTIC_LOCK_TN3 NOT NULL,
        LOCK_DESCRIPTOR                VARCHAR2(4000),
        FDOC_NBR                       VARCHAR2(14) CONSTRAINT KNS_PESSIMISTIC_LOCK_TN4 NOT NULL,             
        LOCK_GENERATED_TS              DATE CONSTRAINT KNS_PESSIMISTIC_LOCK_TN5 NOT NULL,
        PERSON_UNVL_ID                 VARCHAR2(10) CONSTRAINT KNS_PESSIMISTIC_LOCK_TN6 NOT NULL,
     CONSTRAINT KNS_PESSIMISTIC_LOCK_TP1 PRIMARY KEY (LOCK_ID),
     CONSTRAINT KNS_PESSIMISTIC_LOCK_TC0 UNIQUE (OBJ_ID)
)
/
CREATE INDEX KNS_PESSIMISTIC_LOCK_TI1 ON KNS_PESSIMISTIC_LOCK_T(FDOC_NBR)
/
CREATE INDEX KNS_PESSIMISTIC_LOCK_TI2 ON KNS_PESSIMISTIC_LOCK_T(PERSON_UNVL_ID)
/

-- BOOTSTRAP DATA --
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', sys_guid(), 1, 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS') 
/
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', sys_guid(), 1, 'AUTH', 'KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS') 
/

CREATE TABLE FP_MAINT_DOC_ATTACHMENT_T (
        FDOC_NBR                       VARCHAR2(14) NOT NULL,
        ATTACHMENT                     BLOB NOT NULL,
		FILE_NAME		       		   VARCHAR2(150),
		CONTENT_TYPE		       	   VARCHAR2(50),
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 NOT NULL, 
        CONSTRAINT FP_MAINT_DOC_ATTACHMENT_TP1 PRIMARY KEY (FDOC_NBR),
        CONSTRAINT FP_MAINT_DOC_ATTACHMENT_TC0 UNIQUE (OBJ_ID)
)
/

CREATE SEQUENCE FP_DOC_TYPE_ATTR_ID_SEQ INCREMENT BY 1 START WITH 1000
/
CREATE TABLE FP_DOC_TYPE_ATTR_T (
        ID                             NUMBER(8) CONSTRAINT FP_DOC_TYPE_ATTR_TN1 NOT NULL,
        OBJ_ID                         VARCHAR2(36) DEFAULT SYS_GUID() CONSTRAINT FP_DOC_TYPE_ATTR_TN2 NOT NULL,
        VER_NBR                        NUMBER(8) DEFAULT 1 CONSTRAINT FP_DOC_TYPE_ATTR_TN3 NOT NULL,
        ACTIVE_IND                     CHAR(1) DEFAULT 'Y' CONSTRAINT FP_DOC_TYPE_ATTR_TN4 NOT NULL,
        DOC_TYP_ATTR_CD                VARCHAR2(100) CONSTRAINT FP_DOC_TYPE_ATTR_TN5 NOT NULL,
        DOC_TYP_ATTR_VAL               VARCHAR2(400),
        DOC_TYP_ATTR_LBL               VARCHAR2(400),
        FDOC_TYP_CD                    VARCHAR2(4) CONSTRAINT FP_DOC_TYPE_ATTR_TN6 NOT NULL,
     CONSTRAINT FP_DOC_TYPE_ATTR_TP1 PRIMARY KEY (
        ID) ,
     CONSTRAINT FP_DOC_TYPE_ATTR_TC0 UNIQUE (OBJ_ID) 
)
/
ALTER TABLE FP_DOC_TYPE_ATTR_T
ADD CONSTRAINT FP_DOC_TYPE_ATTR_TR1 FOREIGN KEY
(
FDOC_TYP_CD
)
REFERENCES FP_DOC_TYPE_T
(
FDOC_TYP_CD
) ENABLE
/
---- FP_DOC_HEADER_T ADJUSTMENTS ----
drop index FP_DOC_HEADER_TI4
/
alter table FP_DOC_HEADER_T drop column FDOC_STATUS_CD
/
alter table FP_DOC_HEADER_T drop column FDOC_TOTAL_AMT
/
alter table FP_DOC_HEADER_T drop column FDOC_IN_ERR_NBR
/
alter table FP_DOC_HEADER_T drop column TEMP_DOC_FNL_DT
/
---- FP_DOC_TYPE_T ADJUSTMENTS ----
drop index FP_DOC_TYPE_TI2
/
alter table FP_DOC_TYPE_T drop constraint FP_DOC_TYPE_TR1
/
alter table FP_DOC_TYPE_T drop column FDOC_GRP_CD
/
alter table FP_DOC_TYPE_T drop column FIN_ELIM_ELGBL_CD
/
alter table FP_DOC_TYPE_T drop column FDOC_RTNG_RULE_CD
/
alter table FP_DOC_TYPE_T drop column FDOC_AUTOAPRV_DAYS
/
alter table FP_DOC_TYPE_T drop column FDOC_BALANCED_CD
/
alter table FP_DOC_TYPE_T drop column TRN_SCRBBR_OFST_GEN_IND
/
---- KNS TABLE REMOVALS ----
drop table FP_DOC_GROUP_T cascade CONSTRAINTS
/
drop table FP_DOC_STATUS_T cascade CONSTRAINTS
/
drop table SH_LOCK_TYP_DESC_T cascade CONSTRAINTS
/
drop table SH_LOCK_T cascade CONSTRAINTS
/
