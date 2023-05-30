package com.co.kr.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class CommentListDomain {
	private String bcSeq;
	private String bdSeq;
	private String mbId;
	private String mbName;
	private String bcContent;
	private String bcCreateAt;
	private String bcUpdateAt;
}
