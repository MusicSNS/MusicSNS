package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.co.kr.domain.LoginDomain;
import com.co.kr.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public void mbCreate(LoginDomain loginDomain) {
		userMapper.mbCreate(loginDomain);
	}

	@Override
	public LoginDomain mbSelectList(Map<String, String> map) {
		return userMapper.mbSelectList(map);
	}

	@Override
	public void mbUpdate(LoginDomain loginDomain) {
		userMapper.mbUpdate(loginDomain);
	}

	@Override
	public void mbRemove(Map<String, String> map) {
		userMapper.mbRemove(map);
	}

	@Override
	public void mbLevelUpdate(LoginDomain loginDomain) {
		userMapper.mbLevelUpdate(loginDomain);
	}

	@Override
	public LoginDomain mbGetId(Map<String, String> map) {
		return userMapper.mbGetId(map);
	}

	@Override
	public int mbDuplicationCheck(Map<String, String> map) {
		return userMapper.mbDuplicationCheck(map);
	}

	@Override
	public int mbLoginCheck(Map<String, String> map) {
		return userMapper.mbLoginCheck(map);
	}

	@Override
	public int mbGetAll() {
		// TODO Auto-generated method stub
		return userMapper.mbGetAll();
	}

	@Override
	public List<LoginDomain> searchMemberById(Map<String, String> map) {
		return userMapper.searchMemberById(map);
	}

	@Override
	public List<LoginDomain> mbAllList() {
		return userMapper.mbAllList();
	}
	// UserServiceImpl.java

	@Override
	public void userboardedit(HashMap<String, String> map) {
		userMapper.userboardedit(map);
	};

	@Override
	public void usercommentedit(HashMap<String, String> map) {
		userMapper.usercommentedit(map);
	};

	@Override
	public void userfilesedit(HashMap<String, String> map) {
		userMapper.userfilesedit(map);
	};

	@Override
	public void usermbfilesedit(HashMap<String, String> map) {
		userMapper.usermbfilesedit(map);
	};

	@Override
	public void userlikeedit(HashMap<String, String> map) {
		userMapper.userlikeedit(map);
	};

	@Override
	public void userfollowedit(HashMap<String, String> map) {
		userMapper.userfollowedit(map);
	};

	@Override
	public void userfollowingedit(HashMap<String, String> map) {
		userMapper.userfollowingedit(map);
	};
}