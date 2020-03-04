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

  create table action_roles(                                                           action_id int unsigned not null,                                                role_id int unsigned not null,                                                  FOREIGN KEY (action_id) REFERENCES actions (id),                                FOREIGN KEY (role_id) REFERENCES roles (id),                                    unique(action_id, role_id)                                                      )engine=InnoDB;

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
	create table incident_initials(                                                     id int unsigned auto_increment primary key,                                     email varchar(70) not null,                                                     received datetime not null,			                                                incident_type_id int(10) unsigned,                                               foreign key(incident_type_id) references incident_types(id)                     )engine=InnoDB;

			 
	create table incidents(
			 id int unsigned auto_increment primary key,
  		 cfs_number varchar(20) DEFAULT NULL,
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
