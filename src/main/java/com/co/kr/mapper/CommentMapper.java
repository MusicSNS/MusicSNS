package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.CommentListDomain;
import com.co.kr.domain.CommentContentDomain;
import java.util.HashMap;

@Mapper
public interface CommentMapper {

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
