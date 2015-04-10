ALTER SEQUENCE SEQ_EG_BNDRY RENAME TO SEQ_EG_BOUNDARY;

ALTER TABLE EG_BOUNDARY RENAME COLUMN ID_BNDRY TO ID;
ALTER TABLE EG_BOUNDARY RENAME COLUMN LNG TO LONGITUDE;
ALTER TABLE EG_BOUNDARY RENAME COLUMN LAT TO LATITUDE;
ALTER TABLE EG_BOUNDARY RENAME COLUMN parent_id TO parent;
ALTER TABLE EG_BOUNDARY RENAME COLUMN bndry_num TO boundarynum;
ALTER TABLE EG_BOUNDARY RENAME COLUMN id_bndry_type TO boundarytype;
ALTER TABLE EG_BOUNDARY RENAME COLUMN bndry_name_local TO boundaryNameLocal;
ALTER TABLE EG_BOUNDARY RENAME COLUMN materialized_path to materializedpath;

ALTER TABLE EG_BOUNDARY DROP column updatedtime;

ALTER TABLE EG_BOUNDARY ADD COLUMN ishistory BOOLEAN;
UPDATE EG_BOUNDARY SET ISHISTORY = TRUE WHERE IS_HISTORY = 'Y';
UPDATE EG_BOUNDARY SET ISHISTORY = FALSE WHERE IS_HISTORY = 'N';
ALTER TABLE EG_BOUNDARY DROP COLUMN is_history;

ALTER TABLE EG_BOUNDARY ADD COLUMN createddate TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE EG_BOUNDARY ADD COLUMN lastmodifieddate TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE EG_BOUNDARY ADD COLUMN createdby BIGINT;
ALTER TABLE EG_BOUNDARY ADD COLUMN lastmodifiedby BIGINT;
ALTER TABLE EG_BOUNDARY ADD COLUMN version BIGINT;

ALTER SEQUENCE SEQ_EG_CITY RENAME TO SEQ_EG_CITY_WEBSITE;

ALTER TABLE EG_CITY_WEBSITE RENAME COLUMN bndryid TO boundary;
ALTER TABLE EG_CITY_WEBSITE RENAME COLUMN url TO citybaseurl;

alter table eg_city_website drop column isactive;

ALTER TABLE EG_CITY_WEBSITE ADD COLUMN ISACTIVE BOOLEAN;
ALTER TABLE EG_CITY_WEBSITE ADD COLUMN version BIGINT;


UPDATE EG_CITY_WEBSITE set isactive=true;



﻿--ROLLBACK ALTER SEQUENCE SEQ_EG_BOUNDARY RENAME TO SEQ_EG_BNDRY;

--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN ID TO ID_BNDRY;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN LONGITUDE TO LNG;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN LATITUDE TO LAT;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN parent TO parent_id;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN boundarynum TO bndry_num;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN boundarytype TO id_bndry_type;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN boundaryNameLocal TO bndry_name_local;
--ROLLBACK ALTER TABLE EG_BOUNDARY RENAME COLUMN materializedpath to materialized_path;

--ROLLBACK ALTER TABLE EG_BOUNDARY ADD column updatedtime TIMESTAMP WITHOUT TIME ZONE;

--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN createddate;
--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN lastmodifieddate;
--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN createdby;
--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN lastmodifiedby;
--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN version;

--ROLLBACK ALTER TABLE EG_BOUNDARY ADD COLUMN is_history bigint;
--ROLLBACK UPDATE EG_BOUNDARY SET IS_HISTORY = 'Y' WHERE ISHISTORY = TRUE;
--ROLLBACK UPDATE EG_BOUNDARY SET IS_HISTORY = 'N' WHERE ISHISTORY = FALSE;
--ROLLBACK ALTER TABLE EG_BOUNDARY DROP COLUMN ishistory;

--ROLLBACK ALTER SEQUENCE SEQ_EG_CITY_WEBSITE RENAME TO SEQ_EG_CITY;

--ROLLBACK ALTER TABLE EG_CITY_WEBSITE RENAME COLUMN boundary TO bndryid;
--ROLLBACK ALTER TABLE EG_CITY_WEBSITE RENAME COLUMN citybaseurl TO url;

--ROLLBACK ALTER TABLE EG_CITY_WEBSITE DROP COLUMN ISACTIVE
--ROLLBACK ALTER TABLE EG_CITY_WEBSITE DROP COLUMN version

--ROLLBACK ALTER TABLE EG_CITY_WEBSITE ADD COLUMN ISACTIVE BIGINT;

--ROLLBACK UPDATE EG_CITY_WEBSITE set isactive=1;