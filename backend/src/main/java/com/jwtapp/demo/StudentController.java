package com.jwtapp.demo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.jwtapp.demo.encrypt.FileInfo;
import com.jwtapp.demo.encrypt.FilesStorageService;

//@PreAuthorize("hasRole('stu')")
@RequestMapping("/student")
@RestController
public class StudentController {

	@Autowired
	FilesStorageService storageService;
	@RequestMapping("/greet")
	public String hello()
	{
		return "Hello student";
	}
	
	
	@PostMapping( value = "/byImageFile", consumes = { "multipart/form-data" })  
	public boolean postMap( @RequestPart ( "imageFile") MultipartFile imageFile)//,   
		//	@RequestPart ( "fieldsToExtract") RequestDto requestDto )
	{
		File f=new File("D:\\Users\\shrey\\Documents\\sts\\JwtApp\\src\\main\\resources\\static\\doc1.png");
		try {
			f.createNewFile();
			imageFile.transferTo(f);
			
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@GetMapping("/files")
	  public ResponseEntity<List<FileInfo>> getListFiles() {
	    List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
	      String filename = path.getFileName().toString();
	      String url = MvcUriComponentsBuilder
	          .fromMethodName(StudentController.class, "getFile", path.getFileName().toString()).build().toString();

	      return new FileInfo(filename, url);
	    }).collect(Collectors.toList());

	    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
	  }
	
	
	@GetMapping(value="/files/{filename}",produces = MediaType.IMAGE_JPEG_VALUE)//:.+
	  public ResponseEntity<File> getFile(@PathVariable String filename) {
	    File file = new File("D:\\Users\\shrey\\Documents\\sts\\JwtApp\\src\\main\\resources\\static\\"+filename+".png");
	    file.canRead();
	   
//	    try {
//			file.createNewFile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	    InputStream in = getClass()
//	    	      .getResourceAsStream("D:\\Users\\shrey\\Documents\\sts\\JwtApp\\src\\main\\resources\\static\\"+filename+".png");
//	    	    return IOUtils.toByteArray(in);
	    return ResponseEntity.ok()//.contentType(MediaType.parseMediaType())   
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"").body(file);
	  }
	
	
//	@GetMapping("/files1/{filename}")
//	  public ResponseEntity<FileInfo> getListFiles(@PathVariable String filename) {
//	    FileInfo fileInfos = ((Object) new File("D:\\Users\\shrey\\Documents\\sts\\JwtApp\\src\\main\\resources\\static\\"+filename)).map(path -> {
//	      String filename = path.getFileName().toString();
//	      String url = MvcUriComponentsBuilder
//	          .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();
//
//	      return new FileInfo(filename, url);
//	    }).collect(Collectors.toList());
//
//	    return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
//	  }

}
