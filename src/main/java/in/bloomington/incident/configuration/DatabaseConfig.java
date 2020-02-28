package in.bloomington.incident.configuration;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class DatabaseConfig{

		// prefix set in application.properties file
		@Bean(name = "dbdatasource")		
		@ConfigurationProperties(prefix = "incident.dbdatasource") 
		@Primary
    public DataSource createDataSource() {
				return DataSourceBuilder.create().build();
    }

		@Bean(name = "jdbcService")
		@Autowired
		public JdbcTemplate createJdbcTemplate_DbDataService(@Qualifier("dbdatasource") DataSource jdbcServiceDS) {
      return new JdbcTemplate(jdbcServiceDS);
   }		
		/**
			 //
			 // copy from example
			 //
			@Bean(name = "dbProductService")
   @ConfigurationProperties(prefix = "spring.dbProductService")
   @Primary
   public DataSource createProductServiceDataSource() {
      return DataSourceBuilder.create().build();
   }
   @Bean(name = "jdbcProductService")
   @Autowired
   public JdbcTemplate createJdbcTemplate_ProductService(@Qualifier("dbProductService") DataSource productServiceDS) {
      return new JdbcTemplate(productServiceDS);
   }	 

		 */

}
