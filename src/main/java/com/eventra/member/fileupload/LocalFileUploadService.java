package com.eventra.member.fileupload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalFileUploadService implements FileUploadService {
	
	// 定義根目錄：把所有上傳檔案都放在專案根目錄下的 uploads 資料夾
	
	private final Path root;
	
	public LocalFileUploadService(@Value("${app.upload.dir}") String localRoot) {
		this.root = Paths.get(localRoot);
	}
	// .resolve 是 Java NIO Path API 的方法，用來「拼接路徑」-> 等於一個安全的 String 拼接，並且自動處理 / 問題
		// Path root = Paths.get("upload");
		// Path subDir = root.resolve("avatars"); // uploads/avatars
		// Path filePath = subDir.resolve("abc.png"); // uploads/avatars/abc.png
	// .normalize 會「標準化路徑」，把裡面多餘的 . 或 .. 拿掉 -> 避免有人故意傳 ../../etc/passwd 類似的路徑，降低目錄穿越攻擊風險
		// Path path = root.resolve("../uploads/avatars/../abc.png").normalize(); // uploads/abc.png
	
	@Override
	public String save(MultipartFile file, FileCategory type) {
		String dir = type.toString();
		try {
			// 0. 限制副檔名 / MIME Type -> 避免上傳可執行檔等等
			String contentType = file.getContentType(); // 例如 image/jpdg
			// 白名單示意
			Set<String> allow = Set.of("image/png", "image/jpeg", "image/gif", "application/pdf");
			if(contentType == null || !allow.contains(contentType)) {
				throw new IllegalArgumentException("Unsupported file type");
			}
			
			// 1. 如果 uploads/dir 子目錄不存在就建立（例如 dir = "avatars" -> uploads/avatars）
			if (!Files.exists(root.resolve(dir))) {
				Files.createDirectories(root.resolve(dir));
			}
			
			// 2. 檔名淨化 -> 配合 UUID 產生
			String original = file.getOriginalFilename();
			// getOriginalFilename() 可能按照瀏覽器或 OS 會帶完整路徑 -> getFileName() 本身就只取最後一段檔名，所以可以把惡意路徑前綴丟掉
			// 而 normalize() 是針對「完整路徑」才有意義 -> 例如 Paths.get("abc/../xyz.png").normalize() -> xyz.png -> 所以如果 getFileName() 已經只取檔名，就不用 .normalize()
			String safeOriginal = original == null ? "file" : Paths.get(original).getFileName().toString();
			// 產生不重複的檔名：UUID_原檔名
			String filename = UUID.randomUUID() + "_" + safeOriginal;
			
			// 目標路徑：uploads/dir/filename
			Path filePath = root.resolve(dir).resolve(filename);
			
			// 3. 寫入：把上傳 inputstream 複製到目標路徑
			// REPLACE_EXISTING：若檔案名稱意外重複，就直接覆蓋（但有 UUID 加上理論上不會）
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			
			// 4. 回傳給前端可直接 <img src="..."> 使用的 URL 路徑
			return "/" + dir + "/" + filename;
			
		} catch (IOException e) {
			// 任何 IO 問題（建立目錄/寫檔失敗）都轉成 RuntimeException 讓上層決定如何回應
			throw new RuntimeException("Could not save file", e);
		}
	}
	
	@Override
	public Resource load(String path) {
		try {
			// 將呼叫端（前端）給的相對路徑（例如 "avatars/xxx.png"）拼成完整檔案路徑
			// normalize() 可移除路徑中的 .. 等片段，降低目錄跳脫風險
			Path filePath = root.resolve(path).normalize();
			
			if(!filePath.startsWith(root)) throw new SecurityException("invalid path"); 
			
			// 將實體檔案轉成 Spring 的 Resource 物件，方便 Controller 以串流回傳
			// new UrlResource(filePath.toUri()) -> 把 本地檔案路徑 轉成 URI（統一資源識別符）
				// Path 是 Java 本地的檔案路徑抽象(OS dependent，Windows/Linux 表示法不同)
				// URI 是網路協議統一格式，可以代表
					// file:///Users/xxx/uploads/abc.png -> 本地檔案
					// http://example.com/file.png -> 遠端檔案
				// Spring Resource API 設計就是統一走 URI，不管是本地檔案還是 URL，用 UrlResource 包裝後都能用同介面處理
			return new UrlResource(filePath.toUri());
			
		} catch (Exception e) {
			throw new RuntimeException("Could not load file", e);
		}
	}
}

//transferTo 是 「我已經在玩 Stream」 的最短路；
//Files.copy 是 「我要搬檔案或在 Path/Stream 間拷貝並控制行為」 的萬用工具，還能吃到覆蓋/屬性與底層最佳化。

//// 以 NIO.2 儲存上傳檔
//Files.createDirectories(uploadDir);
//try (var in = multipartFile.getInputStream();
//     var out = Files.newOutputStream(uploadDir.resolve(filename))) {
//    in.transferTo(out);
//}
//
//// Path <-> Stream
//try (var in = Files.newInputStream(path);
//     var out = response.getOutputStream()) {
//    in.transferTo(out);
//}
//
//// 零拷貝（檔案 -> 網路 Channel；非 Servlet 環境較好用）
//try (var fc = FileChannel.open(path, StandardOpenOption.READ)) {
//    long pos = 0, size = fc.size();
//    while (pos < size) {
//        pos += fc.transferTo(pos, size - pos, socketChannel);
//    }
//}
