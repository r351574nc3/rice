/*
 * Copyright 2009 The Kuali Foundation
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
CREATE TABLE KRIM_ROLE_MBR_T
(
          ROLE_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , ROLE_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) DEFAULT 'P'
        , ACTV_FRM_DT TIMESTAMP
        , ACTV_TO_DT TIMESTAMP
        , LAST_UPDT_DT TIMESTAMP
    , CONSTRAINT KRIM_ROLE_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_ROLE_MBR_T
    ADD CONSTRAINT KRIM_ROLE_MBR_TP1
PRIMARY KEY (ROLE_MBR_ID)
/

ALTER TABLE KRIM_ROLE_MBR_T
    ADD CONSTRAINT KRIM_ROLE_MBR_TR1 FOREIGN KEY (ROLE_ID)
    REFERENCES KRIM_ROLE_T (ROLE_ID)
ON DELETE CASCADE
/


CREATE TABLE KRIM_GRP_MBR_T
(
          GRP_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , GRP_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) DEFAULT 'P'
        , ACTV_FRM_DT TIMESTAMP
        , ACTV_TO_DT TIMESTAMP
        , LAST_UPDT_DT TIMESTAMP
    , CONSTRAINT KRIM_GRP_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_GRP_MBR_T
    ADD CONSTRAINT KRIM_GRP_MBR_TP1
PRIMARY KEY (GRP_MBR_ID)
/

ALTER TABLE KRIM_GRP_MBR_T
    ADD CONSTRAINT KRIM_GRP_MBR_TR1 FOREIGN KEY (GRP_ID)
    REFERENCES KRIM_GRP_T (GRP_ID)
ON DELETE CASCADE
/






CREATE TABLE KRIM_DLGN_MBR_T
(
          DLGN_MBR_ID VARCHAR2(40)
        , VER_NBR NUMBER(8) NOT NULL
        , OBJ_ID VARCHAR2(36) NOT NULL
        , DLGN_ID VARCHAR2(40)
        , MBR_ID VARCHAR2(40)
        , MBR_TYP_CD CHAR(1) DEFAULT 'P'
        , ACTV_FRM_DT TIMESTAMP
        , ACTV_TO_DT TIMESTAMP
        , LAST_UPDT_DT TIMESTAMP
    , CONSTRAINT KRIM_DLGN_MBR_TC0 UNIQUE (OBJ_ID)

)
/

ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TP1
PRIMARY KEY (DLGN_MBR_ID)
/

ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TR1 FOREIGN KEY (DLGN_ID)
    REFERENCES KRIM_DLGN_T (DLGN_ID)
ON DELETE CASCADE
/





---------------------------------
-- import all group records to the new table

INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, VER_NBR, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    (SELECT GRP_MBR_ID, VER_NBR, OBJ_ID, GRP_ID, PRNCPL_ID, 'P', ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT
        FROM KRIM_GRP_PRNCPL_T
    )
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, VER_NBR, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT) 
    (SELECT GRP_MBR_ID, VER_NBR, OBJ_ID, GRP_ID, MBR_GRP_ID, 'G', ACTV_FRM_DT, ACTV_TO_DT, LAST_UPDT_DT
        FROM KRIM_GRP_GRP_T
    )
/
COMMIT
/

---------------------------------
-- import all role records to the new table
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, PRNCPL_ID, 'P', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_ROLE_PRNCPL_T
    )
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, GRP_ID, 'G', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_ROLE_GRP_T
    )
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ROLE_ID, 'R', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_ROLE_ROLE_T
    )
/
COMMIT
/
SELECT * FROM KRIM_ROLE_MBR_T
/
---------------------------------
-- import all delegation records to the new table

INSERT INTO KRIM_DLGN_MBR_T(DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, PRNCPL_ID, 'P', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_DLGN_PRNCPL_T
    )
/
INSERT INTO KRIM_DLGN_MBR_T(DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, GRP_ID, 'G', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_DLGN_GRP_T
    )
/
INSERT INTO KRIM_DLGN_MBR_T(DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, MBR_ID, MBR_TYP_CD, ACTV_FRM_DT, ACTV_TO_DT) 
    (SELECT -DLGN_MBR_ID, VER_NBR, OBJ_ID, DLGN_ID, ROLE_ID, 'R', ACTV_FRM_DT, ACTV_TO_DT
        FROM KRIM_DLGN_ROLE_T
    )
/
COMMIT
/
SELECT * FROM KRIM_DLGN_MBR_T
