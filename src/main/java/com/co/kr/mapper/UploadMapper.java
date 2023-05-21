package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import java.util.HashMap;

@Mapper
public interface UploadMapper {

	//list
	public List<BoardListDomain> boardList();
	
	//content upload
	public void contentUpload(BoardContentDomain boardContentDomain);
	
	//file upload
	public void fileUpload(BoardFileDomain boardFileDomain);

	//content update
	public void bdContentUpdate(BoardContentDomain boardContentDomain);

	//file updata
	public void bdFileUpdate(BoardFileDomain boardFileDomain);

	//content delete 
	public void bdContentRemove(HashMap<String, Object> map);
	//All Content delete
	public void bdContentAllRemove(HashMap<String, String>map);
	
	//file delete 
	public void bdFileRemove(BoardFileDomain boardFileDomain);
	
	//All File delete
	public void bdFileAllRemove(HashMap<String, String> map);
	
	//select one
	public BoardListDomain boardSelectOne(HashMap<String, Object> map);

	//select one file
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map);
	
	//search board by title
	public List<BoardListDomain> searchBoardByTitle(HashMap<String, String> map);
	
	//author board
	public int authorBoard(HashMap<String, Object> map);
	
	//select mbName board
	public List<BoardListDomain> authorBoardList(HashMap<String, Object> map);
}
