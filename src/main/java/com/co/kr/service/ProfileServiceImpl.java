package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.UserContentDomain;
import com.co.kr.domain.UserFileDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.ProfileMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	ProfileMapper profileMapper;

	@Override
	public String fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException {
		// session 생성
		HttpSession session = httpReq.getSession();
		// content domain 생성
		UserContentDomain userContentDomain = UserContentDomain.builder().mbId(session.getAttribute("id").toString())
				.mbName(fileListVO.getMname()).mbContent(fileListVO.getMcontent()).mbSeq(fileListVO.getMbseq()).build();
		if (fileListVO.getIsEdit() != null) {
			userContentDomain.setMbSeq(fileListVO.getMbseq());
			System.out.println("수정업데이트");
			// db 업데이트
			profileMapper.mbContentUpdate(userContentDomain);
		} else {
			// db 인서트
			profileMapper.mbContentUpdate(userContentDomain);
			System.out.println("db 인서트");
		}

		// file 데이터 db 저장시 쓰일 값 추출
		int mbSeq = Integer.parseInt(userContentDomain.getMbSeq());
		String mbId = userContentDomain.getMbId();
		String mbName = userContentDomain.getMbName();

		// 파일객체 담음
		List<MultipartFile> multipartFiles = request.getFiles("files");

		// 게시글 수정시 파일관련 물리저장 파일, db 데이터 삭제
		if (fileListVO.getIsEdit() != null) { // 수정시

			List<UserFileDomain> fileList = null;

			for (MultipartFile multipartFile : multipartFiles) {

				if (!multipartFile.isEmpty()) { // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기

					if (session.getAttribute("files") != null) {

						fileList = (List<UserFileDomain>) session.getAttribute("files");

						for (UserFileDomain list : fileList) {
							list.getUpFilePath();
							Path filePath = Paths.get(list.getUpFilePath());

							try {

								// 파일 삭제
								Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
								// 삭제
								mbFileRemove(list); // 데이터 삭제

							} catch (DirectoryNotEmptyException e) {
								throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}

				}

			}

		}

		///////////////////////////// 새로운 파일 저장 ///////////////////////

		// 저장 root 경로만들기
		Path rootPath = Paths.get(new File("C://").toString(), "profile", File.separator).toAbsolutePath().normalize();
		File pathCheck = new File(rootPath.toString());

		// folder chcek
		if (!pathCheck.exists())
			pathCheck.mkdirs();

		if (multipartFiles.size() > 1) {
			CommonUtils.redirect("파일은 하나만 선택 가능합니다.", "author", response);
		}
		for (MultipartFile multipartFile : multipartFiles) {
			if (!multipartFile.isEmpty()) { // 파일 있을때

				// 확장자 추출
				String originalFileExtension;
				String contentType = multipartFile.getContentType();
				String origFilename = multipartFile.getOriginalFilename();
				// 확장자 조재안을경우
				if (ObjectUtils.isEmpty(contentType)) {
					break;
				} else { // 확장자가 jpeg, png인 파일들만 받아서 처리
					if (contentType.contains("image/jpeg")) {
						originalFileExtension = ".jpg";
					} else if (contentType.contains("image/png")) {
						originalFileExtension = ".png";
					} else {
						break;
					}
				}

				// 파일명을 업로드한 날짜로 변환하여 저장
				String uuid = UUID.randomUUID().toString();
				String current = CommonUtils.currentTime();
				String newFileName = uuid + current + originalFileExtension;

				// 최종경로까지 지정
				Path targetPath = rootPath.resolve(newFileName);

				File file = new File(targetPath.toString());

				try {
					// 파일복사저장
					multipartFile.transferTo(file);
					// 파일 권한 설정(쓰기, 읽기)
					file.setWritable(true);
					file.setReadable(true);

					// 파일 domain 생성
					UserFileDomain userFileDomain = UserFileDomain.builder().mbSeq(mbSeq).mbId(mbId).mbName(mbName)
							.upOriginalFileName(origFilename).upNewFileName("resources/profile/" + newFileName) // WebConfig에
																												// 동적
																												// 이미지
																												// 폴더 생성
																												// 했기때문
							.upFilePath(targetPath.toString()).upFileSize((int) multipartFile.getSize()).build();
					System.out.println(userFileDomain);
					int affectedRows = profileMapper.mbFileUpdateCheck(userFileDomain);

					if (affectedRows > 0) {
						profileMapper.mbFileUpdate(userFileDomain);
						System.out.println("Update done");
					} else {
						// DB가 변경되지 않았을 때 처리할 코드 작성
						profileMapper.mbFileUpload(userFileDomain);
						System.out.println("Upload done");
						// 다른 코드 실행
					}
				} catch (IOException e) {
					throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
				}
			}
		}

		return mbName; // 저장된 게시판 번호
	}

	@Override
	public UserFileDomain mbSelectOneFile(HashMap<String, Object> map) {
		return profileMapper.mbSelectOneFile(map);
	}

	@Override
	public void mbFileUpload(UserFileDomain userFileDomain) {
		profileMapper.mbFileUpload(userFileDomain);
	}

	@Override
	public void mbFileUpdate(UserFileDomain userFileDomain) {
		profileMapper.mbFileUpdate(userFileDomain);
	}
	
	@Override
	public int mbFileUpdateCheck(UserFileDomain userFileDomain) {
		return profileMapper.mbFileUpdateCheck(userFileDomain);
	}
	
	@Override
	public void mbFileRemove(UserFileDomain userFileDomain) {
		profileMapper.mbFileRemove(userFileDomain);
	}

	@Override
	public void mbContentUpdate(UserContentDomain userContentDomain) {
		profileMapper.mbContentUpdate(userContentDomain);
	}

	@Override
	public void mbContentRemove(HashMap<String, Object> map) {
		profileMapper.mbContentRemove(map);
	}

	@Override
	public String userSelectName(HashMap<String, Object> map) {
		return profileMapper.userSelectName(map);
	}

	@Override
	public void changeName(HashMap<String, String> map) {
		profileMapper.changeName(map);
	}
}
