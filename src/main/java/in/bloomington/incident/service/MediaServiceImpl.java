package in.bloomington.incident.service;
/**
 * @copyright Copyright (C) 2014-2015 City of Bloomington, Indiana. All rights reserved.
 * @license http://www.gnu.org/copyleft/gpl.html GNU/GPL, see LICENSE.txt
 * @author W. Sibo <sibow@bloomington.in.gov>
 *
 */

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import java.util.stream.Stream;
import in.bloomington.incident.repos.MediaRepository;
import in.bloomington.incident.utils.UploadException;
import in.bloomington.incident.model.Media;

@Service
public class MediaServiceImpl implements MediaService {


		@Autowired
		MediaRepository repository;

		@Override
		public void save(Media val){
				repository.save(val);
		}
		@Override
		public Media findById(int id){
				Media media = repository.findById(id)
						.orElseThrow(() -> new IllegalArgumentException("Invalid media Id:" + id));
				return media;
		}						
		@Override
		public void update(Media media){
				repository.save(media);
		}
		@Override
		public void delete(int id){
				repository.deleteById(id);
		}
		

}
