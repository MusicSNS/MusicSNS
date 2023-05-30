package com.co.kr.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class FollowListDomain {
	private String mbName;
	private String mbId;
	private String mbComment;
	private String flmbName;
	private String flmbId;
	private String flmbComment;
}