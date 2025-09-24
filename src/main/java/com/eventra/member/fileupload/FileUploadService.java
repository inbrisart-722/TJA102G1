package com.eventra.member.fileupload;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
	
	String save(MultipartFile file, FileCategory type);
	Resource load(String path);
	
}
