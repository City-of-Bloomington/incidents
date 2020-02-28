package in.bloomington.incident.utils;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentHelper
{
    @Autowired
    private Environment         env;
		
    static String url = "",
				image_url = "";

    String
				file_path            = "",
				address_check_url    = "",
				ldap_host            = "";
    boolean isSet                = false;

    public EnvironmentHelper()
    {
        populatePaths();
    }
		/**
			 // we can get env info from properites file as well
			 // or inject directly
			 String dbUrl = env.getProperty("jdbc.url"))


		 */

    public void populatePaths()
    {
        if (env != null) {
            String str = env.getProperty("file_path");
            if (str != null) {
                file_path = str;
            }
            str = env.getProperty("image_url");
            if (str != null) {
                image_url = str;
            }
            str = env.getProperty("address_check_url");
            if (str != null) {
                address_check_url = str;
            }						
        }
    }

    /*
		 * getters
     */
    public String getFilePath()
    {
        return file_path;
    }


    public String getImageUrl()
    {
        return image_url;
    }

    public String getLdapHost()
    {
        return ldap_host;
    }
    public String getAddressCheckUrl()
    {
        return address_check_url;
    }
		
    public boolean isSet()
    {
        return isSet();
    }

    public void doReset()
    {
        populatePaths();
    }

}
