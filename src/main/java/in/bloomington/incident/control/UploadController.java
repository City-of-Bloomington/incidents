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
// import org.springframework.core.envnvironment;
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

    @Value("${spring.servlet.multipart.location}")
    private String storagePath;
    @Value("${incident.media.max.size}")
    private int mediaMaxSize; // MB
    @Value("${incident.person.media.count}")
    private int mediaPersonMaxCount;
    @Value("${incident.business.media.count}")
    private int mediaBusinessMaxCount;		
		
    @GetMapping("/media/add/{id}")
    public String mediaForm(@PathVariable("id") int id,
			    Model model,
			    RedirectAttributes redirectAttributes){
	Incident incident = incidentService.findById(id);
	model.addAttribute("app_url",app_url);
	try{
	    int mediaMaxCount = mediaPersonMaxCount;
	    int media_count = incident.getMediaCount();
	    if(media_count < mediaMaxCount){
		model.addAttribute("incident_id", id);
		model.addAttribute("media_count", media_count);
		model.addAttribute("media_max_count", mediaMaxCount);
		model.addAttribute("media_max_size", mediaMaxSize);
		return "mediaAdd";
	    }
	    else{
		redirectAttributes.addFlashAttribute("message","No more images can be uploaded");
	    }
	}catch(Exception ex){
	    redirectAttributes.addFlashAttribute("error",ex);						
	    System.err.println(""+ ex);
	}
	if(incident.isBusinessRelated()){
	    return "redirect:/businessIncident/"+id;
	}
	return "redirect:/incident/"+id;
    }
    @GetMapping("/businessMedia/add/{id}")
    public String busMediaForm(@PathVariable("id") int id,
			       Model model,
			       RedirectAttributes redirectAttributes){
	Incident incident = incidentService.findById(id);
	model.addAttribute("app_url",app_url);
	try{
	    int media_count = incident.getMediaCount();
	    if(media_count == 0){
		addMessage("You are required to upload a photo of the incident or receipt");
		model.addAttribute("messages", messages);
	    }
	    int mediaMaxCount = mediaBusinessMaxCount;
	    if(media_count < mediaMaxCount){
		model.addAttribute("incident_id", id);
		model.addAttribute("incident",incident);
		model.addAttribute("media_count", media_count);
		model.addAttribute("media_max_count", mediaMaxCount);
		model.addAttribute("media_max_size", mediaMaxSize);
		return "businessMediaAdd";
	    }
	    else{
		redirectAttributes.addFlashAttribute("message","No more images can be uploaded");
	    }
	}catch(Exception ex){
	    addError(""+ex);
	    redirectAttributes.addFlashAttribute("error",""+ex);						
	    System.err.println(""+ ex);
	}
	return "redirect:/businessIncident/"+id;
    }		
		
    // delete by id
    @GetMapping("/media/delete/{id}")
    public String deleteAttachment(@PathVariable("id") int id, Model model)
    {
        Media media  = mediaService.findById(id);
        Incident     incident       = media.getIncident();
        mediaService.delete(id);
	addMessage("Photo deleted successfully");
	model.addAttribute("app_url", app_url);
	if(incident.isBusinessRelated()){
	    return "redirect:/businessIncident/"+incident.getId();
	}
        return 
            "redirect:/incident/" + incident.getId();

    }


    @PostMapping("/media/save")
    public String doUploadAndSave(@RequestParam("files" ) MultipartFile[] files,
				  @RequestParam("incident_id"   ) int  incident_id,
				  @RequestParam("notes") String  notes,
				  RedirectAttributes redirectAttributes
				  ){
        String fileName = null;
        if (files == null) {
	    addMessage("Please select a file to upload");
            return "redirect:media/add/" + incident_id;
        }
	int jj = 0;
	for(MultipartFile file:files){
	    if(file == null || file.isEmpty()) continue;
	    jj++;
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
		    redirectAttributes.addFlashAttribute("error",back);
		}
		else{
		    Path path = Paths.get(dirPath + newName);
		    Files.write(path, bytes);
		    Media one = new Media();
		    one.setFileName   (newName   );
		    one.setOldFileName(oldFileName  );
		    if(jj == 1)
			one.setNotes(notes);
		    one.setYear(year);
		    one.setMimeType(mimeType);
		    Incident incident = incidentService.findById(incident_id);
		    one.setIncident(incident);
		    mediaService.save(one);
		    if(jj == 1){
			addMessage("Uploaded Successfully");
			redirectAttributes.addFlashAttribute("message",
							     "Uploaded Successfully");
		    }
		}
	    }
	    catch (Exception e) {
		e.printStackTrace();
		addError(""+e);
	    }
	}
	Incident incident = incidentService.findById(incident_id);
	if(incident.isBusinessRelated()){
	    return "redirect:/businessIncident/" +  incident_id;
	}
        return "redirect:/incident/" +  incident_id;
    }
    @GetMapping("/media/download/{id}")
    public ResponseEntity<InputStreamResource> doDownload(@PathVariable int id,
							  Model model)
    {
	model.addAttribute("app_url", app_url);
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
    public void picture(HttpServletResponse response, @PathVariable int id,
			Model model) {
	model.addAttribute("app_url", app_url);
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
