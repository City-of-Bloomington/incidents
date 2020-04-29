package in.bloomington.incident.configuration;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.ArrayList;
import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
class PropertiesFileConfig{
    // dev mode

    // final static String fileLocation = "c:/data/incidents/conf/application.properties";

    //
    // production mode
    // uncomment for production
    final static String fileLocation = "/srv/data/incidents/conf/application.properties";
    @Bean(name = "propertiesfile")		
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
	properties.setLocation(new FileSystemResource(fileLocation));
	properties.setIgnoreResourceNotFound(false);
	return properties;
    }
    /*
    @Bean(name = "propertiesfile")		
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
	properties.setLocation(new ClassPathResource("application.properties"));
	properties.setIgnoreResourceNotFound(false);
	return properties;
    }
    */    
    /*
    @Bean(name = "propertiesfile")
    public PropertyPlaceholderConfigurer properties() {
        final PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreResourceNotFound(true);

        final List<Resource> resourceLst = new ArrayList<Resource>();

        //resourceLst.add(new ClassPathResource("application.properties"));
        resourceLst.add(new FileSystemResource("c:/data/incidents/conf/application.propertie"));
        resourceLst.add(new FileSystemResource("/srv/data/incidents/conf/application.propertie"));

        ppc.setLocations(resourceLst.toArray(new Resource[]{}));

        return ppc;
    }    
    */
}
