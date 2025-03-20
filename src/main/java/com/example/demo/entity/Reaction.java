package com.example.demo.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "reaction")
@NoArgsConstructor
@Setter
@Getter
@Table(name = "reaction")
@Entity
public class Reaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reactionId", nullable = false)
	private Integer reactionId;
	
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 若沒加這個Annotation，在thymeleaf頁面要強制使用日期顯示格式"yyyy-MM-dd HH:mm:ss EEEE"，要加兩層{{}}
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime reactionTime;
	
	private String reactionCategory;
	
	@Column(nullable = false)
	private String reactionEmp;
	
	@Column(nullable = false)
	private String reactionBelongsPostId;
	
	@ManyToOne
	@JoinColumn(name = "reactionBelongsPostId", 
	referencedColumnName = "postId", 
	insertable = false, 
	updatable = false)
	private Post post;

}
