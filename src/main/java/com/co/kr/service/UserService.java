package com.co.kr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.co.kr.domain.LoginDomain;

public interface UserService {
	// selectId
	public LoginDomain mbSelectList(Map<String, String> map);

	// selectAll Conut
	public int mbGetAll();

	// 신규
	public void mbCreate(LoginDomain loginDomain);

	// getMbIdCheck
	public LoginDomain mbGetId(Map<String, String> map);

	// mbDuplicationCheck
	public int mbDuplicationCheck(Map<String, String> map);

	// login
	public int mbLoginCheck(Map<String, String> map);

	// update
	public void mbUpdate(LoginDomain loginDomain);

	// delete
	public void mbRemove(Map<String, String> map);

	// 등급 업데이트하기
	public void mbLevelUpdate(LoginDomain loginDomain);

	// 멤버 id로 찾기
	public List<LoginDomain> searchMemberById(Map<String, String> map);

	public List<LoginDomain> mbAllList();

	// UserMapper.java, UserService.java

	public void userboardedit(HashMap<String, String> map);

	public void usercommentedit(HashMap<String, String> map);

	public void userfilesedit(HashMap<String, String> map);

	public void usermbfilesedit(HashMap<String, String> map);

	public void userlikeedit(HashMap<String, String> map);

	public void userfollowedit(HashMap<String, String> map);

	public void userfollowingedit(HashMap<String, String> map);
}