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
import com.co.kr.domain.UserContentDomain;
import com.co.kr.domain.UserFileDomain;
import com.co.kr.domain.UserListDomain;
import com.co.kr.vo.FileListVO;

public interface ProfileService {

	public String fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, 
			HttpServletRequest httpReq, HttpServletResponse response) throws IOException;
	public UserFileDomain mbSelectOneFile(HashMap<String, Object> map);
	public void mbFileUpload(UserFileDomain userFileDomain);
	public int mbFileUpdateCheck(UserFileDomain userFileDomain);
	public void mbFileUpdate(UserFileDomain userFileDomain);
	public void mbFileRemove(UserFileDomain userFileDomain);
	public void mbContentUpdate(UserContentDomain userContentDomain);
	public void mbContentRemove(HashMap<String, Object> map);
	public String userSelectName(HashMap<String, Object> map);
	public void changeName(HashMap<String, String> map);
	//public UserListDomain userSelectOne(HashMap<String, Object> map);
	//public List<BoardListDomain> boardList();
}
