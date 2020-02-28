package in.bloomington.incident.configuration;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */


import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

@Configuration
class PropertiesFileConfig{
		// dev mode
		final static String fileLocation = "c:/data/incidents/conf/application.properties";
		//
		// production mode
		// uncomment for production
		// final static string fileLocation = "/srv/data/incidents/conf/application.properties";		
		@Bean(name = "propertiesfile")		
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
				System.err.println(" **** in cofig ");
				PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
				properties.setLocation(new FileSystemResource(fileLocation));
				properties.setIgnoreResourceNotFound(false);
				return properties;
		}

}
