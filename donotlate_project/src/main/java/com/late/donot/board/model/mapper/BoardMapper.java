package com.late.donot.board.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.late.donot.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	List<Board> Notices();

	void createBoard(Board inputBoard);

	void removeNotice(int boardNo);

	void editBoard(Board board);



}
