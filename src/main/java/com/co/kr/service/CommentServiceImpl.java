package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.co.kr.domain.CommentContentDomain;
import com.co.kr.domain.CommentListDomain;
import com.co.kr.mapper.CommentMapper;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentMapper commentMapper;

	@Override
	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq) {
		// session 생성
		HttpSession session = httpReq.getSession();

		// content domain 생성
		CommentContentDomain commentContentDomain = CommentContentDomain.builder().bdSeq(httpReq.getParameter("bdSeq"))
				.bcSeq(httpReq.getParameter("bcSeq")).mbId(session.getAttribute("id").toString())
				.mbName(session.getAttribute("name").toString()).bcContent(fileListVO.getBccontent()).build();

		if (fileListVO.getIsEdit() != null) {
			commentContentDomain.setBcContent(httpReq.getParameter("content"));
			System.out.println("댓글 수정 업데이트");
			// db 업데이트
			commentMapper.commentUpdate(commentContentDomain);
		} else {
			// db 인서트
			commentContentDomain.setBcContent(httpReq.getParameter("content"));
			commentMapper.commentUpload(commentContentDomain);
			System.out.println("댓글 db 인서트");
		}

		int bdSeq = Integer.parseInt(commentContentDomain.getBdSeq());

		return bdSeq; // 저장된 게시판 번호
	}

	// comment list
	public List<CommentListDomain> commentList(HashMap<String, Object> map) {
		return commentMapper.commentList(map);
	}

	// comment upload
	public void commentUpload(CommentContentDomain commentContentDomain) {
		commentMapper.commentUpload(commentContentDomain);
	}

	// comment update
	public void commentUpdate(CommentContentDomain commentContentDomain) {
		commentMapper.commentUpdate(commentContentDomain);
	}

	// comment delete
	public void commentRemove(HashMap<String, Object> map) {
		commentMapper.commentRemove(map);
	}

	// All Comment delete
	public void commentAllRemove(HashMap<String, String> map) {
		commentMapper.commentAllRemove(map);
	}

	// select comment one
	public CommentListDomain commentSelectOne(HashMap<String, Object> map) {
		return commentMapper.commentSelectOne(map);
	}
}
