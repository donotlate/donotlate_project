package com.late.donot.board.model.service;

import java.util.List;

import com.late.donot.board.model.dto.Board;


public interface BoardService {

	List<Board> Notices();

	List<Board> createBoard(Board inputBoard);

	List<Board> removeNotice(int boardNo);

	List<Board> editBoard(Board board);

	
	
}
