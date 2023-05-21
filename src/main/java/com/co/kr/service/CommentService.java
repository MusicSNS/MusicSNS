package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.co.kr.domain.CommentContentDomain;
import com.co.kr.domain.CommentListDomain;
import com.co.kr.vo.FileListVO;

public interface CommentService {

	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq);
	//comment list
	public List<CommentListDomain> commentList(HashMap<String, Object>map);
	
	//comment upload
	public void commentUpload(CommentContentDomain commentContentDomain);
	
	//comment update
	public void commentUpdate(CommentContentDomain commentContentDomain);

	//comment delete 
	public void commentRemove(HashMap<String, Object> map);
	
	//All Comment delete
	public void commentAllRemove(HashMap<String, String>map);
	
	//select comment one
	public CommentListDomain commentSelectOne(HashMap<String, Object> map);
}
