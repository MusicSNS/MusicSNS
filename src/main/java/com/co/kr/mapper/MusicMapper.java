package com.co.kr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.MusicFileDomain;
import java.util.HashMap;

@Mapper
public interface MusicMapper {

	// file upload
	public void musicFileUpload(MusicFileDomain musicFileDomain);

	// file updata
	public void musicFileUpdate(MusicFileDomain musicFileDomain);

	// file delete
	public void musicFileRemove(MusicFileDomain musicFileDomain);

	// file delete
	public void musicFileOneRemove(HashMap<String, String>map);

	// All File delete
	public void musicFileAllRemove(HashMap<String, String> map);

	// select one file
	public List<MusicFileDomain> boardSelectOneMusicFile(HashMap<String, Object> map);
}
