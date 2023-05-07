package com.co.kr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping(value = "/")
public class UserController {

	// 진입점
	@GetMapping("/")
	public String index() {
		return "index.html";
	}

	// about
	@GetMapping("/about")
	public String about() {
		return "pages/about-us.html";
	}

	// signin
	@GetMapping("/signin")
	public String signin() {
		return "pages/sign-in.html";
	}

	// Author
	@GetMapping("/author")
	public String author() {
		return "pages/author.html";
	}

	// contact
	@GetMapping("/contact")
	public String contact() {
		return "pages/contact-us.html";
	}

	// contact
	@GetMapping("/signup")
	public String signup() {
		return "pages/sign-up.html";
	}
}