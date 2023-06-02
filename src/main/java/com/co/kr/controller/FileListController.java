package com.co.kr.controller;

import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.co.kr.service.UploadService;
import com.co.kr.service.UserService;
import com.co.kr.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

import com.co.kr.vo.FileListVO;
import com.co.kr.vo.LoginVO;
import com.co.kr.vo.RequestQuestionVo;
import com.co.kr.vo.ResponseVo;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.CommentListDomain;
import com.co.kr.domain.FollowListDomain;
import com.co.kr.domain.LikeListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.domain.MusicFileDomain;
import com.co.kr.domain.UserFileDomain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.co.kr.service.CommentService;
import com.co.kr.service.GptService;
import com.co.kr.service.ProfileService;

@Slf4j
@Controller
public class FileListController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private CommentService commentService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private UploadService uploadService;

	@RequestMapping(value = "detail", method = RequestMethod.GET)
	public ModelAndView bdSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO,
			@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		map.put("bdSeq", Integer.parseInt(bdSeq));
		map.put("mbId", session.getAttribute("id"));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
		List<CommentListDomain> commentListDomain = commentService.commentList(map);
		int likecount = uploadService.selectlike(map);
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		String statlike = uploadService.selectstatus(map);
		if (statlike == null) {
			mav.addObject("like", "like!");
		} else {
			mav.addObject("like", "unlike!");
		}
		List<MusicFileDomain> musicfileList = uploadService.boardSelectOneMusicFile(map);
		for (MusicFileDomain list : musicfileList) {
			String path = list.getMusicFilePath().replaceAll("\\\\", "/");
			list.setMusicFilePath(path);
		}
		mav.addObject("musicfiles", musicfileList);
		mav.addObject("likecount", likecount);
		mav.addObject("comment", commentListDomain);
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.setViewName("pages/board.html");
		session.setAttribute("files", fileList);
		return mav;
	}

	@PostMapping(value = "upload")
	public ModelAndView bdUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		fileListVO.setName(session.getAttribute("name").toString());
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq, response);
		uploadService.MusicfileProcess(fileListVO, request, httpReq, response, bdSeq);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle("");
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("pages/board.html");
		return mav;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.GET)
	public ModelAndView remove(BoardFileDomain boardFileDomain, MusicFileDomain musicFileDomain, 
			String bdSeq, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		map.put("bdSeq", Integer.parseInt(bdSeq));
		uploadService.bdContentRemove(map);
		uploadService.deletelike(map);
		commentService.commentRemove(map);
		boardFileDomain.setBdSeq(Integer.parseInt(bdSeq));
		musicFileDomain.setBdSeq(Integer.parseInt(bdSeq));
		System.out.println(bdSeq + "번째 게시글 사진 삭제");
		uploadService.bdFileRemove(boardFileDomain);
		uploadService.musicFileRemove(musicFileDomain);
		mav.setViewName("pages/board.html");
		String alertText = "게시글이 삭제되었습니다.";
		String redirectPath = "/main/bdList";
		CommonUtils.redirect(alertText, redirectPath, response);
		return mav;
	}

	@RequestMapping(value = "edit", method = RequestMethod.GET)
	public ModelAndView edit(FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request)
			throws IOException {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		map.put("bdSeq", Integer.parseInt(bdSeq));
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
		List<MusicFileDomain> musicfileList = uploadService.boardSelectOneMusicFile(map);
		List<CommentListDomain> commentListDomain = commentService.commentList(map);
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		for (MusicFileDomain list : musicfileList) {
			String path = list.getMusicFilePath().replaceAll("\\\\", "/");
			list.setMusicFilePath(path);
		}
		int likecount = uploadService.selectlike(map);
		map.put("mbId", session.getAttribute("id"));
		String statlike = uploadService.selectstatus(map);
		if (statlike == null) {
			mav.addObject("like", "like!");
		} else {
			mav.addObject("like", "unlike!");
		}
		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit"); // upload 재활용하기위해서
		mav.addObject("comment", commentListDomain);
		mav.addObject("likecount", likecount);
		mav.addObject("detail", boardListDomain);
		mav.addObject("files", fileList);
		mav.addObject("musicfiles", musicfileList);
		mav.addObject("fileLen", fileList.size());
		mav.addObject("musicfileLen", musicfileList.size());
		mav.setViewName("pages/boardEdit.html");
		return mav;
	}

	@PostMapping(value = "/editSave")
	public ModelAndView editSave(BoardFileDomain boardFileDomain, MusicFileDomain musicFileDomain,
			FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("bdSeq", fileListVO.getSeq().toString());
		uploadService.musicFileOneRemove(map);
		fileListVO.setBcseq(httpReq.getParameter("bcSeq"));
		fileListVO.setTitle(httpReq.getParameter("title"));
		fileListVO.setName(session.getAttribute("name").toString());
		fileListVO.setBccontent(httpReq.getParameter("content"));
		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq, response);
		uploadService.MusicfileProcess(fileListVO, request, httpReq, response, bdSeq);
		System.out.println(bdSeq + "번째 게시물 수정완료");
		uploadService.bdFileRemove(boardFileDomain);
		fileListVO.setContent("");
		fileListVO.setTitle("");
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.setViewName("pages/board.html");
		return mav;
	}

	// Comment line
	@PostMapping(value = "bcupload")
	public ModelAndView bcUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException, ParseException {
		ModelAndView mav = new ModelAndView();
		fileListVO.setBcseq(httpReq.getParameter("bcSeq"));
		fileListVO.setSeq(httpReq.getParameter("bdSeq"));
		fileListVO.setBccontent(httpReq.getParameter("content"));
		HashMap<String, Object> map = new HashMap<>();
		int bdSeq = commentService.fileProcess(fileListVO, httpReq);
		fileListVO.setContent(""); // 초기화
		map.put("bdSeq", bdSeq);
		CommentListDomain commentListDomain = commentService.commentSelectOne(map);
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.addObject("bcitems", commentListDomain);
		String redirectPath = "/main/bdList";
		CommonUtils.redirect("댓글이 추가되었습니다.", redirectPath, response);
		return mav;
	}

	@GetMapping("bcedit")
	public ModelAndView bcedit(FileListVO fileListVO, @RequestParam("bcSeq") String bcSeq,
			@RequestParam("bdSeq") Integer bdSeq, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		map.put("bdSeq", bdSeq);
		BoardListDomain boardListDomain = uploadService.boardSelectOne(map);
		List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
		map.put("bcSeq", bcSeq);
		CommentListDomain commentListDomain = commentService.commentSelectOne(map);
		for (BoardFileDomain list : fileList) {
			String path = list.getUpFilePath().replaceAll("\\\\", "/");
			list.setUpFilePath(path);
		}
		int likecount = uploadService.selectlike(map);
		map.put("mbId", session.getAttribute("id"));
		String statlike = uploadService.selectstatus(map);
		if (statlike == null) {
			mav.addObject("like", "like!");
		} else {
			mav.addObject("like", "unlike!");
		}
		fileListVO.setBcseq(commentListDomain.getBcSeq());
		fileListVO.setBccontent(commentListDomain.getBcContent());
		fileListVO.setSeq(boardListDomain.getBdSeq());
		fileListVO.setContent(boardListDomain.getBdContent());
		fileListVO.setTitle(boardListDomain.getBdTitle());
		fileListVO.setIsEdit("edit"); // upload 재활용하기위해서
		mav.addObject("bcitems", commentListDomain);
		mav.addObject("likecount", likecount);
		mav.addObject("detail", boardListDomain);
		mav.addObject("bcSeq", commentListDomain.getBcSeq());
		mav.addObject("files", fileList);
		mav.addObject("fileLen", fileList.size());
		mav.setViewName("pages/commentEdit.html");
		return mav;
	}

	@PostMapping("bceditSave")
	public ModelAndView bceditSave(@ModelAttribute("fileListVO") FileListVO fileListVO,
			MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		int bdSeq = commentService.fileProcess(fileListVO, httpReq);
		HashMap<String, Object> map = new HashMap<>();
		map.put("bdSeq", bdSeq);
		List<CommentListDomain> commentList = commentService.commentList(map);
		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq), request);
		mav.addObject("comment", commentList);
		fileListVO.setContent(""); // 초기화
		fileListVO.setTitle(""); // 초기화
		mav.setViewName("pages/board.html");
		return mav;
	}

	@GetMapping("bcremove")
	public ModelAndView bcremove(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		map.put("bdSeq", request.getParameter("bdSeq"));
		map.put("bcSeq", request.getParameter("bcSeq"));
		commentService.commentRemove(map);
		String redirectPath = "/main/bdList";
		CommonUtils.redirect("댓글이 삭제되었습니다.", redirectPath, response);
		return mav;
	}

	@RequestMapping(value = "/profileEdit")
	public ModelAndView profileEdit(FileListVO fileListVO, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		HashMap<String, Object> mb = new HashMap<String, Object>();
		mb.put("mbName", session.getAttribute("name").toString());
		int followsize = profileService.selectcountfollow(mb);
		int followingsize = profileService.selectcountfollowing(mb);
		List<BoardListDomain> items = uploadService.authorBoardList(mb);
		int size = uploadService.authorBoard(mb);
		for (BoardListDomain item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bdSeq", Integer.parseInt(item.getBdSeq()));
			List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
			for (BoardFileDomain list : fileList) {
				String path = list.getUpNewFileName().replaceAll("\\\\", "/");
				list.setUpFilePath(path);
			}
			item.setFiles(fileList);
		}
		UserFileDomain profile = profileService.mbSelectOneFile(mb);
		if (profile != null) {
			String profilePath = profile.getUpNewFileName().replace("\\\\", "/");
			profile.setUpFilePath(profilePath);
			mav.addObject("profile", profile.getUpFilePath());
		}
		fileListVO.setMcontent(session.getAttribute("comment").toString());
		fileListVO.setMname(session.getAttribute("name").toString());
		mav.addObject("followsize", followsize);
		mav.addObject("followingsize", followingsize);
		mav.addObject("items", items);
		mav.addObject("itemSize", size);
		mav.addObject("mbSeq", session.getAttribute("mbSeq"));
		mav.setViewName("profile/authorEdit.html");
		return mav;
	}

	@RequestMapping(value = "profileEditSave", method = RequestMethod.POST)
	public ModelAndView profileEditSave(UserFileDomain userFileDomain, FileListVO fileListVO,
			MultipartHttpServletRequest request, HttpServletRequest httpReq, HttpServletResponse response)
			throws IOException, ParseException {
		HttpSession session = request.getSession();
		ModelAndView mav = new ModelAndView();
		String mbSeq = session.getAttribute("mbSeq").toString();
		fileListVO.setMbseq(mbSeq);
		fileListVO.setMname(httpReq.getParameter("mname"));
		fileListVO.setMcontent(httpReq.getParameter("mcontent"));
		session.setAttribute("files", httpReq.getParameter("file"));
		fileListVO.setIsEdit("Y");
		String mbName = profileService.fileProcess(fileListVO, request, httpReq, response);
		System.out.println(mbName + "님의 프로필을 업데이트 하였습니다.");
		session.setAttribute("comment", httpReq.getParameter("mcontent"));
		List<BoardListDomain> items = uploadService.boardList();
		HashMap<String, Object> mb = new HashMap<String, Object>();
		mb.put("mbName", session.getAttribute("name").toString());
		int size = uploadService.authorBoard(mb);
		for (BoardListDomain item : items) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bdSeq", Integer.parseInt(item.getBdSeq()));
			List<BoardFileDomain> fileList = uploadService.boardSelectOneFile(map);
			for (BoardFileDomain list : fileList) {
				String path = list.getUpNewFileName().replaceAll("\\\\", "/");
				list.setUpFilePath(path);
			}
			item.setFiles(fileList);
		}
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("oldName", session.getAttribute("name").toString());
		session.setAttribute("name", httpReq.getParameter("mname"));
		param.put("newName", session.getAttribute("name").toString());
		param.put("newComment", httpReq.getParameter("mcontent"));
		mav.addObject("items", items);
		mav.addObject("itemSize", size);
		userFileDomain.setMbId(session.getAttribute("id").toString());
		profileService.mbFileRemove(userFileDomain);
		mav.setViewName("profile/author");
		String redirectPath = "author";
		CommonUtils.redirect("프로필이 변경되었습니다.", redirectPath, response);
		profileService.changeName(param);
		userService.userboardedit(param);
		userService.usercommentedit(param);
		userService.userfilesedit(param);
		userService.userlikeedit(param);
		userService.usermbfilesedit(param);
		userService.userfollowedit(param);
		userService.userfollowingedit(param);
		return mav;
	}

	@RequestMapping(value = "like")
	public void like(FileListVO fileListVO, LoginVO loginVO, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HashMap<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		String bdSeq = fileListVO.getSeq();
		String mbId = session.getAttribute("id").toString();
		map.put("bdSeq", bdSeq);
		map.put("mbId", mbId);
		Integer duplecheck = uploadService.duplicatelike(map);
		bdSelectOneCall(fileListVO, bdSeq, request);
		if (duplecheck == 0) {
			uploadService.insertlike(map);
			map.put("status", "unlike!");
			uploadService.updatelike(map);
			String redirectPath = "detail" + "?bdSeq=" + bdSeq;
			CommonUtils.redirect("마음에 들었군요!", redirectPath, response);
		} else {
			uploadService.deletelikeone(map);
			map.put("status", "like!");
			uploadService.updatelike(map);
			String redirectPath = "detail" + "?bdSeq=" + bdSeq;
			CommonUtils.redirect("마음에 들지 않으셧나요?", redirectPath, response);
		}
	}

	@RequestMapping("likesearch")
	public ModelAndView LikeSearch(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		map.put("bdSeq", request.getParameter("bdSeq"));
		List<LikeListDomain> likeListDomains = uploadService.selectlikemember(map);
		mav.addObject("likeitems", likeListDomains);
		mav.setViewName("board/like");
		return mav;
	}

	@RequestMapping(value = "follow")
	public ModelAndView Follow(HttpServletRequest request, @RequestParam("flmbName") String flmbName,
			HttpServletResponse response) throws IOException {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		map.put("mbId", session.getAttribute("id"));
		map.put("mbName", session.getAttribute("name"));
		map.put("mbComment", session.getAttribute("comment"));
		map.put("flmbName", flmbName);
		List<LoginDomain> loginDomains = uploadService.searchfollowmember(map);
		for (LoginDomain loginDomain : loginDomains) {
			map.put("flmbId", loginDomain.getMbId());
			map.put("flmbComment", loginDomain.getMbComment());
		}
		Integer duplecheck = profileService.duplicatefollow(map);
		if (duplecheck == 0) {
			profileService.follow(map);
			String redirectPath = "profile" + "?mbName=" + flmbName;
			CommonUtils.redirect(flmbName+"님 팔로우!", redirectPath, response);
		} else {
			profileService.deletefollowone(map);
			String redirectPath = "profile" + "?mbName=" + flmbName;
			CommonUtils.redirect(flmbName+"님 팔로우 취소!", redirectPath, response);
		}
		mav.addObject("duplecheck", duplecheck);
		return mav;
	}

	@RequestMapping(value = "followlist")
	public ModelAndView FollowList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		map.put("mbName", request.getParameter("mbName"));
		List<FollowListDomain> followListDomains = profileService.selectfollow(map);
		mav.addObject("followitems", followListDomains);
		mav.setViewName("profile/followlist");
		return mav;
	}

	@RequestMapping(value = "followinglist")
	public ModelAndView FollowingList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		map.put("mbName", request.getParameter("mbName"));
		List<FollowListDomain> followingListDomains = profileService.selectfollowing(map);
		mav.addObject("followingitems", followingListDomains);
		mav.setViewName("profile/followinglist");
		return mav;
	}

	@Autowired
	GptService gptService;

	@RequestMapping(value = "gptAPI/make/conversation")
	public String gptres(@Valid RequestQuestionVo requestQuestionVo, HttpServletRequest request) {
		// content를 추출하여 변수에 할당
		String result = gptService.getConversation(requestQuestionVo).getResponse();
		return result;
	}

	@RequestMapping("gptresult")
	public ModelAndView GptResult(@Valid RequestQuestionVo requestQuestionVo, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String gptquestion = request.getParameter("question");
		requestQuestionVo.setQuestion(gptquestion + " 분위기나 장르의 음악 추천해줄래?");
		String result = gptres(requestQuestionVo, request);
		mav.addObject("recommendresult", result);
		mav.setViewName("pages/gpt");
		return mav;
	}

	@RequestMapping("gptview")
	public ModelAndView GptView() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("pages/gpt");
		return mav;
	}
}