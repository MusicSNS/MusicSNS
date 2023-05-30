package com.co.kr.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class BoardListDomain {

	private String bdSeq;
	private String mbId;
	private String mbName;
	private String bdTitle;
	private String bdContent;
	private String bdCreateAt;
	private String bdUpdateAt;
	private List<BoardFileDomain> files;
	private List<CommentListDomain> comment;
	private String like;
	private int likecount;
}