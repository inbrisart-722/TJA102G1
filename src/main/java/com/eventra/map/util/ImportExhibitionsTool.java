package com.eventra.map.util;

import com.eventra.exhibition.model.ExhibitionRepository;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.exhibitionstatus.model.ExhibitionStatusVO;
import com.eventra.exhibitor.model.ExhibitorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ImportExhibitionsTool {

	@Autowired
	private ExhibitionRepository exhibitionRepository;

	private static final String API_URL = "https://cloud.culture.tw/frontsite/trans/SearchShowAction.do?method=doFindTypeJ&category=6";

	public void importData() throws Exception {
		System.out.println("===== 開始抓取文化部 JSON =====");

		RestTemplate restTemplate = new RestTemplate();
		CultureExhibition[] exhibitions = restTemplate.getForObject(API_URL, CultureExhibition[].class);

		if (exhibitions != null) {
			for (CultureExhibition e : exhibitions) {
				ExhibitionVO vo = new ExhibitionVO();

//				// 建立展覽狀態 VO（假設 4 ）
				vo.setExhibitionStatusId(4);

				// 建立展商VO (先用隨機 1–6)
				ExhibitorVO exhibitorVO = new ExhibitorVO();
				exhibitorVO.setExhibitorId(ThreadLocalRandom.current().nextInt(1, 7));
				vo.setExhibitorVO(exhibitorVO);

				// 來自 JSON
				vo.setExhibitionName(e.getTitle());
				
				String desc = (e.getDescriptionFilterHtml() != null && !e.getDescriptionFilterHtml().isEmpty())
				        ? e.getDescriptionFilterHtml()
				        : e.getDescription();
				vo.setDescription(desc);
				
				if (e.getShowInfo() != null && e.getShowInfo().length > 0) {
				    ShowInfo info = e.getShowInfo()[0]; // 先抓第一筆
				    vo.setLocation(info.getLocation());

				    try {
				        if (info.getLatitude() != null && !info.getLatitude().isEmpty()) {
				            vo.setLatitude(Double.parseDouble(info.getLatitude()));
				        }
				        if (info.getLongitude() != null && !info.getLongitude().isEmpty()) {
				            vo.setLongitude(Double.parseDouble(info.getLongitude()));
				        }
				    } catch (NumberFormatException ex) {
				        System.out.println("經緯度格式錯誤: " + e.getTitle());
				    }
				}


				// 時間轉換（JSON 多半是 yyyy/MM/dd HH:mm:ss）
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				try {
					LocalDateTime start = LocalDateTime.parse(e.getStartDate() + " 00:00:00", formatter);
					LocalDateTime end = LocalDateTime.parse(e.getEndDate() + " 23:59:59", formatter);
					vo.setStartTime(Timestamp.valueOf(start).toLocalDateTime());
					vo.setEndTime(Timestamp.valueOf(end).toLocalDateTime());
				} catch (Exception ex) {
					System.out.println("時間轉換失敗: " + e.getTitle());
				}

				// 預設值
				vo.setTotalTicketQuantity(0);
				vo.setSoldTicketQuantity(0);
				vo.setTotalRatingCount(0);
				vo.setTotalRatingScore(0);

				// 存進資料庫
				exhibitionRepository.save(vo);
				System.out.println("已匯入: " + vo.getExhibitionName());
			}
		}

		System.out.println("===== JSON 匯入完成 =====");
	}

// ===============================================================================================
	
	// 對應 JSON 結構
	static class CultureExhibition {
		private String title;
		private String description;
		private String descriptionFilterHtml; // 新增
		private String startDate;
		private String endDate;
		private ShowInfo[] showInfo;

		// getter/setter
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public String getDescriptionFilterHtml() {
	        return descriptionFilterHtml;
	    }
	    public void setDescriptionFilterHtml(String descriptionFilterHtml) {
	        this.descriptionFilterHtml = descriptionFilterHtml;
	    }

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public ShowInfo[] getShowInfo() {
			return showInfo;
		}

		public void setShowInfo(ShowInfo[] showInfo) {
			this.showInfo = showInfo;
		}
	}

	static class ShowInfo {
		private String location;
		private String longitude;
		private String latitude;

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}
		
		public String getLongitude() {
	        return longitude;
	    }
	    
		public void setLongitude(String longitude) {
	        this.longitude = longitude;
	    }
	    
	    public String getLatitude() {
	        return latitude;
	    }
	    public void setLatitude(String latitude) {
	        this.latitude = latitude;
	    }
	}
}
