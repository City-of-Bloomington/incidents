package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.sql.*;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
//
// implementation of logging is logback
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//
import in.bloomington.incident.service.ActionService;
import in.bloomington.incident.service.UserService;
import in.bloomington.incident.model.Action;
import in.bloomington.incident.model.User;
// this class is needed only once to import data from old system
// to the new one.
// for new systems that start from scratch this is not needed
@Controller
public class ImportController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(ImportController.class);
    @Value("${incident.dbdatasource.jdbc-url}")
    private String sql_url;
		@Value("${incident.dbdatasource.username}")
    private String sql_username;		
		@Value("${incident.dbdatasource.password}")
    private String sql_password;
		String sqlUrlStr = "";
		Connection con = null;
		//
		// this operation is needed one time only to import all old addresses
		// from the incidents table into addresses table and update the incidents
		// address_id to related address Id from addresses table
		// 
    @GetMapping("/importAddresses")
    public String showImport(Model model) {
        return "staff/import_addresses";
    }
    @GetMapping("/performImport")
    public String doImport(Model model) {
				String back = doImport();
				if(back.isEmpty()){
						addMessage("Imported successfully");
						model.addAttribute("messages", messages);
				}
				else{
						addError(back);
						model.addAttribute("errors", errors);
				}
        return "staff/import_addresses";
    }
		private String doImport(){
				String back = "";
				String qq = " select id,address,zip,invalid_address from incidents";
				String qq2 = " select id from addresses where upper(name) like ?";
				String qq3 = " insert into addresses (id,name,city,state,zipcode,invalid_address) values(?,?,'Bloomington','IN',?,?)";
				String qq4 = " update incidents set address_id=? where id=? ";
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null, pstmt4=null;
				ResultSet rs, rs2;
				databaseConnect();
				try{
						System.err.println(" url "+sqlUrlStr);
						System.err.println(" qq "+qq);						
						pstmt = con.prepareStatement(qq);
						System.err.println(" qq2 "+qq2);
						pstmt2 = con.prepareStatement(qq2);
						System.err.println(" qq3 "+qq3);
						pstmt3 = con.prepareStatement(qq3);
						System.err.println(" qq4 "+qq4);
						pstmt4 = con.prepareStatement(qq4);						
						int jj=1;

						rs = pstmt.executeQuery();
						while(rs.next()){
								String id = rs.getString(1);
								String name = rs.getString(2);
								String zip = rs.getString(3);
								String invalid = rs.getString(4);
								String address_id = "";
								System.err.println(" addr: "+name);
								if(name != null
									 && !name.isEmpty()){
										// let us check if we already have this address in addresses table
										String addrCap = name.toUpperCase();
										pstmt2.setString (1, addrCap);
										rs2 = pstmt2.executeQuery();
										qq = qq2;
										if(rs2.next()){ 
												address_id = rs2.getString(1);
												pstmt4.setString(1, address_id);
												pstmt4.setString(2, id);
												pstmt4.executeUpdate();
										}
										else{
												pstmt3.setInt(1, jj);
												pstmt3.setString(2, name);
												pstmt3.setString(3, zip);
												if(invalid == null){
														pstmt3.setNull(4,Types.CHAR);
												}
												else{
														pstmt3.setString(4,"y");
												}
												pstmt3.executeUpdate();
												// 
												pstmt4.setInt(1, jj);
												pstmt4.setString(2, id);
												pstmt4.executeUpdate();
												jj++;
										}
								}
						}
				}
				catch(Exception ex){
						System.err.println(ex);
						back += ex;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4);
				}
				return back;
		}


    public void databaseConnect(){
				//
				try {
						Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
						sqlUrlStr = sql_url+"&user="+sql_username+"&password="+java.net.URLEncoder.encode(sql_password, "UTF-8");
						System.err.println(" sql url "+sqlUrlStr);
						//
						// mysql
						con = DriverManager.getConnection(sqlUrlStr);
						if(con == null){
								System.err.println("Could not connect");
						}
				}
				catch (Exception ex){
						System.err.println(ex);

				}
    }

    public void databaseDisconnect(Statement... stmts){
				try {
						for(Statement st:stmts){
								if(st != null) st.close();
						}
						if(con != null)
								con.close();
				}
				catch (Exception e) {
						System.err.println(e);
				}
    }

		
		
}
