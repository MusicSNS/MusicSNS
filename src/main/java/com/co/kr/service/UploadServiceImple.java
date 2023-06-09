package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.LikeListDomain;
import com.co.kr.domain.LoginDomain;
import com.co.kr.domain.MusicFileDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.MusicMapper;
import com.co.kr.mapper.UploadMapper;
import com.co.kr.mapper.UserMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class UploadServiceImple implements UploadService {

	@Autowired
	private UploadMapper uploadMapper;

	@Autowired
	private MusicMapper musicMapper;

	@Override
	public List<BoardListDomain> boardList() {
		// TODO Auto-generated method stub
		return uploadMapper.boardList();
	}

	@Override
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq,
			HttpServletResponse response) throws IOException {
		// session 생성
		HttpSession session = httpReq.getSession();

		// content domain 생성
		BoardContentDomain boardContentDomain = BoardContentDomain.builder().mbId(session.getAttribute("id").toString())
				.mbName(fileListVO.getName()).bdTitle(fileListVO.getTitle()).bdContent(fileListVO.getContent())
				.bdSeq(fileListVO.getSeq()).build();
		if (fileListVO.getIsEdit() != null) {
			boardContentDomain.setBdSeq(fileListVO.getSeq());
			System.out.println("글 수정 업데이트");
			// db 업데이트
			uploadMapper.bdContentUpdate(boardContentDomain);
		} else {
			// db 인서트
			uploadMapper.contentUpload(boardContentDomain);
			System.out.println("글 db 인서트");

		}

		// file 데이터 db 저장시 쓰일 값 추출
		int bdSeq = Integer.parseInt(boardContentDomain.getBdSeq());
		String mbId = boardContentDomain.getMbId();
		String mbName = boardContentDomain.getMbName();

		// 파일객체 담음
		List<MultipartFile> multipartFiles = request.getFiles("files");

		// 게시글 수정시 파일관련 물리저장 파일, db 데이터 삭제
		if (fileListVO.getIsEdit() != null) { // 수정시

			List<BoardFileDomain> fileList = null;

			for (MultipartFile multipartFile : multipartFiles) {

				if (!multipartFile.isEmpty()) { // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기

					if (session.getAttribute("files") != null) {

						fileList = (List<BoardFileDomain>) session.getAttribute("files");

						for (BoardFileDomain list : fileList) {
							list.getUpFilePath();
							Path filePath = Paths.get(list.getUpFilePath());

							try {

								// 파일 삭제
								Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
								// 삭제
								bdFileRemove(list); // 데이터 삭제

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
		Path rootPath = Paths.get(new File("C://").toString(), "upload", File.separator).toAbsolutePath().normalize();
		File pathCheck = new File(rootPath.toString());

		// folder chcek
		if (!pathCheck.exists())
			pathCheck.mkdirs();

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
					} else if (contentType.contains("image/gif")) {
						originalFileExtension = ".gif";
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
					BoardFileDomain boardFileDomain = BoardFileDomain.builder().bdSeq(bdSeq).mbId(mbId).mbName(mbName)
							.upOriginalFileName(origFilename).upNewFileName("resources/upload/" + newFileName) // WebConfig에
																												// 동적
																												// 이미지
																												// 폴더 생성
																												// 했기때문
							.upFilePath(targetPath.toString()).upFileSize((int) multipartFile.getSize()).build();

					// db 인서트
					uploadMapper.fileUpload(boardFileDomain);
				} catch (IOException e) {
					throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
				}
			}
		}

		return bdSeq; // 저장된 게시판 번호
	}

	public void MusicfileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, 
			HttpServletRequest httpReq, HttpServletResponse response, Integer bdSeq) throws IOException {
	    // session 생성
	    HttpSession session = httpReq.getSession();

	    // 게시글 정보 추출
	    String mbId = session.getAttribute("id").toString();
	    String mbName = fileListVO.getName();

	    // 파일객체 담음
	    List<MultipartFile> multipartFiles = request.getFiles("music");

	    // 게시글 수정시 파일관련 물리저장 파일, db 데이터 삭제
	    if (fileListVO.getIsEdit() != null) { // 수정시
	        List<MusicFileDomain> fileList = null;

	        for (MultipartFile multipartFile : multipartFiles) {
	            if (!multipartFile.isEmpty()) { // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기
	                if (session.getAttribute("music") != null) {
	                    fileList = (List<MusicFileDomain>) session.getAttribute("music");
	                    for (MusicFileDomain file : fileList) {
	                        file.getMusicFilePath();
	                        Path filePath = Paths.get(file.getMusicFilePath());
	                        try {
	                            // 파일 삭제
	                            Files.deleteIfExists(filePath);
	                            // 데이터베이스 삭제
	                            musicFileRemove(file);
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

	    // 새로운 파일 저장
	    // 저장 root 경로 만들기
	    Path rootPath = Paths.get(new File("C://").toString(), "audio", File.separator).toAbsolutePath().normalize();
	    File pathCheck = new File(rootPath.toString());

	    // 폴더 확인
	    if (!pathCheck.exists())
	        pathCheck.mkdirs();

	    // 파일 업로드 처리
	    if (multipartFiles.size() > 1) {
	        CommonUtils.redirect("파일은 하나만 선택 가능합니다.", "bdList", response);
	    } else {
	        for (MultipartFile multipartFile : multipartFiles) {
	            if (!multipartFile.isEmpty()) { // 파일이 존재할 때
	                // 확장자 추출
	                String originalFileExtension;
	                String contentType = multipartFile.getContentType();
	                String origFilename = multipartFile.getOriginalFilename();

	                // 확장자 유효성 검사
	                if (ObjectUtils.isEmpty(contentType)) {
	                    break;
	                } else {
	                    if (contentType.contains("audio")) { // 음악 파일인 경우
	                        originalFileExtension = ".mp3"; // 예시로 .mp3 확장자로 처리
	                    } else {
	                        break;
	                    }
	                }

	                // 파일명을 업로드한 날짜로 변환하여 저장
	                String uuid = UUID.randomUUID().toString();
	                String current = CommonUtils.currentTime();
	                String newFileName = uuid + current + originalFileExtension;

	                // 최종 경로 지정
	                Path targetPath = rootPath.resolve(newFileName);

	                File file = new File(targetPath.toString());

	                try {
	                    // 파일 복사 및 저장
	                    multipartFile.transferTo(file);
	                    // 파일 권한 설정(쓰기, 읽기)
	                    file.setWritable(true);
	                    file.setReadable(true);

	                    // 파일 도메인 생성
	                    MusicFileDomain musicFileDomain = MusicFileDomain.builder()
	                        .bdSeq(bdSeq)
	                        .mbId(mbId)
	                        .mbName(mbName)
	                        .musicOriginalFileName(origFilename)
	                        .musicNewFileName("resources/audio/" + newFileName)
	                        .musicFilePath(targetPath.toString())
	                        .musicFileSize((int) multipartFile.getSize())
	                        .build();

	                    // 데이터베이스에 파일 업로드
	                    musicMapper.musicFileUpload(musicFileDomain);
	                    System.out.println("음악 업로드 완료!");
	                } catch (IOException e) {
	                    throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
	                }
	            }
	        }
	    }
	}


	@Override
	public void bdContentRemove(HashMap<String, Object> map) {
		uploadMapper.bdContentRemove(map);
	}

	@Override
	public void bdFileRemove(BoardFileDomain boardFileDomain) {
		uploadMapper.bdFileRemove(boardFileDomain);
	}

	// 하나만 가져오기
	@Override
	public BoardListDomain boardSelectOne(HashMap<String, Object> map) {
		return uploadMapper.boardSelectOne(map);
	}

	// 하나 게시글 파일만 가져오기
	@Override
	public List<BoardFileDomain> boardSelectOneFile(HashMap<String, Object> map) {
		return uploadMapper.boardSelectOneFile(map);
	}

	// 게시글 업데이트하기
	@Override
	public void bdContentUpdate(BoardContentDomain boardContentDomain) {
		uploadMapper.bdContentUpdate(boardContentDomain);
	}

	// 게시글 사진 업데이트하기
	@Override
	public void bdFileUpdate(BoardFileDomain boardFileDomain) {
		uploadMapper.bdFileUpdate(boardFileDomain);
	}

	// 관련된 id에 대해서 전체 파일 삭제
	@Override
	public void bdFileAllRemove(HashMap<String, String> map) {
		uploadMapper.bdFileAllRemove(map);
	}

	@Override
	public void bdContentAllRemove(HashMap<String, String> map) {
		uploadMapper.bdContentAllRemove(map);
	}

	@Override
	public List<BoardListDomain> searchBoardByTitle(HashMap<String, String> map) {
		return uploadMapper.searchBoardByTitle(map);
	}

	@Override
	public int authorBoard(HashMap<String, Object> map) {
		return uploadMapper.authorBoard(map);
	}

	@Override
	public List<BoardListDomain> authorBoardList(HashMap<String, Object> map) {
		return uploadMapper.authorBoardList(map);
	}

	@Override
	public void insertlike(HashMap<String, Object> map) {
		uploadMapper.insertlike(map);
	};

	@Override
	public int selectlike(HashMap<String, Object> map) {
		return uploadMapper.selectlike(map);
	};

	@Override
	public void deletelike(HashMap<String, Object> map) {
		uploadMapper.deletelike(map);
	};

	@Override
	public void deletelikeuser(HashMap<String, Object> map) {
		uploadMapper.deletelikeuser(map);
	};

	@Override
	public int duplicatelike(HashMap<String, Object> map) {
		return uploadMapper.duplicatelike(map);
	}

	@Override
	public void deletelikeone(HashMap<String, Object> map) {
		uploadMapper.deletelikeone(map);
	}

	@Override
	public void updatelike(HashMap<String, Object> map) {
		uploadMapper.updatelike(map);
	}

	@Override
	public String selectstatus(HashMap<String, Object> map) {
		return uploadMapper.selectstatus(map);
	}

	@Override
	public List<LikeListDomain> selectlikemember(HashMap<String, Object> map) {
		return uploadMapper.selectlikemember(map);
	}

	@Override
	public List<LoginDomain> searchfollowmember(HashMap<String, Object> map) {
		return uploadMapper.searchfollowmember(map);
	}

	@Override
	public void musicFileRemove(MusicFileDomain musicFileDomain) {
		musicMapper.musicFileRemove(musicFileDomain);
	}

	@Override
	public void musicFileOneRemove(HashMap<String, String>map) {
		musicMapper.musicFileOneRemove(map);
	}

	// 하나 게시글 파일만 가져오기
	@Override
	public List<MusicFileDomain> boardSelectOneMusicFile(HashMap<String, Object> map) {
		return musicMapper.boardSelectOneMusicFile(map);
	}

	// 게시글 사진 업로드하기
	@Override
	public void musicFileUpload(MusicFileDomain musicFileDomain) {
		musicMapper.musicFileUpload(musicFileDomain);
	}

	// 게시글 사진 업데이트하기
	@Override
	public void musicFileUpdate(MusicFileDomain musicFileDomain) {
		musicMapper.musicFileUpdate(musicFileDomain);
	}

	// 관련된 id에 대해서 전체 파일 삭제
	@Override
	public void musicFileAllRemove(HashMap<String, String> map) {
		musicMapper.musicFileAllRemove(map);
	}
}