package com.late.donot.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private int boardViewCount;
	private String boardWriteDate;
	private String boardDelFl;
	private String thumbnailUrl;


	
	private int memberNo;
	private String memberName;
	
}
