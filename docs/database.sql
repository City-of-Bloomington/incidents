;;
;; databse incident_types
;; old name inidenttypes
;;
rename table incidenttypes to incident_types;		
create table incident_types(
			 id int unsigned auto_increment primary key,
			 name varchar(70)
			 )engine=InnoDB;

;;
;; change column name from type to name;
alter table incident_types change type name varchar(70);
;;
 rename table cardamagetypes to car_damage_types;			
create table car_damage_types(
			 id int unsigned auto_increment primary key,
			 name varchar(70)
			 )engine=InnoDB;
;;
;; change column name from type to name;
alter table car_damage_types change type name varchar(70);
;;
rename table persontypes to person_types;			
create table person_types(
			 id int unsigned auto_increment primary key,
			 name varchar(70)
			 )engine=InnoDB;

alter table person_types change type name varchar(70);
;;

create table damage_types(
			 id int unsigned auto_increment primary key,
			 name varchar(70)
			 )engine=InnoDB;
 alter table damagetypes change type name varchar(70);			 
 rename table damagetypes to damage_types;
 
;;
create table properties(
			 id int unsigned auto_increment primary key,
			 incident_id int not null,
			 damage_type_id int unsigned,
			 brand varchar(70),
			 model varchar(70),
			 value decimal(10,2),
			 serial_num varchar(70),
			 owner  varchar(70),
			 description text,
			 foreign key(incident_id) references incidents(id),
			 foreign kye(damage_type,id) references damage_types(id)
			 )engine=InnoDB;
 alter table properties drop foreign key properties_ibfk_1;
 alter table properties change column damageType_id damage_type_id int unsigned;
 alter table properties add foreign key(damage_type_id) references damage_types(id);
 alter table properties change column serialNum serial_num varchar(70);

create table persons(
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `incident_id` int(10) unsigned NOT NULL,
  `person_type_id` int(10) unsigned DEFAULT NULL,
  `title` enum('Mr','Ms','Mrs') DEFAULT NULL,
  `firstname` varchar(70) NOT NULL,
  `lastname` varchar(70) NOT NULL,
  `midname` varchar(2) DEFAULT NULL,
  `suffix` varchar(10) DEFAULT NULL,
  `address` varchar(70) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` char(2) DEFAULT NULL,
  `zip` varchar(10) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `phone2` varchar(20) DEFAULT NULL,
  `phone_type` enum('Cell','Home','Work') DEFAULT NULL,
  `phone_type2` enum('Cell','Home','Work') DEFAULT NULL,	
  `email` varchar(70) DEFAULT NULL,
  `email2` varchar(70) DEFAULT NULL,
  `dln` varchar(20) DEFAULT NULL,
  `dof` date DEFAULT NULL,
  `ssn` varchar(11) DEFAULT NULL,
  `race` enum('Caucasion','Hispanic','African American','Native American','Asian','Other') DEFAULT NULL,
  `height_feet` int(10) unsigned DEFAULT NULL,
  `height_inch` int(10) unsigned DEFAULT NULL,
  `weight` varchar(25) DEFAULT NULL,
  `sex` enum('Male','Female','Other') DEFAULT NULL,
  `occupation` varchar(128) DEFAULT NULL,
  `reporter` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `incident_id` (`incident_id`),
  KEY `person_type_id` (`person_type_id`),
  CONSTRAINT `persons_ibfk_1` FOREIGN KEY (`incident_id`) REFERENCES `incidents` (`id`),
  CONSTRAINT `persons_ibfk_2` FOREIGN KEY (`person_type_id`) REFERENCES `person_types` (`id`)
) ENGINE=InnoDB;
;;
alter table persons change driverLicenseNumber dln varchar(20);
alter table persons change dateOfBirth dob date;
alter table persons change socialSecurityNumber ssn varchar(20);
alter table persons modify sex enum('Male','Female','Other');
alter table persons change heightFeet height_feet int unsigned;
alter table persons change heightInch height_inch int unsigned;

alter table drop persons foreign key persons_ibfk_2;
alter table persons change personType_id person_type_id int unsigned;
alter table persons add foreign key(person_type_id) references person_types(id);
alter table persons add phone_type2 enum('Cell','Home','Work') after phone_type; 

	;;
;; 
;;
create table users(
			 id int unsigned auto_increment primary key,
			 username varchar(70) not null unique,
			 firstname varchar(70),
			 lastname varchar(70) not null,
			 inactive char(1)
			 )engine=InnoDB;

			 alter table users drop column password;
			 alter table users drop column authenticationMethod;
			 alter table users add inactive char(1);

create table roles(
			 id int unsigned auto_increment primary key,
			 name varchar(28) not null
			 )engine=InnoDB;
alter table roles change role name varchar(28);

create table user_roles(
			 user_id int unsigned not null,
			 role_id int unsigned not null,
			 FOREIGN KEY (user_id) REFERENCES users (id),
			 FOREIGN KEY (role_id) REFERENCES roles (id),
			 unique(user_id, role_id)
			 )engine=InnoDB;

	
create table requests(
			 id int unsigned auto_increment primary key,
			 hash varchar(32) not null,
			 confirmed char(1),
			 expires datetime,
			 )engine=InnoDB;
;;
;;
;; no need to do change, because the database is saved in date time
;;			 update requests set expires2 = FROM_UNIXTIME(expires);
			 
create table actions(                                                                  id int unsigned auto_increment primary key,                                     name varchar(32) not null,                                                      description varchar(256),                                                       workflow_step tinyint unsigned                                                  )engine=InnoDB;

insert into actions select * from statuses;

;; later drop the old statuses table

  create table role_actions(                                                           role_id int unsigned not null,                                                  action_id int unsigned not null,                                                 FOREIGN KEY (action_id) REFERENCES actions (id),                                FOREIGN KEY (role_id) REFERENCES roles (id),                                    primary key(role_id, action_id)                                               )engine=InnoDB;	

	insert into role_actions select role_id,action_id from action_roles;
	
;;
;; drop tables statuses and status_roles
;;


  create table action_logs(                                                             id int unsigned auto_increment primary key,                                     incident_id int unsigned not null,                                              date datetime,                                                                  action_id int unsigned,                                                         user_id int unsigned,                                                           comments text,                                                                  FOREIGN KEY (incident_id) REFERENCES incidents (id),                            FOREIGN KEY (action_id) REFERENCES actions (id),                                FOREIGN KEY (user_id) REFERENCES users (id)		                                 )engine=InnoDB;			 
;;

         delete from status_history where incident_id not in(select id from incidents);
;; copy data				
  insert into action_logs select id,incident_id, FROM_UNIXTIME(date),status_id,user_id,comments from status_history;

;;
;; we needed this table for the first two steps in reporting process
;; and before the complete form is presented to reporter
;;

			 
 create table incidents(
		 id int unsigned auto_increment primary key,
  		 case_number varchar(20) DEFAULT NULL,
  		 incident_type_id int(10) unsigned DEFAULT NULL,
  		 status_id int(10) unsigned DEFAULT NULL,
			 received timestamp NOT NULL,
			 date datetime,
			 address varchar(70) NOT NULL,
			 city varchar(50) NOT NULL,
			 state char(2) NOT NULL,
			 zip varchar(10) DEFAULT NULL,
			 details text,
			 invalid_address char(1) DEFAULT NULL,
			 date_description text,
			 end_date datetime DEFAULT NULL,
			 entry_type enum('Unlocked vehicle','Broke window','Pried window','Pried door','Other specify') DEFAULT NULL,
			 other_entry text,
			 have_media char(1) DEFAULT NULL,
			 email varchar(70) DEFAULT NULL,
			 foreign key(incident_type_id) references incident_types(id)
			 )engine=InnoDB;

  alter table incidents drop foreign key incidents_ibfk_2;
  alter table incidents change column incidentType_id incident_type_id int unsigned;
  alter table incidents add foreign key(incident_type_id) references incident_types(id);
  alter table incidents change column cfsNumber cfs_number varchar(20);
  alter table incidents change column invalidAddress invalid_address char(1);
  alter table incidents change column dateDescription date_description text;
  alter table incidents change column haveMedia have_media char(1);
	alter table incidents modify address varchar(80);
	alter table incidents modify city varchar(60);
  alter table incidents modify state char(2);		
	;;
	;; status_id not needed, we have action_logs instead
	;;
	alter table incidents add column action_id int unsigned after status_id;
	alter table incidents add foreign key(action_id) references actions(id);	
	update incidents set action_id=status_id;
	;;
	;; now we need to drop the foreign key before column
	;;
	alter TABLE incidents DROP FOREIGN KEY incidents_ibfk_1;
	alter table incidents drop column status_id;
	
  create table media_ids(
	  id int unsigned auto_increment primary key
	)engine=InnoDB;
	
  create table media(
	 id int unsigned auto_increment primary key,
	 incident_id int unsigned not null,
	 file_name varchar(80),
	 old_file_name varchar(150),
	 year int unsigned,
	 mime_type varchar(30),
	 notes text,
 	 FOREIGN KEY (incident_id) REFERENCES incidents (id)
	 )engine=InnoDB;	
 alter table media change name file_name varchar(80);
 alter table media modify id int unsigned auto_increment;
 
  create table vehicles(
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `incident_id` int(10) unsigned NOT NULL,
  `car_damage_type_id` int(10) unsigned DEFAULT NULL,
  `year` int(10) unsigned DEFAULT NULL,
  `make` varchar(30) DEFAULT NULL,
  `model` varchar(30) DEFAULT NULL,
  `color` varchar(30) DEFAULT NULL,
  `plate_number` varchar(20) DEFAULT NULL,
  `state` char(2) NOT NULL,
  `plate_year` int(10) unsigned DEFAULT NULL,
  `owner` varchar(70) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`damage_id`) REFERENCES `car_damage_types` (`id`),
  FOREIGN KEY (`incident_id`) REFERENCES `incidents` (`id`)
) ENGINE=InnoDB;
 
  alter table vehicles drop foreign key vehicles_ibfk_1;
  alter table vehicles change carDamageType_id car_damage_type_id int unsigned;
  alter table vehicles change licensePlateNumber plate_number varchar(20);
 alter table vehicles change licensePlateYear plate_year int unsigned;
  alter table vehicles add foreign key(car_damage_type_id) references car_damage_types(id);
	
 
;;
;; drop table status_history 
;; drop table status_roles;
;; drop table statuses;
;;


;;
;; received but not confirmed
;; modified
  create or Replace view incident_received AS                                  select i.id from incidents i,action_logs l where l.incident_id=i.id and l.action_id = 2 and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id and l2.cancelled is null) order by i.id desc;

;;
;; confirmed
;;
  create or Replace view incident_confirmed AS                                  select i.id from incidents i,action_logs l where l.incident_id=i.id and l.action_id = 3 and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id and l2.cancelled is null);

;; approved
     create or Replace view incident_approved AS                                     select i.id from incidents i,action_logs l where l.incident_id=i.id and l.action_id = 4 and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id and l2.cancelled is null);				 
;;
;; rejected (not used)
;;
    create or Replace view incident_rejected AS                                   select i.id from incidents i,action_logs l where l.incident_id=i.id and l.action_id = 5 and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id and l2.cancelled is null) order by i.id desc;
;;
;; processed
     create or Replace view incident_processed AS                                    select i.id from incidents i,action_logs l where l.incident_id=i.id and l.action_id = 6 and l.action_id in (select max(l2.action_id) from action_logs l2 where l2.incident_id=i.id);

;;
;; incomplete incidents are those started but not submitted
;; 
   create or Replace view incident_incomplete AS                                  select i.id from incidents i where i.address_id is not null and i.details is not null and 0 = (select count(*) from action_logs l where l.incident_id=i.id) order by i.id desc;

;;
;; for import, first we do clean up
;;
delete from action_logs;
delete from vehicles;
delete from properties;
delete from media;
delete from incident_initials;
delete from persons;
delete from incidents;
;;
;; now we do import
;; going in reverse order
;;
  insert into incidents select * from incident_reporting.incidents;

;;
  insert into persons (id,incident_id,person_type_id,title,firstname,lastname,midname,suffix,address,city,state,zip,phone,phone2,phone_type,email,email2,dln,dob,ssn,race,height_feet,height_inch,weight,sex,occupation,reporter) select * from incident_reporting.persons;
;;
;;
  insert into properties select * from incident_reporting.properties;
  insert into vehicles select * from incident_reporting.vehicles;
  insert into media select * from incident_reporting.media;
;;
;; find the orphan records in status_history
;;
 select id,incident_id from status_history where incident_id not in(select id from incidents);
;;
;; need to delete these orphan records from status_history;
;;
  delete from status_history where id in (12027,13522,13523,13524,13525,15305,15306,15339,15356,16375,18517,18520);
;;
;;
 insert into action_logs select * from incident_reporting.status_history;


 drop table incident_initials;
 alter table incidents change cfs_number case_number varchar(20);

 update incident_types set name='Theft - All Other' where id=1;
 insert into incident_types values(4,'Theft - From Vehicle');
 insert into incident_types values(5,'Theft - From Building');
 insert into incident_types values(6,'Fraud');

alter table vehicles add value decimal(10,2);

;;
;; actions table
 id | name      | description              | workflow_step |
+----+-----------+--------------------------+---------------+
|  1 | received  | Incident report received |             1 |
|  2 | confirmed | Email address confirmed  |             1 |
|  3 | approved  | Approved                 |             2 |
|  4 | rejected  | Rejected                 |             2 |
|  5 | processed | Processed                |             3 |

 id | name      | description              | workflow_step | nextstep
+----+-----------+--------------------------+---------------+
|  1 | emailed   | Incomplete, Email Sent   |             1 | null
|  2 | received  | Incident report received |             1 | 2 
|  3 | confirmed | Email address confirmed  |             1 | 2
|  4 | approved  | Approved                 |             2 | 3
|  5 | rejected  | Rejected                 |             2 | null
|  6 | processed | Processed                |             3 | null
|  7 | discarded | Discarded                |             1 } null


 alter table action_logs drop foreign key action_logs_ibfk_2;

 update action_logs set action_id=action_id+1;
;;
;; not needed
;;
drop table action_roles; 

show create table role_actions;
alter table role_actions drop foreign key role_actions_ibfk_1;
alter table incidents drop foreign key incidents_ibfk_3

update incidents set action_id=action_id+1;

update actions set id=6 where id=5;
update actions set id=5 where id=4;
update actions set id=4 where id=3;
update actions set id=3 where id=2;
update actions set id=2 where id=1;
insert into actions values(1,'emailed','Incomplete, Email Sent',1)
alter table actions add nextstep tinyint;

| id | name      | description              | workflow_step | nextstep |
+----+-----------+--------------------------+---------------+----------+
|  1 | emailed   | Incomplete, Email Sent   |             1 |     NULL |
|  2 | received  | Incident report received |             1 |     2    |
|  3 | confirmed | Email address confirmed  |             1 |     2    |
|  4 | approved  | Approved                 |             2 |     3    |
|  5 | rejected  | Rejected                 |             2 |     NULL |
|  6 | processed | Processed                |             3 |     NULL |

update actions set nextstep=2 where id in (2,3);
update actions set nextstep=3 where id in (4);

alter table action_logs add foreign key(action_id) references actions(id);
alter table role_actions add foreign key(action_id) references actions(id);
alter table incidents add foreign key(action_id) references actions(id);

old role_actions;
+---------+-----------+
| role_id | action_id |
+---------+-----------+
|       1 |         3 |
|       2 |         3 |
|       1 |         4 |
|       2 |         4 |
|       1 |         5 |
|       3 |         5 |

 update role_actions set action_id=6 where role_id=3 and action_id=5;
 update role_actions set action_id=6 where role_id=1 and action_id=5;
 update role_actions set action_id=5 where role_id=2 and action_id=4;
 update role_actions set action_id=5 where role_id=1 and action_id=4; 
 update role_actions set action_id=4 where role_id=1 and action_id=3;
 update role_actions set action_id=4 where role_id=2 and action_id=3; 

;; after
+---------+-----------+
| role_id | action_id |
+---------+-----------+
|       1 |         4 |
|       2 |         4 |
|       1 |         5 |
|       2 |         5 |
|       1 |         6 |
|       3 |         6 |


;; need to update views
;;
 alter table damage_types add inactive char(1);
 update damage_types set inactive='y' where id=3;

;;
;; remove from person_types suspect from the list, id = 2, name = Suspect
;; 
 delete from person_types where id=2;
;;
;;
  create table race_types(                                                          id int unsigned auto_increment primary key,                                     name varchar(50)                                                                )engine=InnoDB;
;;
;; new tables
;;

  insert into race_types values(1,'Black Non-Hisp'),                                                           (2,'Hawaiian/Oth Pacific Hispanic'),                                            (3,'Hawaiian/Oth Pacific Non-Hisp'),                                            (4,'Indian/Alaskan Natv Non-Hisp'),                                             (5,'Black Hispanic'),                                                           (6,'White Hispanci'),                                                           (7,'Indian/Alaskan Natv Hispanci'),                                             (8,'Asian Non-Hisp'),                                                           (9,'Asian Hispanic'),                                                           (10,'Unknown'),                                                                 (11,'White Non-Hisp');
alter table persons drop foreign key persons_ibfk_3;
;; do insert
alter table persons add foreign key(race_type_id) references race_types(id);
;;
;;
;; in persons table we need to change the field race enum to
;; int unsigned
  alter table persons add race_type_id int unsigned after race;
	alter table persons add foreign key(race_type_id) references race_types(id);
	update persons set race_type_id = 1 where race = 3;
	update persons set race_type_id = 11 where race = 1;
	update persons set race_type_id = 6 where race = 2;
	update persons set race_type_id = 8 where race = 5;		
	update persons set race_type_id = 4 where race = 4;		
	update persons set race_type_id = 10 where race = 6;		

  alter table persons drop column race;
  alter table persons modify sex enum('Male','Female','Nonbinary','Unknown');
	alter table persons add gender enum('Male','Female','Transgender') after sex;
	alter table persons add ethnicity enum('Hispanic','Non-hispanic','Unknown') after gender;

 insert into incident_types values(5,'Theft - From Building');
 insert into incident_types values(6,'Fraud');
 alter table incidents modify entry_type enum('Unlocked vehicle','Broke window','Pried window','Pried door','Other specify','Damaged window','Damaged door','Unlocked door');
 alter table incidents add evidence text after details;

;;
;; added on 8/4/2020
;;
  create table fraud_types(                                                          id int unsigned auto_increment primary key,                                     name varchar(50)                                                                )engine=InnoDB;

 alter table frauds drop foreign key frauds_ibfk_2;
 delete from fraud_types;

insert into fraud_types values
(1,'Credit Card Fraud'),
(2,'Impersonation'),
(3,'Welfare Fraud'),
(4,'Wire Fraud'),
(5,'Identity Theft'),
(6,'Hacking/Computer Invasion'),
(7,'Other Specify')
;;
;; if frauds table created before you may need to add the foreign key back
;;
 alter table frauds add foreign key(fraud_type_id) references fraud_types(id);

  create table frauds(                                                              id int unsigned auto_increment primary key,                                     incident_id int unsigned NOT NULL,                                              fraud_type_id int unsigned,                                                     other_type varchar(80),                                                         identity_used text,                                                             account_used text,                                                              amount_taken decimal(12,2),                                                     details text,                                                                  foreign key(incident_id) references incidents(id),                              foreign key(fraud_type_id) references fraud_types(id)                           )engine=InnoDB;


;;
;; addresses info are taken from City of Bloomington master addreess Database
;; address_id and subunit_id are master address ids
;;
create table addresses(                                                            id int unsigned auto_increment primary key,                                     name varchar(80) not null unique,                                                      latitude decimal(15,10),                                                        longitude decimal(15,10),                                                       city varchar(80),                                                               state varchar(2),                                                               zipcode varchar(15),                                                            jurisdiction varchar(80),                                                       address_id int unsigned,                                                        subunit_id int unsigned,                                                        invalid_address char(1)                                                         )engine=InnoDB;
			 
  alter table incidents add address_id int unsigned after date;
  alter table incidents add foreign key(address_id) references addresses(id);
;;
;; populating addresses table from incidents is done though ImportController class
;;
;; after import is done in Production the following columns should be dropped
;;
alter table incidents drop column address;
alter table incidents drop column city;
alter table incidents drop column zip;
alter table incidents drop column state;
alter table incidents drop column invalidAddress
;;


;;
;; changes on 12/15
 insert into person_types values(2,'Complainant');
;;

insert into fraud_types values
(1,'Credit Card Fraud'),
(2,'Impersonation'),
(3,'Welfare Fraud'),
(4,'Wire Fraud'),
(5,'Identity Theft'),
(6,'Hacking/Computer Invasion'),
(7,'Other Specify')
;;
;; if frauds table created before you may need to add the foreign key back
;;
 alter table frauds add foreign key(fraud_type_id) references fraud_types(id);

;;
alter table persons drop foreign key persons_ibfk_3;
;;
;; delete old ones
;;
delete from race_types;
;;
;; do insert
;;
  insert into race_types values(1,'Black Non-Hisp'),                                                           (2,'Hamalian/Oth Pacific Hispanic'),                                            (3,'Hamalian/Oth Pacific Non-Hisp'),                                            (4,'Indian/Alaskan Natv Non-Hisp'),                                             (5,'Black Hispanic'),                                                           (6,'White Hispanci'),                                                           (7,'Indian/Alaskan Natv Hispanci'),                                             (8,'Asian Non-Hisp'),                                                           (9,'Asian Hispanic'),                                                           (10,'Unknown'),                                                                 (11,'White Non-Hisp');
;;
;; add back foreign key
;;
alter table persons add foreign key(race_type_id) references race_types(id);
;;
;; person type Complainant
insert into person_types values(2,'Complainant');
;;
;; add the following to application.properties
;; for hibernate to handle table names
;;
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
;;
;;
;; Spring session tables
;;
CREATE TABLE SPRING_SESSION (
    PRIMARY_ID CHAR(36) NOT NULL,
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    PRIMARY KEY (PRIMARY_ID)
) ENGINE=InnoDB;
;;
CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_PRIMARY_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BLOB NOT NULL,
    CONSTRAINT SPRING_SESSION_ATTRIBUTES_PK PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME)
) ENGINE=InnoDB;
;;
;;
;; to upload files for tomcat9 you need to set the following
;; edit
/etc/systemd/system/multi-user.target.wants/tomcat9.service
;; 
;;  adding to the end of 'ReadWritePath' section
;; 
ReadWritePaths=/srv/data/incidents/files
;;
;;  and run
service tomcat9 stop
systemctl daemon-reload
service tomcat9 restart
;;
;; Note: we also need to copy media files from the old server to
;; /srv/data/incidents/files/
;;

;;
;; modify incidents table add category 
;; 
alter table incidents add category enum('Person','Business') after incident_type_id;
;; for the old incidents 
update incidents set category='Person' where category is null;

;;
;; database tables related to business portion of incidents
;; business local address is entered separately, so not needed here
;;
create table businesses(                                                            id int unsigned auto_increment primary key,                                     name varchar(80) not null,                                                      corporate_address varchar(160),                                                 business_number varchar(32),                                                    phone varchar(32),                                                              email varchar(160),                                                             address_id int unsigned,                                                        reporter_name varchar(160),                                                     reporter_title varchar(80),                                                     foreign key(address_id) references addresses(id)                                )engine=InnoDB;
;;
;;
;; this table will not be used right now
;; 
 create table credentials(                                                            id int unsigned auto_increment primary key,                                     business_id int unsigned not null,                                              email varchar(256) not null unique,                                             password varchar(256),                                                          last_update datetime,                                                           foreign key(business_id) references businesses(id)                              )engine=InnoDB;
;;

;;
;; we will be using sha2(string, 256) for encryption
;; something like select sha2(str, 256) = password; => 1 if match
;;
alter table incidents add business_id int unsigned after address_id;
alter table incidents add foreign key(business_id) references businesses(id);
;;
;; 6/10/2021 changes
alter table businesses add reporter_name varchar(160);
alter table businesses add reporter_title varchar(80);
;;
;; probably will not hurt keeping the foreign key for credentials table
;; since we are not going to use it right now.
alter table credentials drop constraint credentials_ibfk_1;
;;
;; modified credentials table just to keep it around
;;
create table credentials(                                                            id int unsigned auto_increment primary key,                                     email varchar(256) not null unique,                                             password varchar(256),                                                          last_update datetime                                                           )engine=InnoDB;

;;
;;
create table offenders like persons;
alter table offenders drop column person_type_id;
alter table offenders drop column reporter;
alter table offenders drop column email2;
alter table offenders drop column phone2;
alter table offenders drop column phone_type2;
alter table offenders add foreign key(incident_id) references incidents(id);
;;
;; for business incidents we have two incident types only, theft and vandal
;;
alter table incident_types add used_in_business char(1);
update incident_types set used_in_business='y' where id in (1,2);

;;
;; 8/4/2021
;; adding roll back action
;; we are adding cancelled and cancelled by rows to the action_logs
;; so the table becomes as follows
;;
  create table action_logs(                                                             id int unsigned auto_increment primary key,                                     incident_id int unsigned not null,                                              date datetime,                                                                  action_id int unsigned,                                                         user_id int unsigned,                                                           comments text,                                                                  cancelled char(1),                                                              cancelled_by int unsigned,                                                      FOREIGN KEY (incident_id) REFERENCES incidents (id),                            FOREIGN KEY (action_id) REFERENCES actions (id),                                FOREIGN KEY (user_id) REFERENCES users (id),                                    foreign key(cancelled_by) references users(id)	                               )engine=InnoDB;

alter table action_logs add cancelled char(1);
alter table action_logs add cancelled_by int unsigned;
alter table action_logs add foreign key(cancelled_by) references users(id);
;;
;; 8/5
;; update views in top received, confirmed, approved, rejected
;;
;;
alter table incidents add transient_offender char(1);
alter table offenders add transient_address char(1);
;;
;;
;; 8/24
;; add to application.properties
;; max size in MB
incident.media.max.size=5
incident.person.media.count=3
incident.business.media.count=10
;;
;; Steps for upgrade
;;
;; copy old war file
;; stop tomcat
;; dump mysql db
;; add to properties file
;; modify database
;; move new war file
;; restart
;; test
;;
;; adding discard action to the action list
;; 9/30/21
;;
insert into actions values( 7,'discarded','Discarded',1, null);
insert into role_actions values(1,7),(2,7);
insert into action_roles values(7,1),(7,2);
