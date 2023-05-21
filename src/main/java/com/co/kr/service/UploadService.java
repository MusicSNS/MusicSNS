package com.co.kr.service;

import java.io.IOException;
import java.util.HashMap;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.vo.FileListVO;

public interface UploadService {

	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException;

	// all list
	public List<BoardListDomain> boardList();

	// 하나 삭제
	public void bdContentRemove(HashMap<String, Object> map);

	// 전체 삭제
	public void bdContentAllRemove(HashMap<String, String> map);

	// 하나 삭제
	public void bdFileRemove(BoardFileDomain boardFileDomain);

	// 전체 삭제
	public void bdFileAllRemove(HashMap<String, String> map);

	// select one
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);

	// content update
	public void bdContentUpdate(BoardContentDomain boardContentDomain);

	// file update
	public void bdFileUpdate(BoardFileDomain boardFileDomain);

	// author board
	public int authorBoard(HashMap<String, Object> map);

	// search content by title
	public List<BoardListDomain> searchBoardByTitle(HashMap<String, String> map);

	// select one file
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);

	public List<BoardListDomain> authorBoardList(HashMap<String, Object> map);

	//like
	public void insertlike(HashMap<String, Object> map);

	public int selectlike(HashMap<String, Object> map);

	public void deletelike(HashMap<String, Object> map);

	public void deletelikeuser(HashMap<String, Object> map);
	
	public int duplicatelike(HashMap<String, Object> map);
	
	public void deletelikeone(HashMap<String, Object>map); 
	
	public void updatelike(HashMap<String, Object> map);
	
	public String selectstatus(HashMap<String, Object> map);
}