# Incidents

## Introduction
Incidents is a reporting program to help residance of certain city or town to report minor theft, lost and/or vandlized car within certian dollar value set by the local authority.
Residance will be able to report the incident online so no need to for police authority to come to record the incident. Then the local police authority will process each incident either as approved or denied. A letter or email of the descision is provided for resident about the incident.

There are a number of conditions are set for the type of incidents that are accepted to go through this system. If the conditions are not met. The person reporting the incident must contact the authority directlry by phone or by person.



## Requirements

Incidents is built using java spring boot framework and requires Apache Tomcat version 8.* or above.

## Development

incidents uses Maven to build a WAR file.

```bash
mvn clean package
```

## Deployment

We use Ansible to deploy a previously built WAR file.  Ansible looks for the WAR file: `/target/incidents.war`.  If you are using Ansible to deploy, your inventory will need to set all the variables in `/ansible/group_vars/all.yml`.


```bash
mvn clean package
cd ansible
ansible-playbook deploy.yml -i /path/to/inventory
```
