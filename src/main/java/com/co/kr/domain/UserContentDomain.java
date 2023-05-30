package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class UserContentDomain {
	private String mbSeq;
	private String mbId;
	private String mbName;
	private String mbContent;
}