//	---------------------前台----------------------
//	---------------------後台----------------------
//	---------------------分隔線----------------------
package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Post;

import jakarta.transaction.Transactional;


/*
 * Spring框架會幫我們進行實作API:Class"SimpleJpaRepository<T,ID>"(@Repository、@Transactional(readOnly=true))
 * @Transactional(readOnly=true)如果要使用insert、update、delete都要在你的Repository.java該方法上@Transactional來蓋掉readonly部分
 */
public interface PostRepository extends JpaRepository<Post, String> {
	
//	---------------------前台----------------------
//	 貼文牆關鍵字查詢
	 @Query("SELECT p FROM Post p WHERE " +
			 "(:keyword1 IS NULL OR p.postTitle LIKE %:keyword1% OR p.postEmp LIKE %:keyword1% OR p.postEmp IN (SELECT e.empno FROM Employees e WHERE e.name LIKE %:keyword1%)) "
			 + "AND (:keyword2 IS NULL OR p.postCategory LIKE %:keyword2%) "
	         + "ORDER BY p.postDate DESC")
	    List<Post> findPostsByKeywords(
	            @Param("keyword1") String keyword1,
	            @Param("keyword2") String keyword2
	            );
	 
//	瀏覽"特定發文者"、"特定貼文分類"的所有文章
	@Query("SELECT p FROM Post p WHERE p.postEmp = :postEmp AND p.postCategory = :postCategory ORDER BY p.postDate DESC")
	List<Post> findAllPostsByPostEmpAndPostCategory(@Param("postEmp") String postEmp, @Param("postCategory") String postCategory);
	
//	瀏覽"特定發文者"的所有文章
	@Query("SELECT p FROM Post p WHERE p.postEmp = :postEmp ORDER BY p.postDate DESC")
	List<Post> findAllPostsByPostEmp(@Param("postEmp") String postEmp);
	
//	---------------------後台----------------------
//	瀏覽"特定貼文分類"的所有文章
	@Query("SELECT p FROM Post p WHERE p.postCategory = :postCategory ORDER BY p.postDate DESC")
	List<Post> findAllPostsByPostCategory(@Param("postCategory") String postCategory);
	
	 
//	---------------------分隔線----------------------
	
	
//	貼文瀏覽權限開啟
	@Transactional
	@Modifying
	@Query("UPDATE Post p SET p.postStatus = true WHERE p.postId = :postId")
	void browsePost(@Param("postId") String postId);
	
//	貼文瀏覽權限關閉(假刪除貼文)
	@Transactional
	@Modifying
	@Query("UPDATE Post p SET p.postStatus = false WHERE p.postId = :postId")
	void softDeletePost(@Param("postId") String postId);

//	瀏覽貼文(過濾假刪除的貼文)(照時間新-->舊排序)
	@Query("SELECT p FROM Post p WHERE p.postStatus = true")
	List<Post> findAllActivePosts(Sort sort);
	
//	瀏覽所有貼文(過濾假刪除的貼文)(每頁顯示固定數量貼文)(限定'活動','考勤','薪資','福利')
	@Query("SELECT p FROM Post p WHERE p.postStatus = true AND p.postCategory IN ('活動','考勤','薪資','福利')")
	Page<Post> findAllActivePosts(Pageable pageable);
	

	
	
	
	
	
	
	
//	模糊查詢(有條件查詢)
	 @Query("SELECT p FROM Post p WHERE (:postId IS NULL OR p.postId = :postId) "
	 		+ "AND (:postEmp IS NULL OR p.postEmp LIKE %:postEmp%) "
	 		+ "AND (:postStatus IS NULL OR p.postStatus = :postStatus) "
	 		+ "AND (:postCategory IS NULL OR p.postCategory = :postCategory) "
	 		+ "AND (:postTitle IS NULL OR p.postTitle LIKE %:postTitle%)")
	    List<Post> findPostByMultiParam(
	    		@Param("postId") String postId, 
	    		@Param("postEmp") String postEmp, 
	    		@Param("postStatus") Boolean postStatus, 
	    		@Param("postCategory") String postCategory, 
	    		@Param("postTitle") String postTitle);




}
