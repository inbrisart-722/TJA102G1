package com.eventra.exhibition.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.comment.controller.CommentStatus;
import com.eventra.comment.model.CommentRepository;

import com.eventra.exhibitionstatus.model.ExhibitionStatusVO;

import com.eventra.eventnotification.model.EventNotificationService.NotificationType;

import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeRepository;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;
import com.eventra.exhibitor.backend.controller.dto.ExhibitionCreateDTO;
import com.eventra.exhibitor.model.ExhibitorDTO;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.notificationpush.model.NotificationPushService;
import com.eventra.tickettype.model.TicketTypeRepository;
import com.eventra.tickettype.model.TicketTypeVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ExhibitionServiceImpl implements ExhibitionService {

	private final ExhibitionRepository repository;
	private final ExhibitionTicketTypeRepository exhibitionTicketTypeRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private record TicketJsonItem(String name, Integer price) {
	}
	


	private final CommentRepository commentRepository;
	
	// peichenlu: 收藏展覽通知(event notification) 需要
	private final NotificationPushService notificationPushService;

	@PersistenceContext
	private EntityManager entityManager;
	
	private static final int DEFAULT_STATUS_ID = 1;
	private static final int DRAFT_STATUS_ID = 6;
	private final String DEFAULT_PHOTO_LANDSCAPE;

	@Autowired
	public ExhibitionServiceImpl(ExhibitionRepository repository, CommentRepository commentRepository, ExhibitionTicketTypeRepository exhibitionTicketTypeRepository, TicketTypeRepository ticketTypeRepository, @Value("${default.exhibition-photo-landscape}") String defaultPhotoLandscape, NotificationPushService notificationPushService) {
		this.repository = repository;
		this.exhibitionTicketTypeRepository = exhibitionTicketTypeRepository;
		this.ticketTypeRepository = ticketTypeRepository;
		this.commentRepository = commentRepository;
		this.DEFAULT_PHOTO_LANDSCAPE = defaultPhotoLandscape;
		this.notificationPushService = notificationPushService;
	}

	@Transactional
	public void addExhibition(ExhibitionCreateDTO dto, Integer exhibitorId) {

		
//		for(ExhibitionTicketTypeVO vo :  dto.getExhibitionTicketTypes()){
//			System.out.println(vo.getExhibitionTicketTypeId() + ": " + vo.getPrice());
//		};
		
		
	
		// 1) 將 DTO 資料轉換成 Entity(VO)
		ExhibitionVO exhibitionVO = ExhibitionMapper.toVO(dto);

		// 若是草稿 -> 草稿id，否則預設
		if(Boolean.TRUE.equals(dto.getDraft())) {
			exhibitionVO.setExhibitionStatusId(DRAFT_STATUS_ID);
		}else if(exhibitionVO.getExhibitionStatusId() == null) {
			exhibitionVO.setExhibitionStatusId(DEFAULT_STATUS_ID);
		}
		
//	    exhibitionVO.setExhibitionStatus(entityManager.getReference(ExhibitionStatusVO.class, 1));
		
		if (exhibitionVO.getExhibitionStatusId() == null) {
	        exhibitionVO.setExhibitionStatusId(DEFAULT_STATUS_ID); 
	    }

		exhibitionVO.setExhibitorVO(entityManager.getReference(ExhibitorVO.class, exhibitorId));

		/** 圖片儲存處理 **/
		// 2) 第一次 save : 先存到 DB 取得自增 id
		ExhibitionVO saved = repository.save(exhibitionVO);
		Integer id = saved.getExhibitionId(); // 取得資料夾名稱要用的 id

		// 3) 建立 /static/uploads/exhibitions/{id}/ 目錄
		Path baseDir = Paths.get("/Users/lianliwei/uploads/exhibitions", String.valueOf(id));
		try {
			Files.createDirectories(baseDir);
		} catch (IOException e) {
			throw new RuntimeException("建立圖片目錄失敗: " + baseDir, e);
		}

		// 4) 存portrait
		MultipartFile portrait = dto.getPhotoPortrait();
		if(portrait != null && !portrait.isEmpty()) {
			String filename = "p_" + UUID.randomUUID() + "_" + portrait.getOriginalFilename();
			try {
				portrait.transferTo(baseDir.resolve(filename));
				saved.setPhotoPortrait("uploads/exhibitions/" + id + "/" + filename);
			}catch(IOException e) {
				throw new RuntimeException("存 portrait 失敗", e);
			}
		}
		
		// 5) 存 landscape
		MultipartFile landscape = dto.getPhotoLandscape();
		if(landscape != null && !landscape.isEmpty()) {
			String filename = "l_" + UUID.randomUUID() + "_" + landscape.getOriginalFilename();
			try {
				landscape.transferTo(baseDir.resolve(filename));
				saved.setPhotoLandscape("uploads/exhibitions/" + id + "/" + filename);
			}catch(IOException e) {
				throw new RuntimeException("存 landscape 失敗", e);
			}
		}

		// 6) 第二次 save : 更新圖片路徑
		repository.save(saved);

		// 7) 票種：解析 ticketJson 並寫入子表（最小改動、逐筆 save）
		List<TicketJsonItem> ticketJsonItem = parseTicketJson(dto.getTicketJson());
		if(ticketJsonItem.isEmpty()) {
			// 允許只選部分，但至少要有一個有效票種
			throw new IllegalArgumentException("請至少設定一種有效票種(需有價格");
		}
		
		for(TicketJsonItem it : ticketJsonItem) {
			// 依名稱(全票/學生票/...) 找 TicketType
			TicketTypeVO ticketTypeVO = ticketTypeRepository.findByTicketTypeName(it.name())
					.orElseThrow(() -> new IllegalArgumentException("找不到票種類型: " + it.name()));
			
			ExhibitionTicketTypeVO ett = new ExhibitionTicketTypeVO();
			ett.setExhibition(saved);
			ett.setTicketType(ticketTypeVO);
			ett.setPrice(it.price());
			exhibitionTicketTypeRepository.save(ett);
			
		}
	}
	// 解析前端 hidden 的 ticketJson，只保留「有名稱且價格 >= 0」的項目
	private List<TicketJsonItem>  parseTicketJson(String json){
		if(json == null || json.isBlank()) {
			return List.of();
		}
		try {
			ObjectMapper om = new ObjectMapper();
			List<Map<String, Object>> raw = om.readValue(json, new TypeReference<>() {});
			List<TicketJsonItem> list = new ArrayList<>();
            for (Map<String, Object> m : raw) {
                String name = String.valueOf(m.get("name")).trim();
                if (name.isEmpty() || "null".equalsIgnoreCase(name)) continue;

                Object p = m.get("price");
                if (p == null) continue;
                String ps = String.valueOf(p).trim();
                if (ps.isEmpty() || "null".equalsIgnoreCase(ps)) continue;

                int price = Integer.parseInt(ps);
                if (price < 0) continue;

                list.add(new TicketJsonItem(name, price));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("票種 JSON 解析失敗", e);
        }
	}
	
//	// 實作展覽查詢方法
//	public List<ExhibitionVO> getAllExhibitions(){
//			return repository.findAll();
//	}
		
	// 分頁查詢
	public Page<ExhibitionVO> getExhibitionsPage(int page, int size){
		Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
		return repository.findAll(pageable);
	}

	// 查詢單筆
	public ExhibitionVO findById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	// 實作展覽編輯後更新
	@Override
	@Transactional
	public void updateExhibition(ExhibitionCreateDTO dto, Integer exhibitorId) {
	    if (dto.getExhibitionId() == null) {
	        throw new IllegalArgumentException("缺少 exhibitionId，無法更新");
	    }

	    // 1) 取原資料
	    ExhibitionVO vo = repository.findById(dto.getExhibitionId())
	            .orElseThrow(() -> new IllegalArgumentException("展覽不存在: " + dto.getExhibitionId()));

	    // （可選）安全檢查：確認是此展商的展覽
	    // if (!vo.getExhibitorVO().getExhibitorId().equals(exhibitorId)) throw new AccessDeniedException("無權限");

	    // peichenlu: 展覽資訊判斷異動 (LOCATION_CHANGE / TIME_CHANGE)
	    boolean locationChanged = dto.getLocation() != null && !dto.getLocation().equals(vo.getLocation());
        boolean timeChanged =
                (dto.getStartTime() != null && !dto.getStartTime().equals(vo.getStartTime())) ||
                (dto.getEndTime() != null && !dto.getEndTime().equals(vo.getEndTime()));
	    
	    // 2) 更新基本欄位
	    vo.setExhibitionName(dto.getExhibitionName());
	    vo.setStartTime(dto.getStartTime());
	    vo.setEndTime(dto.getEndTime());
	    vo.setLocation(dto.getLocation());
	    vo.setTicketStartTime(dto.getTicketStartTime());
	    vo.setTotalTicketQuantity(dto.getTotalTicketQuantity());
	    vo.setDescription(dto.getDescription());
	    if (vo.getExhibitionStatusId() == null) {
	    	vo.setExhibitionStatusId(DEFAULT_STATUS_ID);
	    }

	    // 3) 有上傳才覆蓋
	    Integer id = vo.getExhibitionId(); // 取得目前正在編輯的展覽主見
	    Path baseDir = Paths.get("/Users/lianliwei/uploads/exhibitions", String.valueOf(id)); // 組出該展覽專屬的檔案夾路徑
	    try {
	    	Files.createDirectories(baseDir); // 確保目錄存在;若不存在就建立，失敗也只是略過
	    }catch(IOException ignore) {		  
	    	
	    }
	    MultipartFile portrait = dto.getPhotoPortrait();
	    if(portrait != null && !portrait.isEmpty()) {  // 只有使用者真的有上傳才處理覆蓋
	    	String filename = "p_" + UUID.randomUUID() + "_" + portrait.getOriginalFilename();
	    	try {
	    		portrait.transferTo(baseDir.resolve(filename)); // 把上傳檔存進該展覽資料夾
	    		vo.setPhotoPortrait("uploads/exhibitions/" + id + "/" + filename); // 在資料庫欄位存相對路徑，供前端顯示用
	    	}catch(IOException e) {
	    		throw new RuntimeException("存portrait失敗", e);
	    	}
	    }
	    // 同上，改成landscape
	    MultipartFile landscape = dto.getPhotoLandscape(); 
	    if(landscape != null && !landscape.isEmpty()) {  
	    	String filename = "l_" + UUID.randomUUID() + "_" + landscape.getOriginalFilename();
	    	try {
	    		landscape.transferTo(baseDir.resolve(filename)); 
	    		vo.setPhotoLandscape("uploads/exhibitions/" + id + "/" + filename); // 在資料庫欄位存相對路徑，供前端顯示用
	    	}catch(IOException e) {
	    		throw new RuntimeException("存landscape失敗", e);
	    	}
	    }
	    
	    // 按儲存為草稿按鈕後變更展覽狀態為草稿
	    if (Boolean.TRUE.equals(dto.getDraft())) {
            vo.setExhibitionStatusId(DRAFT_STATUS_ID);
        }
	    // 4)
	    repository.save(vo); // 儲存展覽本體，把剛剛更新的圖片路徑寫回資料庫
	    
	    // ========= 票種差異同步 =========
	    
	    // 1) 解析前端送來的 ticketJson
	    List<TicketJsonItem> items = parseTicketJson(dto.getTicketJson()); // 將隱藏欄位 ticketJson (JSON 字串) 解析成物件清單 (name, price)
	    
	    // 2) 合併同名 (避免前端重複名稱造成重複 insert)
	    Map<String, Integer> incomingByName = new LinkedHashMap<>(); // 用名稱作key，LinkedHashMap保留順序
	    for(TicketJsonItem it : items) {
	    	String name = (it.name() == null ? "" : it.name().trim()); // 取出名稱並去頭尾空白，null視為空字串
	    	if(name.isEmpty() || it.price() == null) { // 名稱空或價格為空的資料忽略
	    		continue;
	    	}
	    	incomingByName.put(name, it.price());
	    }
	    
	    // 3) 把DB既有的票種建索引
	    List<ExhibitionTicketTypeVO> existing = exhibitionTicketTypeRepository.findByExhibitionId(vo.getExhibitionId()); // 查出此展覽目前在DB中所有票種
	    Map<String, ExhibitionTicketTypeVO> existingByName = new HashMap<>();
	    for(ExhibitionTicketTypeVO row : existing) {
	    	existingByName.put(row.getTicketType().getTicketTypeName(), row);
	    }
	    
	    // 4) upsert : 有就更新價格，沒有就新增
	    for(Map.Entry<String, Integer> en : incomingByName.entrySet()) { // 遍歷前端送來的所有去重後的票種
	    	String name = en.getKey();
	    	Integer price = en.getValue();
	    	
	    	var type = ticketTypeRepository.findByTicketTypeName(name).orElseThrow (()-> new IllegalArgumentException("找不到票種類型" + name)); // 依名稱茶主檔 ticketType
	    	ExhibitionTicketTypeVO row = existingByName.get(name); // 從既有 map 取得該名稱的 row
	    	if(row != null) {
	    		// 已存在 -> 更新價格
	    		if(!Objects.equals(row.getPrice(), price)) {
	    			row.setPrice(price);
	    			exhibitionTicketTypeRepository.save(row);
	    		}
	    	}else {
	    		// 不存在 -> 新增
	    		ExhibitionTicketTypeVO ett = new ExhibitionTicketTypeVO();
	    		ett.setExhibition(vo);
	    		ett.setTicketType(type);
	    		ett.setPrice(price);
	    		exhibitionTicketTypeRepository.save(ett);
	    	}
	    }
	    
	    // 5) 刪除 DB 有但前端沒送的票種 (若有訂單關聯就不要刪)
	    Set<String> incomingNames = incomingByName.keySet(); // 前端本次送來的所有票種名稱集合
	    for(ExhibitionTicketTypeVO row : existing) { // 檢查 DB 既有的每一筆
	    	String name = row.getTicketType().getTicketTypeName();
	    	if(!incomingNames.contains(name)) {
	    		if(row.getOrderItems() == null || row.getOrderItems().isEmpty()) {
	    			exhibitionTicketTypeRepository.delete(row); // 無訂單才刪
	    		}
	    	}
	    }
	    
	    // peichenlu: 發送通知
	    if (locationChanged) {
	    	notificationPushService.sendExhibitionNotification(vo.getExhibitionId(), NotificationType.LOCATION_CHANGE);
        }
        if (timeChanged) {
        	notificationPushService.sendExhibitionNotification(vo.getExhibitionId(), NotificationType.TIME_CHANGE);

        }
	    
	}
	// inbrisart 20250925 給展覽頁 SSR 帶入
	/**
	 * @param exhibitionId
	 * @return
	 */
	public ExhibitionPageDTO getExhibitionInfoForPage(Integer exhibitionId) {
		ExhibitionVO exhibition = repository.findById(exhibitionId).orElse(null);
		if(exhibition == null) return null;
		Set<ExhibitionTicketTypeVO> etts = exhibition.getExhibitionTicketTypes();
		ExhibitionPageDTO dto = new ExhibitionPageDTO();
		String photoLandscape = exhibition.getPhotoLandscape() != null ? exhibition.getPhotoLandscape() : DEFAULT_PHOTO_LANDSCAPE;
		dto.setExhibitionId(exhibitionId);
		dto.setPhotoLandscape(photoLandscape);
		dto.setExhibitionName(exhibition.getExhibitionName());
		dto.setAverageRatingScore(exhibition.getAverageRatingScore());
		dto.setTotalRatingCount(exhibition.getTotalRatingCount());
		dto.setLeftTicketQuantity(exhibition.getTotalTicketQuantity() - exhibition.getSoldTicketQuantity());
		dto.setTicketStartTime(exhibition.getTicketStartTime());
		Boolean isTicketStart = LocalDateTime.now().isAfter(exhibition.getTicketStartTime());
		dto.setIsTicketStart(isTicketStart);
		Boolean isExhibitionEnded = LocalDateTime.now().isAfter(exhibition.getEndTime());
		dto.setIsExhibitionEnded(isExhibitionEnded);
		Map<Integer, Integer> tickets = new HashMap<>();
		Set<ExhibitionTicketTypeVO> ettVOs = exhibition.getExhibitionTicketTypes();
		for(ExhibitionTicketTypeVO ettVO : ettVOs)
			tickets.put(ettVO.getTicketTypeId(), ettVO.getPrice());
		
		// ??
//		List<String> ticketOrder = List.of("全票", "學生票", "敬老票", "身心障礙者票", "軍警票");
//		tickets.put("");
//		Map<String, Integer> tickets = exhibition.getExhibitionTicketTypes()
//				.stream()
//				.sorted(Comparator.comparingInt(
//						ett -> ticketOrder.indexOf(ett.getTicketType().getTicketTypeName())))
//				.collect(Collectors.toMap(
//						ett -> ett.getTicketType().getTicketTypeName(),
//						ExhibitionTicketTypeVO::getPrice,
//						(v1, v2) -> v1,
//						LinkedHashMap::new
//				));
		
		dto.setTickets(tickets);
				
		Integer cheapestTicketPrice = Integer.MAX_VALUE;
		for(ExhibitionTicketTypeVO vo : etts)
			if(vo.getPrice() < cheapestTicketPrice) cheapestTicketPrice = vo.getPrice();
		
		dto.setCheapestTicketPrice(cheapestTicketPrice);
		dto.setStartTime(exhibition.getStartTime());
		dto.setEndTime(exhibition.getEndTime());
		dto.setLocation(exhibition.getLocation());
		dto.setDescription(exhibition.getDescription());
		ExhibitorVO exhibitorVO = exhibition.getExhibitorVO();
		String exhibitorDisplayName = 
				exhibitorVO.getExhibitorRegistrationName() != null
				? exhibitorVO.getExhibitorRegistrationName()
				: exhibitorVO.getCompanyName();
		
		ExhibitorDTO exhibitorDTO = new ExhibitorDTO();
		exhibitorDTO
			.setExhibitorId(exhibitorVO.getExhibitorId())
			.setExhibitorDisplayName(exhibitorDisplayName);
		
		dto.setExhibitor(exhibitorDTO); 
		Integer totalCommentCount = commentRepository.countByExhibitionId(CommentStatus.正常.toString(), exhibitionId);
		System.out.println("totalCommentCount: " + totalCommentCount);
		dto.setTotalCommentCount(totalCommentCount);
		
		return dto;
	}

	private Pageable defaultPageable(int page, int size) {
	    return PageRequest.of(page, size, Sort.by("startTime").descending());
	}

	@Override
	public Page<ExhibitionVO> findAll(Integer exhibitorId, int page, int size, String q) {
		return repository.findByExhibitorVO_ExhibitorIdAndExhibitionNameContainingIgnoreCase(exhibitorId, (q == null ? "" : q), defaultPageable(page, size));
	}

	@Override
	public Page<ExhibitionVO> findDrafts(Integer exhibitorId, Integer draftStatusId, int page, int size, String q) {
		return repository.findDrafts(exhibitorId, (draftStatusId != null ? draftStatusId : DRAFT_STATUS_ID), (q == null ? "" : q), defaultPageable(page, size));
	}

	@Override
	public Page<ExhibitionVO> findNotOnSale(Integer exhibitorId, int page, int size, String q) {
		return repository.findNotOnSale(exhibitorId, DRAFT_STATUS_ID, (q == null ? "" : q), defaultPageable(page, size));
	}

	@Override
	public Page<ExhibitionVO> findOnSale(Integer exhibitorId, int page, int size, String q) {
		return repository.findOnSale(exhibitorId, DRAFT_STATUS_ID, (q == null ? "" : q), defaultPageable(page, size));
	}

	@Override
	public Page<ExhibitionVO> findEnded(Integer exhibitorId, int page, int size, String q) {
		return repository.findEnded(exhibitorId, DRAFT_STATUS_ID, (q == null ? "" : q), defaultPageable(page, size));
	}

	public ExhibitionSidebarResultDTO findSidebarExhibitionsByRatingScore(Integer exhibitionId, Double averageRatingScore){
		Pageable pageable = PageRequest.ofSize(5);
		Slice<ExhibitionVO> exhibitionsSlice = repository.findExhibitionsByAverageRatingScoreDesc(averageRatingScore, exhibitionId, pageable);
		
		List<ExhibitionSidebarDTO> dtos = exhibitionsSlice.getContent()
				.stream().map(vo -> new ExhibitionSidebarDTO()
						.setAverageRatingScore(vo.getAverageRatingScore())
						.setExhibitionId(vo.getExhibitionId())
						.setPhotoPortrait(vo.getPhotoPortrait())
						.setExhibitionName(vo.getExhibitionName())
						.setLocation(vo.getLocation())
						.setStartTime(vo.getStartTime())
						.setEndTime(vo.getEndTime())
						.setTotalRatingCount(vo.getTotalRatingCount())
				).collect(Collectors.toList());
		
		ExhibitionSidebarResultDTO dtoResult = new ExhibitionSidebarResultDTO();
		dtoResult.setHasNextPage(exhibitionsSlice.hasNext())
				 .setList(dtos);
		
		return dtoResult;
	}
	
	public Slice<ExhibitionLineBotCarouselDTO> findHotExhibitionsForLineBot(){
		return null;
	}
	
	public Slice<ExhibitionLineBotCarouselDTO> findUpcomingExhibitionsForLineBot(int page, int size){
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startTime"));
		
		Slice<ExhibitionVO> exhibitionsSlice = repository.findByStartTimeAfter(LocalDateTime.now(), pageable); 
		List<ExhibitionVO> exhibitions = exhibitionsSlice.getContent();
		
		List<ExhibitionLineBotCarouselDTO> dtos =
			exhibitions.stream().map(vo -> {
			ExhibitionLineBotCarouselDTO dto = new ExhibitionLineBotCarouselDTO();
			dto
				.setExhibitionName(vo.getExhibitionName())
//				.setPhotoPortrait(vo.getPhotoPortrait())
				// 測試
				.setPhotoPortrait("https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_1_cafe.png")
				.setAverageRatingScore(vo.getAverageRatingScore())
				.setLocation(vo.getLocation())
				.setStartTime(vo.getStartTime())
				.setEndTime(vo.getEndTime())
				// 測試
				.setPageUrl("http://localhost:8088/front-end/exhibitions?exhibitionId=" + vo.getExhibitionId());
			return dto;
		}).collect(Collectors.toList());
		
		return new SliceImpl<>(dtos, pageable, exhibitionsSlice.hasNext());
	}
	
	public Slice<ExhibitionLineBotCarouselDTO> findNewExhibitionsForLineBot(){
		return null;
	}
	
	public Slice<ExhibitionLineBotCarouselDTO> findNearestExhibitionsForLineBot(Double lat, Double lng, int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		Slice<ExhibitionVO> exhibitionsSlice =repository.findNearestExhibition(lat, lng, pageable);
		List<ExhibitionVO> exhibitions = exhibitionsSlice.getContent();
		
		List<ExhibitionLineBotCarouselDTO> dtos =
			exhibitions.stream().map(vo -> {
			ExhibitionLineBotCarouselDTO dto = new ExhibitionLineBotCarouselDTO();
			dto
				.setExhibitionName(vo.getExhibitionName())
//				.setPhotoPortrait(vo.getPhotoPortrait())
				// 測試
				.setPhotoPortrait("https://scdn.line-apps.com/n/channel_devcenter/img/fx/01_1_cafe.png")
				.setAverageRatingScore(vo.getAverageRatingScore())
				.setLocation(vo.getLocation())
				.setStartTime(vo.getStartTime())
				.setEndTime(vo.getEndTime())
				// 測試
				.setPageUrl("http://localhost:8088/front-end/exhibitions?exhibitionId=" + vo.getExhibitionId());
			return dto;
		}).collect(Collectors.toList());
		
		return new SliceImpl<>(dtos, pageable, exhibitionsSlice.hasNext());
	}
}
