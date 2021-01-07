package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
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

		// this is needed for data import from the old app
		@Value("${incident.oldDatabase.dbUrl}")
    private String sql_url2;
		@Value("${incident.oldDatabase.username}")
    private String sql_username2;		
		// @Value("${incident.dbdatasource.password2}")
    private String sql_password2;
		
		String sqlUrlStr = "", sqlUrlStr2 = "";
		Connection con = null, con2 = null;
		//
		// this operation is needed one time only to import all old addresses
		// from the incidents table into addresses table and update the incidents
		// address_id to related address Id from addresses table
		// 
    @GetMapping("/importData")
    public String showImport(Model model) {
        return "staff/import_data";
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
        return "staff/import_result";
    }
		private String cleanup(){
				/**
					 delete from fruads;
					 delete from action_logs;
					 delete from persons;
					 delete from media;
					 delete from properties;
					 
					 delete from requests;
					 delete from vehicles;
					 delete from incidents;
					 delete from addresses;

				 */
				String back = "";
				String qq = "delete from frauds";
				String qq2 = "delete from action_logs";
				String qq3 = "delete from persons";
				String qq4 = "delete from media";
				String qq5 = "delete from properties";
				String qq6 = "delete from requests";
				String qq7 = "delete from vehicles";
				String qq8 = "delete from incidents";
				String qq9 = "delete from addresses";
				
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null,pstmt7=null,
						pstmt8=null,pstmt9=null;
				
				ResultSet rs=null;
				databaseConnect();
				try{
						// addresses
						System.err.println(" qq "+qq);
						pstmt = con.prepareStatement(qq);
						pstmt.executeUpdate();
						qq = qq2;
						pstmt2 = con.prepareStatement(qq);
						pstmt2.executeUpdate();
						qq = qq3;
						pstmt3 = con.prepareStatement(qq);
						pstmt3.executeUpdate();
						qq = qq4;
						pstmt4 = con.prepareStatement(qq);
						pstmt4.executeUpdate();
						qq = qq5;
						pstmt5 = con.prepareStatement(qq);
						pstmt5.executeUpdate();
						qq = qq6;
						pstmt6 = con.prepareStatement(qq);
						pstmt6.executeUpdate();
						qq = qq7;
						pstmt7 = con.prepareStatement(qq);
						pstmt7.executeUpdate();
						qq = qq8;
						pstmt8 = con.prepareStatement(qq);
						pstmt8.executeUpdate();
						qq = qq9;
						pstmt9 = con.prepareStatement(qq);
						pstmt9.executeUpdate();
				}catch(Exception ex){
						back = ex+":"+qq;
						System.err.println(back);
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5,
															 pstmt6, pstmt7, pstmt8, pstmt9);
				}
				return back;

		}
		//
		// imports addresses, incidents, action_logs
		//
		private String importOne(){
				String back = "";
				// we delete the test data
				//
				/**
					 delete from frauds;
					 delete from action_logs;
					 delete from persons;
					 delete from media;
					 delete from properties;
					 delete from requests;
					 delete from vehicles;
					 delete from incidents;
					 delete from addresses;

				 */
				// addresses
				String qs = " select id,address,zip,invalidAddress from incidents";
				// rmeove unique key
				String qqu = "alter table addresses drop index name ";
				String qq = " insert into addresses (id,name,city,state,zipcode,invalid_address) values(?,?,'Bloomington','IN',?,?)";
				//
				//
				// action_id=status_id+1
				String qs2 = " select id,cfsNumber,incidentType_id,status_id+1,received,date,id,details,dateDescription,end_date,entry_type,other_entry,haveMedia,email from incidents";
				String qq2 = "insert into incidents (id,case_number,incident_type_id,action_id, received,date,address_id,details,date_description,end_date, entry_type,other_entry,have_media,email) values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?)";
				//
				// action_id=status_id+1
				String qs3 = "select id,incident_id,date,status_id+1,user_id,comments from status_history";				
				String qq3 = "insert into action_logs (id,incident_id,date,action_id,user_id,comments) values(?,?,?,?,?, ?)";
				//
				
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs=null;
				String qc =qs;
				databaseConnect();
				try{
						pstmt3 = con.prepareStatement(qqu);
						pstmt3.executeUpdate();
						// addresses
						System.err.println(" qs "+qs);
						pstmt = con2.prepareStatement(qs);
						qc = qq;
						System.err.println(" qq "+qq);
						pstmt2 = con.prepareStatement(qq);
						rs = pstmt.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<5;jj++){
										pstmt2.setString(jj, rs.getString(jj));
								}
								pstmt2.executeUpdate();
						}
						qc = qs2;
						// incidents;
						System.err.println(" qs "+qs2);
						pstmt3 = con2.prepareStatement(qs2);
						qc = qq2;
						System.err.println(" qq "+qq2);
						pstmt4 = con.prepareStatement(qq2);
						rs = pstmt3.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<15;jj++){
										pstmt4.setString(jj, rs.getString(jj));
								}
								pstmt4.executeUpdate();
						}
						//
						// actions
						// some incidents are not there so we need to skip these
						qc = qs3;
						System.err.println(" qs "+qs3);
						pstmt5 = con2.prepareStatement(qs3);
						qc = qq3;
						System.err.println(" qq "+qq3);
						pstmt6 = con.prepareStatement(qq3);
						rs = pstmt5.executeQuery();
						while(rs.next()){
								try{
										for(int jj=1;jj<7;jj++){
												pstmt6.setString(jj, rs.getString(jj));
										}
										pstmt6.executeUpdate();
								}catch(Exception skip){
										// we just ignore this
								}
						}
				}catch(Exception ex){
						back = ex+":"+qc;
						System.err.println(back);
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}
				return back;
		}
		//
		// import media, requests
		//
		private String importTwo(){
				String back = "";
				// media
				String qs = "select id,incident_id,name,old_file_name,year,mime_type,notes from media";
				String qq = "insert into media (id,incident_id,file_name,old_file_name,year,mime_type,notes) values(?,?,?,?,?, ?,?)";
				//
				// requests
				String qs2 ="select id,hash,confirmed,expires from requests";
				String qq2 ="insert into requests (id,hash,confirmed,expires) values(?,?,?,?)";
				String qc = qs;
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs=null;
				databaseConnect();
				try{
						// media
						System.err.println(" qs "+qs);
						pstmt = con2.prepareStatement(qs);
						qc = qq;
						System.err.println(" qq "+qq);
						pstmt2 = con.prepareStatement(qq);
						rs = pstmt.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<8;jj++){
										pstmt2.setString(jj, rs.getString(jj));
								}
								pstmt2.executeUpdate();
						}
						qc = qs2;
						// requests
						System.err.println(" qs "+qs2);
						pstmt3 = con2.prepareStatement(qs2);
						System.err.println(" qq "+qq2);
						qc = qq2;
						pstmt4 = con.prepareStatement(qq2);
						rs = pstmt3.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<5;jj++){
										pstmt4.setString(jj, rs.getString(jj));
								}
								pstmt4.executeUpdate();
						}
						
				}catch(Exception ex){
						System.err.println(ex+": "+qc);
						back = ex+": "+qc;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}
				return back;
		}
		private String importThree(){
				String back = "";
				//
				// update persons
				String qu = "update persons set title=null where title=''";
				String qu2 = "update persons set phoneType=null where phoneType=''";
				String qu3 =  "update persons set race=null where race=''";
				// persons
				String qs = "select id,incident_id,personType_id,title,firstname,lastname,midname,suffix,address,city,state,zip,phone,phone2,phoneType,email,email2,driverLicenseNumber,dateOfBirth,socialSecurityNumber,heightFeet,heightInch,weight,sex,occupation,reporter,race from persons";
				// make sure to translate race to related id
				String qq = "insert into persons (id,incident_id,person_type_id,title,firstname,lastname,midname,suffix,address,city,state,zip,phone,phone2,phone_type,email,email2,dln,dob,ssn,height_feet,height_inch,weight,sex,occupation,reporter,race_type_id) values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?)";
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs=null;
				String qc = qs;
				databaseConnect();
				try{
						System.err.println(" qu "+qu);
						pstmt3 = con2.prepareStatement(qu);
						pstmt3.executeUpdate();
						System.err.println(" qu "+qu2);
						pstmt4 = con2.prepareStatement(qu2);
						pstmt4.executeUpdate();
						System.err.println(" qu "+qu3);
						pstmt5 = con2.prepareStatement(qu3);
						pstmt5.executeUpdate();						
						System.err.println(" qs "+qs);
						pstmt = con2.prepareStatement(qs);
						qc = qq;
						System.err.println(" qq "+qq);
						pstmt2 = con.prepareStatement(qq);
						rs = pstmt.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<27;jj++){
										pstmt2.setString(jj, rs.getString(jj));
								}
								String race_id=null;
								String race = rs.getString(27); // find the related id
								if(race != null){
										// old types
										// 'Caucasion','Hispanic','African American','Native American','Asian','Other'
										if(race.startsWith("Cauc"))
												race_id="11";
										else if(race.startsWith("Hisp"))
												race_id="6";
										else if(race.startsWith("Afric"))
												race_id="1";
										else if(race.startsWith("Native"))
												race_id="4";
										else if(race.startsWith("Asian"))
												race_id="8";
										else if(race.startsWith("Other"))
												race_id="10";
								}
								pstmt2.setString(27,race_id);
								pstmt2.executeUpdate();
						}
				}catch(Exception ex){
						System.err.println(ex+": "+qc);
						back = ex+": "+qc;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}
				return back;
		}
		//
		// properties and vehicles
		//
		private String importFour(){
				// properties
				//
				String back = "";
				String qs = "select id,incident_id,damageType_id,brand,model,value,serialNum,owner,description from properties";
				String qq = "insert into properties (id,incident_id,damage_type_id,brand,model,value,serial_num,owner,description) values(?,?,?,?,?, ?,?,?,?)";
				// vehicles
				String qs2 = "select id,incident_id,carDamageType_id,year,make,model,color,licensePlateNumber,state,licensePlateYear,owner,description from vehicles ";
				String qq2 = "insert into vehicles (id,incident_id,car_damage_type_id,year,make,model,color,plate_number,state,plate_year,owner,description) values(?,?,?,?,?, ?,?,?,?,?, ?,?)";
				String qc = qs;
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs=null;
				databaseConnect();
				try{				
						System.err.println(" qs "+qs);
						pstmt = con2.prepareStatement(qs);
						qc = qq;
						System.err.println(" qq "+qq);
						pstmt2 = con.prepareStatement(qq);
						rs = pstmt.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<10;jj++){
										pstmt2.setString(jj, rs.getString(jj));
								}
								pstmt2.executeUpdate();
						}
						qc = qs2;
						// vehicles
						System.err.println(" qs "+qs2);
						pstmt3 = con2.prepareStatement(qs2);
						qc = qq2;
						System.err.println(" qq "+qq2);
						pstmt4 = con.prepareStatement(qq2);
						rs = pstmt3.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<13;jj++){
										pstmt4.setString(jj, rs.getString(jj));
								}
								pstmt4.executeUpdate();
						}						
				}catch(Exception ex){
						System.err.println(ex+": "+qc);
						back = ex+": "+qc;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}
				return back;
		}
		private String doImport(){
				String back = "";
				back = cleanup();
				if(back.isEmpty()){
						back = importOne();
				}
				if(back.isEmpty()){
						back = importTwo();
				}
				if(back.isEmpty()){
						back = importThree();
				}
				if(back.isEmpty()){
						back = importFour();
				}
				back = findAddressDups();
				//
				back = addActionToProcessedIncidents();
				return back;
		}
		/**
		 * the output of the qq script will be something like
		 *
| id   | upper(s.name)   
+------+----------------------
| 5158 | 100 E KIRKWOOD AVE   
| 5747 | 100 E KIRKWOOD AVE   
| 3420 | 100 E MILLER DR      
| 4239 | 100 E MILLER DR      
| 4630 | 100 E MILLER DR      
| 5937 | 100 E MILLER DR      
| 6423 | 100 E MILLER DR      
|  214 | 100 E MILLER DR.     
| 4222 | 100 E MILLER DR.     
| 4492 | 100 E MILLER DR.     
| 5758 | 100 E MILLER DR.     
| 5936 | 100 E MILLER DR.                          

		 */
		String findAddressDups(){
				String back = "";
				String qq =  "select s.id,upper(s.name) from addresses s where upper(s.name) in (select a.name from (select count(*),upper(name) name from addresses group by upper(name) having count(*) > 1) a ) order by 2,1";
				String qu = "update incidents set address_id = ? where address_id=? ";
				String qd = "delete from addresses where id = ?";
				String qu2 = "alter table addresses add unique(name)";
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs=null;
				databaseConnect();
				HashMap<Integer, List<Integer>> hash = new HashMap<>();
				try{				
						System.err.println(" qq "+qq);
						pstmt = con.prepareStatement(qq);
						pstmt2 = con.prepareStatement(qu);
						pstmt3 = con.prepareStatement(qd);
						rs = pstmt.executeQuery();
						int old_id = 0;
						String old_str = "";
						List<Integer> list = null;
						while(rs.next()){
								int id = rs.getInt(1);
								String str = rs.getString(2);
								if(str.equals(old_str)){
										if(list == null) list = new ArrayList<>();
										list.add(id);
								}
								else{
										if(old_id == 0){ // when we start
												old_id = id;
												old_str = str;
										}
										else {
												hash.put(old_id, list);
												old_id = id;
												old_str = str;
												list = new ArrayList<>();
										}
								}
						}
						hash.put(old_id, list); // the last record
						int ii=1;
						
						for(int jj:hash.keySet()){
								List<Integer> lst = hash.get(jj);
								System.err.println(ii+" "+jj+" "+lst);
								for (int id:lst){
										pstmt2.setInt(1, jj);
										pstmt2.setInt(2, id);
										pstmt2.executeUpdate();
										pstmt3.setInt(1, id);
										pstmt3.executeUpdate();
								}
								ii++;
						}
						pstmt4 = con.prepareStatement(qu2); // add unique
						pstmt4.executeUpdate();
				}catch(Exception ex){
						System.err.println(ex+": "+qq);
						back = ex+": "+qq;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}
				return back;
		}
		String addActionToProcessedIncidents(){
				String back = "";
				String qs = "select s.id,a.date,a.user_id from incidents s,action_logs a where s.case_number is not null and s.id=a.incident_id and a.action_id=4 and s.id not in (select b.incident_id from action_logs b where b.action_id=6)";
				String qq = "insert into action_logs values(0,?,?,6,?,null)";
				PreparedStatement pstmt=null,pstmt2=null,pstmt3=null,
						pstmt4=null, pstmt5=null,pstmt6=null;
				ResultSet rs = null;
				databaseConnect();
				String qc=qs;
				try{				
						System.err.println(" qs "+qs);
						pstmt = con .prepareStatement(qs);
						qc = qq;
						System.err.println(" qq "+qq);
						pstmt2 = con.prepareStatement(qq);
						rs = pstmt.executeQuery();
						while(rs.next()){
								for(int jj=1;jj<4 ;jj++){
										System.err.println(jj+" "+rs.getString(jj));
										pstmt2.setString(jj, rs.getString(jj));
								}
								pstmt2.executeUpdate();
						}
				}catch(Exception ex){
						System.err.println(ex+": "+qc);
						back = ex+": "+qc;
				}
				finally{
						databaseDisconnect(pstmt, pstmt2, pstmt3, pstmt4, pstmt5, pstmt6);
				}				
				return back;
		}

		/**
		 // after import complete do the following
		 //
		 // now do cleanup of addresses and add unique key
		 //
		 // find the dups and related id
		 //
		 select count(*),upper(name) name from addresses group by upper(name) having count(*) > 1)
		 select s.id,upper(s.name) from addresses s where upper(s.name) in (select a.name from (select count(*),upper(name) name from addresses group by upper(name) having count(*) > 1) a ) order by 2,1;
		 // now select one from each list, update the rest with this one,
		 // update incidents set address_id = ? where address_id in ( );
		 //
		 // 5158
		 update incidents set address_id = 5158 where address_id in (5747);
		 delete from addresses where id in (5747);
		 //
		 // 214
		 update incidents set address_id = 214 where address_id in (3420,4239,4630,5937,6423,4222,4492,5758,5936,4354,5318);
		 delete from addresses where id in 3420,4239,4630,5937,6423,4222,4492,5758,5936,4354,5318);
		 
		 //
		 // alter table addresses add unique(name);
		 //
				*/						


    public void databaseConnect(){
				//
				try {
						Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
						sqlUrlStr = sql_url+"&user="+sql_username+"&password="+java.net.URLEncoder.encode(sql_password, "UTF-8");
						//
						// new database
						con = DriverManager.getConnection(sqlUrlStr);
						if(con == null){
								System.err.println("Could not connect");
						}
						Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
						sqlUrlStr2 = sql_url2+"&user="+sql_username2+"&password="+java.net.URLEncoder.encode(sql_password, "UTF-8");
						//
						//old database
						con2 = DriverManager.getConnection(sqlUrlStr2);
						if(con2 == null){
								System.err.println("Could not connect db 2");
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
						if(con2 != null)
								con2.close();						
				}
				catch (Exception e) {
						System.err.println(e);
				}
    }

		
		
}
