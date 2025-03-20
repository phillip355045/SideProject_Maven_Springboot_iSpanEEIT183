package com.example.demo.dto;

import com.example.demo.entity.Reaction;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "reactionResponseDto")
public class ReactionResponseDto {

	  private Reaction reaction;
	  private int likeCounts;
	  
}


