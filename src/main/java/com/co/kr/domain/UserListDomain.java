package com.co.kr.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderMethodName="builder")
public class UserListDomain {

	private String mbSeq;
	private String mbId;
	private String mbName;
	private String mbContent;
}