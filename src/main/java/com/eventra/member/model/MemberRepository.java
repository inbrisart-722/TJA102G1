package com.eventra.member.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

//  SRP：僅定義 MemberVO 的持久化查詢介面（不含商業規則）。
//  DIP：上層依賴此抽象介面，實作由 Spring Data 於執行期自動產生。


  

@Repository
public interface MemberRepository extends JpaRepository<MemberVO, Integer>, JpaSpecificationExecutor<MemberVO> {

	/** 依 email 取得單一會員（唯一鍵）。 */
	Optional<MemberVO> findByEmail(String email);

	/** 檢查 email 是否已存在（唯一性預檢）。 */
	boolean existsByEmail(String email);

	Optional<MemberVO> findByNickname(String trim);
	boolean existsByNickname(String trimmedNickname);
	
//	//● (自訂)條件查詢
//	@Query(value = "from MemberVO where memberId=?1 and fullName like ? 2 and hiredate=?3 order by memberId")
//	List<MemberVO> findByOthers(int memberId , String fullName , java.sql.Date hiredate);

	

	
	
	
}
