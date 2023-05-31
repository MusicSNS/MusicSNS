package com.co.kr.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class CommentContentDomain {
	private String bcSeq;
	private String bdSeq;
	private String mbId;
	private String mbName;
	private String bcContent;
}
