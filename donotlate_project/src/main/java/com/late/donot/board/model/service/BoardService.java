package com.late.donot.board.model.service;

import java.util.List;

import com.late.donot.board.model.dto.Board;


public interface BoardService {

	List<Board> selectNoticeList(int cp, int limit);

	int getListCount();



	
	
}
