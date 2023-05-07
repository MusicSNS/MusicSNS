package com.co.kr.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlertUtils {
	String message = "";
	String href = "";
	
	public AlertUtils(String message, String href) {
		this.message = message;
		this.href = href;
	}
}
