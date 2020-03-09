package in.bloomington.incident.utils;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class Helper
{
		/*
		public final static DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/d/yyyy");
		public final static DateTimeFormatter dft = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
		*/
		public final static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		public final static SimpleDateFormat dft = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		public final static NumberFormat curFr = NumberFormat.getCurrencyInstance();
		public final static DecimalFormat dblFr = new DecimalFormat("###.##");
    final static Map<String, String>       mimeTypes = new HashMap<>();
    static {
        mimeTypes.put("image/gif",       "gif");
        mimeTypes.put("image/jpeg",      "jpg");
        mimeTypes.put("image/png",       "png");
        mimeTypes.put("image/tiff",      "tiff");
        mimeTypes.put("image/bmp",       "bmp");
        mimeTypes.put("text/plain",      "txt");
        mimeTypes.put("audio/x-wav",     "wav");
        mimeTypes.put("application/pdf", "pdf");
        mimeTypes.put("audio/midi",      "mid");
        mimeTypes.put("video/mpeg",      "mpeg");
        mimeTypes.put("video/mp4",       "mp4");
        mimeTypes.put("video/x-ms-asf",  "asf");
        mimeTypes.put("video/x-ms-wmv",  "wmv");
        mimeTypes.put("video/x-msvideo", "avi");
        mimeTypes.put("text/html",       "html");

        mimeTypes.put("application/mp4",               "mp4");
        mimeTypes.put("application/x-shockwave-flash", "swf");
        mimeTypes.put("application/msword",            "doc");
        mimeTypes.put("application/xml",               "xml");
        mimeTypes.put("application/vnd.ms-excel",      "xls");
        mimeTypes.put("application/vnd.ms-powerpoint", "ppt");
        mimeTypes.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
    }

    public Helper()
    {

    }

    public final static int getCurrentYear()
    {
        Calendar cal  = Calendar.getInstance();
        int      year = cal.get(Calendar.YEAR);
        return year;
    }

    public final static int getCurrentMonth()
    {
        Calendar cal   = Calendar.getInstance();
        int      month = cal.get(Calendar.MONTH) + 1;
        return month;
    }

    public final static String findFileType(File file)
    {
        String fileType = "";
        try {
            String pp   = file.getAbsolutePath();
            Path   path = Paths.get(pp);
            fileType = Files.probeContentType(path);
            System.err.println(fileType);
        }
        catch (Exception ex) {
            System.err.println(" fle type excep " + ex);
        }
        return fileType;
    }

//
    // check multiple emails separated by comma
    //
    public final static boolean isValidEmails(final String email)
    {
        //
        if (email == null) return false;
        if (email.indexOf("@") == -1) {
            return false;
        }
        if (email.indexOf(",") > 0) {
            //
            // multiple emails
            //
            String strs[] = email.split(",");
            for (String str : strs) {
                if (!isValidEmail(str.trim())) return false;
            }
        }
        else {
            if (!isValidEmail(email)) return false;
        }
        return true;
    }

    public final static boolean isValidEmail(final String email)
    {
        String       expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr   = email;
        // Make the comparison case-insensitive.
        Pattern      pattern    = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher      matcher    = pattern.matcher(inputStr);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * replace '<' and '>' to ' (' and ')' needed to show owners email logs
     */
    public final static String doCleanTag(String str)
    {
        String ret = "";
        if (str != null && str.indexOf("<") > -1 || str.indexOf(">") > -1) {
            ret = str.replace("<", " (").replace(">", ")");
        }
        return ret;
    }

    public final static String getFileExtension(File file)
    {
        String ext = "";
        try {
            // name does not include path
            String name     = file.getName();
            String pp       = file.getAbsolutePath();
            Path   path     = Paths.get(pp);
            String fileType = Files.probeContentType(path);
            if (fileType != null) {
                // application/pdf
                if (fileType.endsWith("pdf")) {
                    ext = "pdf";
                }
                // image/jpeg
                else if (fileType.endsWith("jpeg")) {
                    ext = "jpg";
                }
                // image/gif
                else if (fileType.endsWith("gif")) {
                    ext = "gif";
                }
                // image/bmp
                else if (fileType.endsWith("bmp")) {
                    ext = "bmp";
                }
                // application/msword
                else if (fileType.endsWith("msword")) {
                    ext = "doc";
                }
                // application/vnd.ms-excel
                else if (fileType.endsWith("excel")) {
                    ext = "csv";
                }
                // application/vnd.openxmlformats-officedocument.wordprocessingml.document
                else if (fileType.endsWith(".document")) {
                    ext = "docx";
                }
                // text/plain
                else if (fileType.endsWith("plain")) {
                    ext = "txt";
                }
                // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
                else if (fileType.endsWith(".sheet")) {
                    ext = "xlsx";
                }
                // audio/wav
                else if (fileType.endsWith("wav")) {
                    ext = "wav";
                }
                // text/xml
                else if (fileType.endsWith("xml")) {
                    ext = "xml";
                }
                else if (fileType.endsWith("html")) {
                    ext = "html";
                }
                // video/mng
                else if (fileType.endsWith("mng")) {
                    ext = "mng";
                }
                else if (fileType.endsWith("mpeg")) {
                    ext = "mpg";
                }
                // video/mp4
                else if (fileType.endsWith("mp4")) {
                    ext = "mp4";
                }
                else if (fileType.endsWith("avi")) {
                    ext = "avi";
                }
                else if (fileType.endsWith("mov")) {
                    ext = "mov";
                }
                // quick time video
                else if (fileType.endsWith("quicktime")) {
                    ext = "qt";
                }
                else if (fileType.endsWith("wmv")) {
                    ext = "wmv";
                }
                else if (fileType.endsWith("asf")) {
                    ext = "asf";
                }
                // flash video
                else if (fileType.endsWith("flash")) {
                    ext = "swf";
                }
                else if (fileType.startsWith("image")) {
                    ext = "jpg";
                } // if non of the above we check the file name
                else if (name.indexOf(".") > -1) {
                    ext = name.substring(name.lastIndexOf(".") + 1);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println(" fle ext excep " + e);
        }
        return ext;
    }

    public final static String getFileExtensionFromName(String name)
    {
        String ext = "";
        try {
            if (name.indexOf(".") > -1) {
                ext = name.substring(name.lastIndexOf(".") + 1);
            }
        }
        catch (Exception ex) {

        }
        return ext;
    }

    public final static String getToday()
    {
        // LocalDate date  = LocalDate.now();
				Date date = new Date();
        String today = df.format(date);
        return today;
    }

    public final static String checkFilePath(final String filePath, final String groupName)
    {
        String back = "";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                String os = System.getProperty("os.name").toLowerCase();
                System.err.println(" OS " + os);
                if (os.indexOf("win") == -1) { // linux
                    String cmd  = "/bin/chgrp " + groupName + " " + filePath;
                    String cmd2 = "/bin/chmod 777 " + filePath;
                    Runtime.getRuntime().exec(cmd);
                    Runtime.getRuntime().exec(cmd2);
                }
            }
        }
        catch (Exception ex) {
            back += ex;
            System.err.println(ex);
        }
        return back;
    }

		public final static String checkFilePath(final String filePath){
        String back = "";
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                String os = System.getProperty("os.name").toLowerCase();
                System.err.println(" OS " + os);
                if (os.indexOf("win") == -1) { // linux
                    String cmd  = "/bin/chgrp " + filePath;
                    String cmd2 = "/bin/chmod 777 " + filePath;
                    Runtime.getRuntime().exec(cmd);
                    Runtime.getRuntime().exec(cmd2);
                }
            }
        }
        catch (Exception ex) {
            back += ex;
            System.err.println(ex);
        }
        return back;
    }		
    public final static String extractErrors(final BindingResult result)
    {
				String errors = "";
				if(result != null){
						for (ObjectError error : result.getAllErrors()) {
								if(!errors.equals("")) errors += " ";
								errors += error.getObjectName() + " - " + error.getDefaultMessage();
						}
				}
        return errors;
    }
		/**
		 * we are assuming that the incident Id is already in the session
		 * after incident is saved
		 */
		@SuppressWarnings("unchecked")
		public final static boolean verifySession(final HttpServletRequest req, final String id){
				// no new session
				HttpSession session = req.getSession();
				if(session != null){
						List<String> ids = (List<String>) session.getAttribute("incident_ids");
						if(ids != null){
								System.err.println(" ** ids ** "+ids);
								if(ids.contains(id)){
										return true;
								}
						}
				}
				return false;
		}
		@SuppressWarnings("unchecked")
		public final static boolean verifySession(final HttpSession session, final String id){
				// no new session
				if(session != null){
						List<String> ids = (List<String>) session.getAttribute("incident_ids");
						if(ids != null){
								System.err.println(" ** ids ** "+ids);
								if(ids.contains(id)){
										return true;
								}
						}
				}
				return false;
		}		
		
}
