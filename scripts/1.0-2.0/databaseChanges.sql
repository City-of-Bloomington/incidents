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
select address, city, state, zip, invalidAddress from incidents i
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
    id int unsigned not null primary key auto_increment,
    name varchar(50) not null unique
);
insert into race_types (name) select distinct race from persons where race is not null;
alter table persons add race_type_id int unsigned after race;
alter table persons add foreign key (race_type_id) references race_types(id);
update persons set race_type_id=(select id from race_types where name=race);
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
    id int unsigned not null primary key auto_increment,
    name varchar(50) not null unique
);

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


-- Quartz
create table qrtz_job_details(
    sched_name        varchar(120) not null,
    job_name          varchar(190) not null,
    job_group         varchar(190) not null,
    description       varchar(250),
    job_class_name    varchar(250) not null,
    is_durable        varchar(1)   not null,
    is_nonconcurrent  varchar(1)   not null,
    is_update_data    varchar(1)   not null,
    requests_recovery varchar(1)   not null,
    job_data          blob null,
    primary key (sched_name,job_name,job_group)
);

create table qrtz_triggers (
    sched_name     varchar(120) not null,
    trigger_name   varchar(190) not null,
    trigger_group  varchar(190) not null,
    job_name       varchar(190) not null,
    job_group      varchar(190) not null,
    description    varchar(250) null,
    next_fire_time bigint(13) null,
    prev_fire_time bigint(13) null,
    priority       integer null,
    trigger_state  varchar(16) not null,
    trigger_type   varchar(8) not null,
    start_time     bigint(13) not null,
    end_time       bigint(13) null,
    calendar_name  varchar(190) null,
    misfire_instr  smallint(2) null,
    job_data blob null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,job_name,job_group)
    references qrtz_job_details(sched_name,job_name,job_group)
);

create table qrtz_simple_triggers (
    sched_name      varchar(120) not null,
    trigger_name    varchar(190) not null,
    trigger_group   varchar(190) not null,
    repeat_count    bigint(7)    not null,
    repeat_interval bigint(12)   not null,
    times_triggered bigint(10)   not null,
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

create table qrtz_cron_triggers (
    sched_name      varchar(120) not null,
    trigger_name    varchar(190) not null,
    trigger_group   varchar(190) not null,
    cron_expression varchar(120) not null,
    time_zone_id    varchar(80),
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

create table qrtz_simprop_triggers (
    sched_name    varchar(120) not null,
    trigger_name  varchar(190) not null,
    trigger_group varchar(190) not null,
    str_prop_1    varchar(512),
    str_prop_2    varchar(512),
    str_prop_3    varchar(512),
    int_prop_1    int,
    int_prop_2    int,
    long_prop_1   bigint,
    long_prop_2   bigint,
    dec_prop_1    numeric(13,4),
    dec_prop_2    numeric(13,4),
    bool_prop_1   varchar(1),
    bool_prop_2   varchar(1),
    primary key (sched_name,trigger_name,trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

create table qrtz_blob_triggers (
    sched_name    varchar(120) not null,
    trigger_name  varchar(190) not null,
    trigger_group varchar(190) not null,
    blob_data     blob,
    primary key (sched_name,trigger_name,trigger_group),
    index (sched_name,trigger_name, trigger_group),
    foreign key (sched_name,trigger_name,trigger_group)
    references qrtz_triggers(sched_name,trigger_name,trigger_group)
);

create table qrtz_calendars (
    sched_name    varchar(120) not null,
    calendar_name varchar(190) not null,
    calendar      blob         not null,
    primary key (sched_name,calendar_name)
);

create table qrtz_paused_trigger_grps (
    sched_name    varchar(120) not null,
    trigger_group varchar(190) not null,
    primary key (sched_name,trigger_group)
);

create table qrtz_fired_triggers (
    sched_name        varchar(120) not null,
    entry_id          varchar(95)  not null,
    trigger_name      varchar(190) not null,
    trigger_group     varchar(190) not null,
    instance_name     varchar(190) not null,
    fired_time        bigint(13)   not null,
    sched_time        bigint(13)   not null,
    priority          integer      not null,
    state             varchar(16)  not null,
    job_name          varchar(190),
    job_group         varchar(190),
    is_nonconcurrent  varchar(1),
    requests_recovery varchar(1),
    primary key (sched_name,entry_id)
);

create table qrtz_scheduler_state (
    sched_name        varchar(120) not null,
    instance_name     varchar(190) not null,
    last_checkin_time bigint(13)   not null,
    checkin_interval  bigint(13)   not null,
    primary key (sched_name,instance_name)
);

create table qrtz_locks (
    sched_name varchar(120) not null,
    lock_name  varchar(40)  not null,
    primary key (sched_name,lock_name)
);

-- Spring Sessions
create table spring_session (
    primary_id            char(36)   not null primary key,
    session_id            char(36)   not null unique,
    creation_time         bigint(20) not null,
    last_access_time      bigint(20) not null,
    max_inactive_interval int(11)    not null,
    expiry_time           bigint(20) not null,
    principal_name        varchar(100),
    key spring_session_ix2 (expiry_time),
    key spring_session_ix3 (principal_name)
);

create table spring_session_attributes (
    session_primary_id char(36)     not null,
    attribute_name     varchar(200) not null,
    attribute_bytes    blob         not null,
    primary key (session_primary_id,attribute_name),
    constraint spring_session_attributes_fk foreign key (session_primary_id) references spring_session (primary_id) on delete cascade
);

