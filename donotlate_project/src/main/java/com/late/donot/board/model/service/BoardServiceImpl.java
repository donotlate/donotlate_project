package com.late.donot.board.model.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		
		int offset = (cp - 1) * limit;
		
		Map<String, Object> map = new HashMap<>();
	    map.put("limit", limit);
	    map.put("offset", offset);
	    map.put("query", query);
		
		return mapper.selectNoticeList(map);
	}

	@Override
	public int getListCount(String query) {
		return mapper.getListCount(query);
	}

	@Override
	public Board selectNoticeDetail(int boardNo) {

		return mapper.selectNoticeDetail(boardNo);
	}
	

}
