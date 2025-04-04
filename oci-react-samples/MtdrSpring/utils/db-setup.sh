#!/bin/bash
# Copyright (c) 2022 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

# Fail on error
set -e


# Create Object Store Bucket (Should be replaced by terraform one day)
while ! state_done OBJECT_STORE_BUCKET; do
  echo "Checking object storage bucket"
#  oci os bucket create --compartment-id "$(state_get COMPARTMENT_OCID)" --name "$(state_get RUN_NAME)"
  if oci os bucket get --name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)"; then
    state_set_done OBJECT_STORE_BUCKET
    echo "finished checking object storage bucket"
  fi
done


# Wait for Order DB OCID
while ! state_done MTDR_DB_OCID; do
  echo "`date`: Waiting for MTDR_DB_OCID"
  sleep 2
done


# Get Wallet
while ! state_done WALLET_GET; do
  echo "creating wallet"
  cd $MTDRWORKSHOP_LOCATION
  mkdir wallet
  cd wallet
  oci db autonomous-database generate-wallet --autonomous-database-id "$(state_get MTDR_DB_OCID)" --file 'wallet.zip' --password 'Welcome1' --generate-type 'ALL'
  unzip wallet.zip
  cd $MTDRWORKSHOP_LOCATION
  state_set_done WALLET_GET
  echo "finished creating wallet"
done


# Get DB Connection Wallet and to Object Store
while ! state_done CWALLET_SSO_OBJECT; do
  echo "grabbing wallet"
  cd $MTDRWORKSHOP_LOCATION/wallet
  oci os object put --bucket-name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)" --name "cwallet.sso" --file 'cwallet.sso'
  cd $MTDRWORKSHOP_LOCATION
  state_set_done CWALLET_SSO_OBJECT
  echo "done grabbing wallet"
done


# Create Authenticated Link to Wallet
while ! state_done CWALLET_SSO_AUTH_URL; do
  echo "creating authenticated link to wallet"
  ACCESS_URI=`oci os preauth-request create --object-name 'cwallet.sso' --access-type 'ObjectRead' --bucket-name "$(state_get RUN_NAME)-$(state_get MTDR_KEY)" --name 'mtdrworkshop' --time-expires $(date '+%Y-%m-%d' --date '+7 days') --query 'data."access-uri"' --raw-output`
  state_set CWALLET_SSO_AUTH_URL "https://objectstorage.$(state_get REGION).oraclecloud.com${ACCESS_URI}"
  echo "done creating authenticated link to wallet"
done


# Give DB_PASSWORD priority
while ! state_done DB_PASSWORD; do
  echo "Waiting for DB_PASSWORD"
  sleep 5
done


# Create Inventory ATP Bindings
while ! state_done DB_WALLET_SECRET; do
  echo "creating Inventory ATP Bindings"
  cd $MTDRWORKSHOP_LOCATION/wallet
  cat - >sqlnet.ora <<!
WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY="/mtdrworkshop/creds")))
SSL_SERVER_DN_MATCH=yes
!
  if kubectl create -f - -n mtdrworkshop; then
    state_set_done DB_WALLET_SECRET
  else
    echo "Error: Failure to create db-wallet-secret.  Retrying..."
    sleep 5
  fi <<!
apiVersion: v1
data:
  README: $(base64 -w0 README)
  cwallet.sso: $(base64 -w0 cwallet.sso)
  ewallet.p12: $(base64 -w0 ewallet.p12)
  keystore.jks: $(base64 -w0 keystore.jks)
  ojdbc.properties: $(base64 -w0 ojdbc.properties)
  sqlnet.ora: $(base64 -w0 sqlnet.ora)
  tnsnames.ora: $(base64 -w0 tnsnames.ora)
  truststore.jks: $(base64 -w0 truststore.jks)
kind: Secret
metadata:
  name: db-wallet-secret
!
  cd $MTDRWORKSHOP_LOCATION
done


# DB Connection Setup
export TNS_ADMIN=$MTDRWORKSHOP_LOCATION/wallet
cat - >$TNS_ADMIN/sqlnet.ora <<!
WALLET_LOCATION = (SOURCE = (METHOD = file) (METHOD_DATA = (DIRECTORY="$TNS_ADMIN")))
SSL_SERVER_DN_MATCH=yes
!
MTDR_DB_SVC="$(state_get MTDR_DB_NAME)_tp"
TODO_USER=TODOUSER
ORDER_LINK=ORDERTOINVENTORYLINK
ORDER_QUEUE=ORDERQUEUE


# Get DB Password
while true; do
  if DB_PASSWORD=`kubectl get secret dbuser -n mtdrworkshop --template={{.data.dbpassword}} | base64 --decode`; then
    if ! test -z "$DB_PASSWORD"; then
      break
    fi
  fi
  echo "Error: Failed to get DB password.  Retrying..."
  sleep 5
done


# Wait for DB Password to be set in Order DB
while ! state_done MTDR_DB_PASSWORD_SET; do
  echo "`date`: Waiting for MTDR_DB_PASSWORD_SET"
  sleep 2
done


# Order DB User, Objects
while ! state_done TODO_USER; do
  echo "connecting to mtdr database"
  U=$TODO_USER
  SVC=$MTDR_DB_SVC
  sqlplus /nolog <<!
WHENEVER SQLERROR EXIT 1
connect admin/"$DB_PASSWORD"@$SVC
CREATE USER $U IDENTIFIED BY "$DB_PASSWORD" DEFAULT TABLESPACE data QUOTA UNLIMITED ON data;
GRANT CREATE SESSION, CREATE VIEW, CREATE SEQUENCE, CREATE PROCEDURE TO $U;
GRANT CREATE TABLE, CREATE TRIGGER, CREATE TYPE, CREATE MATERIALIZED VIEW TO $U;
GRANT CONNECT, RESOURCE, pdb_dba, SODA_APP to $U;

CREATE TABLE TODOUSER.PROJECT(
    PROJECT_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR2(4000) NOT NULL
);

CREATE TABLE TODOUSER.SPRINT(
    SPRINT_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    PROJECT_ID NUMBER NOT NULL,
    NAME VARCHAR2(4000) NOT NULL,
    START_DATE DATE NOT NULL,
    END_DATE DATE NOT NULL,
    CONSTRAINT SPRINT_FK FOREIGN KEY(PROJECT_ID) 
    REFERENCES TODOUSER.PROJECT(PROJECT_ID) ON DELETE CASCADE
);

CREATE TABLE TODOUSER.EMPLOYEE(
    EMPLOYEE_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR2(4000) NOT NULL,
    MANAGER_ID NUMBER,
    EMAIL VARCHAR2(320) NOT NULL UNIQUE,
    PASSWORD VARCHAR2(255) NOT NULL,
    PROJECT_ID NUMBER NOT NULL,
    TELEGRAM_ID NUMBER(20),
    CONSTRAINT EMPLOYEE_PROJECT_FK FOREIGN KEY(PROJECT_ID)
    REFERENCES TODOUSER.PROJECT(PROJECT_ID) ON DELETE CASCADE
);

ALTER TABLE TODOUSER.EMPLOYEE
ADD CONSTRAINT EMPLOYEE_EMPLOYEE_FK FOREIGN KEY(MANAGER_ID) 
REFERENCES TODOUSER.EMPLOYEE(EMPLOYEE_ID);

CREATE TABLE TODOUSER.TODOITEM(
    TODOITEM_ID NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NAME VARCHAR2(255) NOT NULL,
    STATUS VARCHAR2(20) DEFAULT 'PENDING' CHECK(STATUS IN ('COMPLETED', 'PENDING', 'CANCELLED', 'REVIEWING')),
    MANAGER_ID NUMBER NOT NULL,
    COMPLETION_TS TIMESTAMP WITH TIME ZONE,
    START_DATE TIMESTAMP WITH TIME ZONE,
    DEADLINE TIMESTAMP WITH TIME ZONE,
    SPRINT_ID NUMBER NOT NULL,
    DESCRIPTION VARCHAR2(4000),
    EST_HOURS NUMBER NOT NULL,
    CONSTRAINT TODOITEM_EMPLOYEE_FK FOREIGN KEY(MANAGER_ID) REFERENCES TODOUSER.EMPLOYEE(EMPLOYEE_ID),
    CONSTRAINT TODOITEM_SPRINT_FK FOREIGN KEY(SPRINT_ID) REFERENCES TODOUSER.SPRINT(SPRINT_ID)
);

CREATE TABLE TODOUSER.ASSIGNEDDEV(
    TODOITEM_ID NUMBER NOT NULL,
    EMPLOYEE_ID NUMBER NOT NULL,

    CONSTRAINT PK_ASSIGNEDDEV PRIMARY KEY(TODOITEM_ID, EMPLOYEE_ID),
    CONSTRAINT ASSIGNEDDEV_TODOITEM_FK FOREIGN KEY(TODOITEM_ID) 
    REFERENCES TODOUSER.TODOITEM(TODOITEM_ID) ON DELETE CASCADE,
    CONSTRAINT ASSIGNEDDEV_EMPLOYEE_FK FOREIGN KEY(EMPLOYEE_ID) 
    REFERENCES TODOUSER.EMPLOYEE(EMPLOYEE_ID) ON DELETE CASCADE
);

CREATE TABLE TODOUSER.SUBTODOITEM(
    TODOITEM_ID NUMBER NOT NULL,
    SUBTODOITEM_ID NUMBER NOT NULL,

    CONSTRAINT PK_SUBTODOITEM PRIMARY KEY(TODOITEM_ID, SUBTODOITEM_ID),
    CONSTRAINT SUBTODOITEM_TODOITEM_FK FOREIGN KEY(TODOITEM_ID) 
    REFERENCES TODOUSER.TODOITEM(TODOITEM_ID),
    CONSTRAINT SUBTODOITEM_SUBTODOITEM_FK FOREIGN KEY(SUBTODOITEM_ID) 
    REFERENCES TODOUSER.TODOITEM(TODOITEM_ID)
);

DECLARE 
    v_project_id NUMBER;
    v_sprint_id NUMBER;
    
    v_todoitem_1 NUMBER;
    v_todoitem_2 NUMBER;
    v_todoitem_3 NUMBER;
    v_todoitem_4 NUMBER;
    v_todoitem_5 NUMBER;
    v_todoitem_6 NUMBER;
    v_todoitem_7 NUMBER;
    v_todoitem_8 NUMBER;
    v_todoitem_9 NUMBER;
    
    v_employee_1 NUMBER;
    v_employee_2 NUMBER;
    v_employee_3 NUMBER;
    v_employee_4 NUMBER;
    v_employee_5 NUMBER;
    v_employee_6 NUMBER;
BEGIN
    -- Insert Project and get its ID
    INSERT INTO TODOUSER.PROJECT (NAME) 
    VALUES ('TEAM 11 Oracle Java')
    RETURNING PROJECT_ID INTO v_project_id;

    -- Insert Sprint and get its ID
    INSERT INTO TODOUSER.SPRINT (PROJECT_ID, NAME, START_DATE, END_DATE) 
    VALUES (v_project_id, 'Sprint 1 (week 1 and 2)', TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'))
    RETURNING SPRINT_ID INTO v_sprint_id;

    -- Insert Employees and get their IDs
    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Angel E.', NULL, 'angel@gmail.com', 'AngelPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_1;

    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Victor J.', v_employee_1, 'victor@gmail.com', 'victorPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_2;

    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Karen C.', v_employee_1, 'karen@gmail.com', 'karenPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_3;

    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Carlos M.', v_employee_1, 'carlos@gmail.com', 'carlosPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_4;

    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Milan de A.', v_employee_1, 'milan@gmail.com', 'milanPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_5;

    INSERT INTO TODOUSER.EMPLOYEE (NAME, MANAGER_ID, EMAIL, PASSWORD, PROJECT_ID, TELEGRAM_ID) 
    VALUES ('Valeria A.', v_employee_1, 'valeria@gmail.com', 'valeriaPassword', v_project_id, NULL)
    RETURNING EMPLOYEE_ID INTO v_employee_6;

    -- Insert TODOITEMS and get their IDs
    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Sprint 1 (Project Administration)', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-03-24 16:57:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-01', 'YYYY-MM-DD'), v_sprint_id, 'Dear students, ir', 2)
    RETURNING TODOITEM_ID INTO v_todoitem_1;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Sprint 1 (Challenge) (Ken)', 'PENDING', v_employee_1, TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'), v_sprint_id, 'For M12, this is', 1)
    RETURNING TODOITEM_ID INTO v_todoitem_2;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Sprint 1 (Java) (Rodrigo)', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-04-03 10:57:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-28', 'YYYY-MM-DD'), TO_DATE('2025-04-05', 'YYYY-MM-DD'), v_sprint_id, 'Implementado k', 4)
    RETURNING TODOITEM_ID INTO v_todoitem_3;

    INSERT INTO TODOUSER.TODOITEM(NAME, STATUS, MANAGER_ID, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS)
    VALUES('Sprint 1 (Requirements) (Luis)', 'PENDING', v_employee_1, TO_DATE('2025-03-26', 'YYYY-MM-DD'), TO_DATE('2025-04-08', 'YYYY-MM-DD'), v_sprint_id, 'Definir los sprints con base a las historias de usuario', 2)
    RETURNING TODOITEM_ID INTO v_todoitem_4;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Add task by developer', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-04-01 11:25:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'), v_sprint_id, 'Developer agrega tarea', 4)
    RETURNING TODOITEM_ID INTO v_todoitem_5;

    INSERT INTO TODOUSER.TODOITEM(NAME, STATUS, MANAGER_ID, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS)
    VALUES('Assign task to sprint', 'PENDING', v_employee_1, TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-02', 'YYYY-MM-DD'), v_sprint_id, 'Se asigna todoitem a developer', 2)
    RETURNING TODOITEM_ID INTO v_todoitem_6;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Reconocimiento usuario de Telegram (telefono /id) conexion con telegram', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-04-03 11:00:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'), v_sprint_id, 'Se puede conectar con telegram', 4)
    RETURNING TODOITEM_ID INTO v_todoitem_7;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Complete task', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-04-01 16:52:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'), v_sprint_id, 'Una tarea se marca como completada', 4)
    RETURNING TODOITEM_ID INTO v_todoitem_8;

    INSERT INTO TODOUSER.TODOITEM (NAME, STATUS, MANAGER_ID, COMPLETION_TS, START_DATE, DEADLINE, SPRINT_ID, DESCRIPTION, EST_HOURS) 
    VALUES ('Sprint 1 (Oracle Cloud Infrastructure) (Oswaldo)', 'COMPLETED', v_employee_1, TO_TIMESTAMP_TZ('2025-04-03 17:15:00 -06:00', 'YYYY-MM-DD HH24:MI:SS TZH:TZM'), TO_DATE('2025-03-24', 'YYYY-MM-DD'), TO_DATE('2025-04-04', 'YYYY-MM-DD'), v_sprint_id, 'Avance de proyecto', 8)
    RETURNING TODOITEM_ID INTO v_todoitem_9;

    -- Insert References in Assigned Developers Table
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_1, v_employee_6);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_2, v_employee_4);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_3, v_employee_5);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_4, v_employee_1);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_4, v_employee_2);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_4, v_employee_3);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_4, v_employee_5);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_4, v_employee_6);

    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_9, v_employee_1);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_9, v_employee_3);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_9, v_employee_4);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_9, v_employee_6);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_7, v_employee_6);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_5, v_employee_1);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_5, v_employee_4);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_6, v_employee_3);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_8, v_employee_3);
    INSERT INTO TODOUSER.ASSIGNEDDEV (TODOITEM_ID, EMPLOYEE_ID) VALUES (v_todoitem_8, v_employee_4);

    -- Insert Subtask Relationships
    INSERT INTO TODOUSER.SUBTODOITEM (TODOITEM_ID, SUBTODOITEM_ID) VALUES (v_todoitem_9, v_todoitem_5);
    INSERT INTO TODOUSER.SUBTODOITEM (TODOITEM_ID, SUBTODOITEM_ID) VALUES (v_todoitem_9, v_todoitem_6);
    INSERT INTO TODOUSER.SUBTODOITEM (TODOITEM_ID, SUBTODOITEM_ID) VALUES (v_todoitem_9, v_todoitem_7);
    INSERT INTO TODOUSER.SUBTODOITEM (TODOITEM_ID, SUBTODOITEM_ID) VALUES (v_todoitem_9, v_todoitem_8);

END;

!
  state_set_done TODO_USER
  echo "finished connecting to database and creating attributes"
done
# DB Setup Done
state_set_done DB_SETUP