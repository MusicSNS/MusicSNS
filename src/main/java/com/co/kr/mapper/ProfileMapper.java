package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.FollowListDomain;
import com.co.kr.domain.UserContentDomain;
import com.co.kr.domain.UserFileDomain;
import com.co.kr.domain.UserListDomain;

@Mapper
public interface ProfileMapper {
	public UserFileDomain mbSelectOneFile(HashMap<String, Object> map);

	public void mbFileUpload(UserFileDomain userFileDomain);

	public int mbFileUpdateCheck(UserFileDomain userFileDomain);

	public void mbFileUpdate(UserFileDomain userFileDomain);

	public void mbFileRemove(UserFileDomain userFileDomain);

	public void mbContentUpdate(UserContentDomain userContentDomain);

	public void mbContentRemove(HashMap<String, Object> map);

	public UserListDomain userSelectOne(HashMap<String, Object> map);

	public String userSelectName(HashMap<String, Object> map);

	public void changeName(HashMap<String, String> map);

	public void follow(HashMap<String, Object> map);
	
	public List<FollowListDomain> selectfollow(HashMap<String, Object> map);
	
	public int selectcountfollow(HashMap<String, Object> map);
	
	public List<FollowListDomain> selectfollowing (HashMap<String, Object> map);
	
	public int selectcountfollowing (HashMap<String, Object> map);
	
	public int duplicatefollow (HashMap<String, Object> map);
	
	public void deletefollowone (HashMap<String, Object> map);
}
