package in.bloomington.incident;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;



 // uncomment to run from jar file as stand alone
@SpringBootApplication
 public class Application {
 
    // this value comes from application.properties file
    @Value("${spring.application.name}")
    static String name;
    public static void main(String[] args) {
       System.err.println(" running "+name);
       SpringApplication.run(Application.class, args);
    }


}

// comment out this when running from jar
/**
@SpringBootApplication
public class Application extends SpringBootServletInitializer{
    @Value("${spring.application.name}")
    static String name;
    public static void main(String[] args) {
       System.err.println(" running "+name);
       SpringApplication.run(Application.class, args);
    }

}
*/
