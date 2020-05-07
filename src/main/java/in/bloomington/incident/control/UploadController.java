package in.bloomington.incident.control;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import in.bloomington.incident.service.MediaService;
import in.bloomington.incident.service.IncidentService;
import in.bloomington.incident.model.Incident;
import in.bloomington.incident.model.Media;
import in.bloomington.incident.utils.Helper;



@Controller
public class UploadController extends TopController{

    final static Logger logger = LoggerFactory.getLogger(UploadController.class);		
    @Autowired
    MediaService mediaService;		
    @Autowired
    IncidentService incidentService;
    private Environment env;
    @Value( "${incident.upload.storage}" )
    private String storagePath;		


    /*
    // get by id
    @GetMapping("/media/view/{id}")
    public String getAttachment(@PathVariable("id") int id, Model model)
    {
    try{
    Media media = mediaService.findById(id);
    model.addAttribute("media", media);
    return "mediaView";
    }catch(Exception ex){
    errors += ex;
    }
    return "redirect:/incident/"+id;
    }
    */
    @GetMapping("/media/add/{id}")
    public String mediaForm(@PathVariable("id") int id, Model model)
    {
	try{
	    Incident incident = incidentService.findById(id);
	    model.addAttribute("incident_id", id);
	    return "mediaAdd";
	}catch(Exception ex){
	    addError("invalid incident "+id);
	    System.err.println(""+ ex);
	}
	return "redirect:/incident/"+id;
    }		
    // delete by id
    @GetMapping("/media/delete/{id}")
    public String deleteAttachment(@PathVariable("id") int id)
    {
        Media media  = mediaService.findById(id);
        Incident     incident       = media.getIncident();
        mediaService.delete(id);
	addMessage("Attachment deleted successfully");
        return 
            "redirect:/incident/" + incident.getId();

    }


    @PostMapping("/media/save")
    public String doUploadAndSave(@RequestParam("file" ) MultipartFile file,
				  @RequestParam("incident_id"   ) int  incident_id,
				  @RequestParam("notes") String  notes
				  ){
        String fileName = null;
        if (file == null || file.isEmpty()) {
	    addMessage("Please select a file to upload");
            return "redirect:media/add" + incident_id;
        }
        String oldFileName  = file.getOriginalFilename();
	if(oldFileName.contains("..")){
	    addError("file name should not have relative directory");
	    return "redirect:media/add/" + incident_id;
	}
	String mimeType = file.getContentType();
        String ret_str   = "";
        int    year      = Helper.getCurrentYear();
        String file_ext  = Helper.getFileExtensionFromName(oldFileName);
        String newName   = genNewFileName(file_ext);
        
        try {
            byte[] bytes   = file.getBytes();
            String dirPath = storagePath+ "/" + year + "/";
            //

            String back    = Helper.checkFilePath(dirPath);
            if (!back.equals("")) {
                addError(back);
                logger.error(back);
            }
	    else{
		Path path = Paths.get(dirPath + newName);
		Files.write(path, bytes);
		Media one = new Media();
		one.setFileName   (newName   );
		one.setOldFileName(oldFileName  );
		one.setNotes      (notes     );
		one.setYear(year);
		one.setMimeType(mimeType);
		Incident incident = incidentService.findById(incident_id);
		one.setIncident(incident);
		mediaService.save(one);
		addMessage("Uploaded Successfully");
	    }
        }
        catch (Exception e) {
            e.printStackTrace();
            addError(""+e);
        }
        return "redirect:/incident/" +  incident_id;
    }
    @GetMapping("/media/download/{id}")
    public ResponseEntity<InputStreamResource> doDownload(@PathVariable int id)
    {
        Media one = mediaService.findById(id);
        if (one != null) {
            try {
                int year     = one.getYear();
                String      fullPath    = storagePath + "/"+year + "/" + one.getFileName();
                File        file        = new File(fullPath);
                String      mimeType    = one.getMimeType();
                HttpHeaders respHeaders = new HttpHeaders();
                respHeaders.setContentType(MediaType.valueOf(mimeType));
                respHeaders.setContentLength(file.length());
                respHeaders.setContentDispositionFormData("attachment", one.getOldFileName());

                InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
                return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
            }
            catch (Exception ex) {
                System.err.println(ex);
            }
        }
        return null;
    }

    @RequestMapping(value = "/media/image/{id}")
    public void picture(HttpServletResponse response, @PathVariable int id) {
	Media media = mediaService.findById(id);
	int year = media.getYear();
	String fullPath    = storagePath + "/"+year + "/" + media.getFileName();
	File  imageFile        = new File(fullPath);
        response.setContentType(media.getMimeType());
	int size = (int)FileUtils.sizeOf(imageFile);
        response.setContentLength(size);
        try {
            InputStream is = new FileInputStream(imageFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            System.err.println("Could not show picture "+e);
        }
     }
		
    String genNewFileName(String file_ext){
        return UUID.randomUUID().toString() + '.' + file_ext;
    }
		
}
