package com.late.donot.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.late.donot.board.model.dto.Board;
import com.late.donot.board.model.mapper.BoardMapper;

@Service
@Transactional(rollbackFor = Exception.class)
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;

	@Override
	public List<Board> selectNoticeList(int cp, int limit,String query) {
		return mapper.selectNoticeList(cp,limit,query);
	}

	@Override
	public int getListCount(String query) {
		return mapper.getListCount(query);
	}
	

}
