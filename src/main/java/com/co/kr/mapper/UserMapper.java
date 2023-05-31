package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.LoginDomain;

@Mapper
public interface UserMapper {

    //멤버 리스트
    public List<LoginDomain> mbAllList();
    
	//하나 리스트 조회
    public LoginDomain mbSelectList(Map<String, String> map);
        
    // 전체갯수
    public int mbGetAll();

    //신규 저장
    public void mbCreate(LoginDomain loginDomain);
    
    //id 정보 가져오기
    public LoginDomain mbGetId(Map<String, String> map);
    
    //중복체크
    public int mbDuplicationCheck(Map<String, String> map);
    
    //로그인
    public int mbLoginCheck(Map<String, String> map);
    
    //업데이트
    public void mbUpdate(LoginDomain loginDomain);
    
    //삭제
    public void mbRemove(Map<String, String> map);
    
    //등급 업데이트하기
    public void mbLevelUpdate(LoginDomain loginDomain);
    
    //아이디로 검색하기
    public List<LoginDomain> searchMemberById(Map<String, String> map);
    
  //UserMapper.java, UserService.java

  		public void userboardedit(HashMap<String, String> map);
  	    
      public void usercommentedit(HashMap<String, String> map);
      
      public void userfilesedit(HashMap<String, String> map);
      
      public void usermbfilesedit(HashMap<String, String> map);
      
      public void userlikeedit(HashMap<String, String> map);
      
      public void userfollowedit(HashMap<String, String> map);
      
      public void userfollowingedit(HashMap<String, String> map);
}

