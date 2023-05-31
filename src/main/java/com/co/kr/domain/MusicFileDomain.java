package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class MusicFileDomain {
	private Integer bdSeq;
	private String mbId;
	private String mbName;
	private String musicOriginalFileName;
	private String musicNewFileName; //동일 이름 업로드 될 경우
	private String musicFilePath;
	private Integer musicFileSize;
}
