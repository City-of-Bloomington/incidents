alter table users drop password;
alter table users drop authenticationMethod;
alter table users add  inactive char(1);

alter table roles rename column role to name;

-- Actions
rename table statuses to actions;
alter table actions add nextstep tinyint unsigned;
update actions set nextstep=2 where name='received';
update actions set nextstep=2 where name='confirmed';
update actions set nextstep=3 where name='approved';

set foreign_key_checks=0;
update actions        set id=id+1 order by id desc;
update incidents      set status_id=status_id+1;
update status_history set status_id=status_id+1;
set foreign_key_checks=1;
insert actions set id=1,name='emailed',description='Incomplete, Email Sent',workflow_step=1;

rename table status_history to action_logs;
alter table action_logs rename column status_id to action_id;
delete from action_logs where incident_id not in (select id from incidents);
alter table action_logs add foreign key(incident_id) references incidents(id);

rename table status_roles to action_roles;
alter table action_roles rename column status_id to action_id;
truncate table action_roles;
insert into action_roles
values (3,1),
       (4,1),
       (5,1),
       (3,2),
       (4,2),
       (5,3);

-- Damage Types
rename table carDamageTypes to car_damage_types;
alter table car_damage_types rename column type to name;

rename table damageTypes to damage_types;
alter table damage_types rename column type to name;
alter table damage_types add inactive char(1);
update damage_types set inactive='y' where id=3;

-- Incidents
rename table incidentTypes to incident_types;
alter table incident_types change type name varchar(70) not null;

alter table incidents rename column cfsNumber       to case_number;
alter table incidents rename column incidentType_id to incident_type_id;
alter table incidents rename column status_id       to action_id;
alter table incidents rename column dateDescription to date_description;
alter table incidents rename column haveMedia       to have_media;
alter table incidents modify entry_type enum('Unlocked vehicle','Broke window','Pried window','Pried door','Other specify','Damaged window','Damaged door','Unlocked door');

-- Addresses
create table addresses (
    id           int unsigned not null primary key auto_increment,
    name         varchar(80)  not null unique,
    latitude     decimal(15,10),
    longitude    decimal(15,10),
    city         varchar(80),
    state        varchar(2),
    zipcode      varchar(15),
    jurisdiction varchar(80),
    address_id   int unsigned,
    subunit_id   int unsigned,
    invalid_address char(1)
);

insert into addresses (name, city, state, zipcode, invalid_address)
select upper(address), city, state, zip, invalidAddress from incidents i
on duplicate key update city=i.city, state=i.state, zipcode=i.zip, invalid_address=i.invalidAddress;

alter table incidents add address_id int unsigned after date;
alter table incidents add foreign key (address_id) references addresses(id);
update incidents i set i.address_id=(select a.id from addresses a where a.name=i.address);

alter table incidents drop column address;
alter table incidents drop column city;
alter table incidents drop column state;
alter table incidents drop column zip;
alter table incidents drop column invalidAddress;
alter table incidents add evidence text after details;


-- Media
alter table media rename column name to file_name;

-- Persons
rename table personTypes to person_types;
alter table person_types rename column type to name;
update person_types set name='Complainant' where id=2;

alter table persons rename column personType_id to person_type_id;
alter table persons rename column phonetype     to phone_type;
alter table persons add phone_type2 enum('Cell', 'Home', 'Work') after phone_type;
alter table persons rename column driverLicenseNumber  to dln;
alter table persons rename column dateOfBirth          to dob;
alter table persons rename column socialSecurityNumber to ssn;
alter table persons rename column heightFeet to height_feet;
alter table persons rename column heightInch to height_inch;
alter table persons modify sex enum('Male', 'Female', 'Nonbinary', 'Unknown');
alter table persons add gender enum('Male', 'Female', 'Trangender') after sex;
alter table persons add ethnicity enum('Hispanic','Non-hispanic','Unknown') after gender;

create table race_types (
    id   int unsigned not null primary key auto_increment,
    name varchar(50)  not null unique
);
insert into race_types values( 1, 'Black Non-Hisp'),
                             ( 2, 'Hawaiian/Oth Pacific Hispanic'),
                             ( 3, 'Hawaiian/Oth Pacific Non-Hisp'),
                             ( 4, 'Indian/Alaskan Natv Non-Hisp'),
                             ( 5, 'Black Hispanic'),
                             ( 6, 'White Hispanci'),
                             ( 7, 'Indian/Alaskan Natv Hispanci'),
                             ( 8, 'Asian Non-Hisp'),
                             ( 9, 'Asian Hispanic'),
                             (10, 'Unknown'),
                             (11, 'White Non-Hisp');

alter table persons add race_type_id int unsigned after race;
alter table persons add foreign key (race_type_id) references race_types(id);
update persons set race_type_id =  1 where race = 3;
update persons set race_type_id = 11 where race = 1;
update persons set race_type_id =  6 where race = 2;
update persons set race_type_id =  8 where race = 5;
update persons set race_type_id =  4 where race = 4;
update persons set race_type_id = 10 where race = 6;
alter table persons drop column race;

-- Properties
alter table properties rename column damageType_id to damage_type_id;
alter table properties rename column serialNum     to serial_num;

-- Vehicles
alter table vehicles rename column carDamageType_id   to car_damage_type_id;
alter table vehicles rename column licensePlateNumber to plate_number;
alter table vehicles rename column licensePlateYear   to plate_year;
alter table vehicles add value decimal(10, 2);


-- ----------
-- New Tables
-- ----------
create table role_actions (
    role_id   int unsigned not null,
    action_id int unsigned not null,
    primary key (role_id, action_id),
    foreign key (role_id  ) references   roles(id),
    foreign key (action_id) references actions(id)
);

create table fraud_types (
    id   int unsigned not null primary key auto_increment,
    name varchar(50)  not null unique
);
insert into fraud_types values
(1,'Credit Card Fraud'),
(2,'Impersonation'),
(3,'Welfare Fraud'),
(4,'Wire Fraud'),
(5,'Identity Theft'),
(6,'Hacking/Computer Invasion'),
(7,'Other Specify');

create table frauds (
    id            int unsigned not null primary key auto_increment,
    incident_id   int unsigned not null,
    fraud_type_id int unsigned,
    other_type    varchar(80) default null,
    identity_used text,
    account_used  text,
    amount_taken  decimal(12,2) default null,
    details       text,
    foreign key (incident_id)   references incidents(id),
    foreign key (fraud_type_id) references fraud_types(id)
);

-- received but not confirmed
create or replace view incident_received as
select i.id
from incidents i, action_logs l
where l.incident_id=i.id
  and l.action_id = 2
  and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id)
order by i.id desc;

-- confirmed
create or replace view incident_confirmed as
select i.id
from incidents i, action_logs l
where l.incident_id=i.id
  and l.action_id = 3
  and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id);

-- approved
create or replace view incident_approved as
select i.id
from incidents i, action_logs l
where l.incident_id=i.id
  and l.action_id = 4
  and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id);

-- rejected (not used)
create or replace view incident_rejected as
select i.id
from incidents i, action_logs l
where l.incident_id=i.id
  and l.action_id = 5
  and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id)
order by i.id desc;

-- processed
create or replace view incident_processed as
select i.id
from incidents i, action_logs l
where l.incident_id=i.id
  and l.action_id = 6
  and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id);

-- incomplete incidents are those started but not submitted
create or replace view incident_incomplete as
select i.id
from incidents i
where i.address_id is not null
  and i.details    is not null
  and 0 = (select count(*) from action_logs l where l.incident_id=i.id)
order by i.id desc;

-- WS added temp to fix hibernate error not find USERS table
create table USERS select * from users;

-- Quartz
CREATE TABLE QRTZ_JOB_DETAILS(
    SCHED_NAME        VARCHAR(120) NOT NULL,
    JOB_NAME          VARCHAR(190) NOT NULL,
    JOB_GROUP         VARCHAR(190) NOT NULL,
    DESCRIPTION       VARCHAR(250),
    JOB_CLASS_NAME    VARCHAR(250) NOT NULL,
    IS_DURABLE        VARCHAR(1)   NOT NULL,
    IS_NONCONCURRENT  VARCHAR(1)   NOT NULL,
    IS_UPDATE_DATA    VARCHAR(1)   NOT NULL,
    REQUESTS_RECOVERY VARCHAR(1)   NOT NULL,
    JOB_DATA          BLOB NULL,
    PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS (
    SCHED_NAME     VARCHAR(120) NOT NULL,
    TRIGGER_NAME   VARCHAR(190) NOT NULL,
    TRIGGER_GROUP  VARCHAR(190) NOT NULL,
    JOB_NAME       VARCHAR(190) NOT NULL,
    JOB_GROUP      VARCHAR(190) NOT NULL,
    DESCRIPTION    VARCHAR(250) NULL,
    NEXT_FIRE_TIME BIGINT(13) NULL,
    PREV_FIRE_TIME BIGINT(13) NULL,
    PRIORITY       INTEGER NULL,
    TRIGGER_STATE  VARCHAR(16) NOT NULL,
    TRIGGER_TYPE   VARCHAR(8) NOT NULL,
    START_TIME     BIGINT(13) NOT NULL,
    END_TIME       BIGINT(13) NULL,
    CALENDAR_NAME  VARCHAR(190) NULL,
    MISFIRE_INSTR  SMALLINT(2) NULL,
    JOB_DATA BLOB NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
    REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS (
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(190) NOT NULL,
    TRIGGER_GROUP   VARCHAR(190) NOT NULL,
    REPEAT_COUNT    BIGINT(7)    NOT NULL,
    REPEAT_INTERVAL BIGINT(12)   NOT NULL,
    TIMES_TRIGGERED BIGINT(10)   NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS (
    SCHED_NAME      VARCHAR(120) NOT NULL,
    TRIGGER_NAME    VARCHAR(190) NOT NULL,
    TRIGGER_GROUP   VARCHAR(190) NOT NULL,
    CRON_EXPRESSION VARCHAR(120) NOT NULL,
    TIME_ZONE_ID    VARCHAR(80),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS (
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_NAME  VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    STR_PROP_1    VARCHAR(512),
    STR_PROP_2    VARCHAR(512),
    STR_PROP_3    VARCHAR(512),
    INT_PROP_1    INT,
    INT_PROP_2    INT,
    LONG_PROP_1   BIGINT,
    LONG_PROP_2   BIGINT,
    DEC_PROP_1    NUMERIC(13,4),
    DEC_PROP_2    NUMERIC(13,4),
    BOOL_PROP_1   VARCHAR(1),
    BOOL_PROP_2   VARCHAR(1),
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS (
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_NAME  VARCHAR(190) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    BLOB_DATA     BLOB,
    PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
    INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
    FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
    REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS (
    SCHED_NAME    VARCHAR(120) NOT NULL,
    CALENDAR_NAME VARCHAR(190) NOT NULL,
    CALENDAR      BLOB         NOT NULL,
    PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS (
    SCHED_NAME    VARCHAR(120) NOT NULL,
    TRIGGER_GROUP VARCHAR(190) NOT NULL,
    PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_FIRED_TRIGGERS (
    SCHED_NAME        VARCHAR(120) NOT NULL,
    ENTRY_ID          VARCHAR(95)  NOT NULL,
    TRIGGER_NAME      VARCHAR(190) NOT NULL,
    TRIGGER_GROUP     VARCHAR(190) NOT NULL,
    INSTANCE_NAME     VARCHAR(190) NOT NULL,
    FIRED_TIME        BIGINT(13)   NOT NULL,
    SCHED_TIME        BIGINT(13)   NOT NULL,
    PRIORITY          INTEGER      NOT NULL,
    STATE             VARCHAR(16)  NOT NULL,
    JOB_NAME          VARCHAR(190),
    JOB_GROUP         VARCHAR(190),
    IS_NONCONCURRENT  VARCHAR(1),
    REQUESTS_RECOVERY VARCHAR(1),
    PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);

CREATE TABLE QRTZ_SCHEDULER_STATE (
    SCHED_NAME        VARCHAR(120) NOT NULL,
    INSTANCE_NAME     VARCHAR(190) NOT NULL,
    LAST_CHECKIN_TIME BIGINT(13)   NOT NULL,
    CHECKIN_INTERVAL  BIGINT(13)   NOT NULL,
    PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS (
    SCHED_NAME VARCHAR(120) NOT NULL,
    LOCK_NAME  VARCHAR(40)  NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

-- Spring Sessions
CREATE TABLE SPRING_SESSION (
    PRIMARY_ID            CHAR(36)   NOT NULL PRIMARY KEY,
    SESSION_ID            CHAR(36)   NOT NULL UNIQUE,
    CREATION_TIME         BIGINT(20) NOT NULL,
    LAST_ACCESS_TIME      BIGINT(20) NOT NULL,
    MAX_INACTIVE_INTERVAL INT(11)    NOT NULL,
    EXPIRY_TIME           BIGINT(20) NOT NULL,
    PRINCIPAL_NAME        VARCHAR(100),
    KEY SPRING_SESSION_IX2 (EXPIRY_TIME),
    KEY SPRING_SESSION_IX3 (PRINCIPAL_NAME)
);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36)     NOT NULL,
    ATTRIBUTE_NAME     VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES    BLOB         NOT NULL,
    PRIMARY KEY (SESSION_PRIMARY_ID,ATTRIBUTE_NAME),
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION (PRIMARY_ID) ON DELETE CASCADE
);
