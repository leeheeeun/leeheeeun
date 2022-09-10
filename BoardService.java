package com.my.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.my.dto.Board;
import com.my.dto.PageBean;
import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.repository.BoardRepository;

@Service
public class BoardService {
	private static final int CNT_PER_PAGE = 3; //페이지별 보여줄 목록수
	@Autowired
	private BoardRepository repository;
	
	/**
	 * 페이지별 게시글 목록과 페이지그룹정보를 반환한다
	 * @param currentPage 검색할 페이지
	 * @return
	 * @throws FindException
	 */
	public PageBean<Board> boardList(int currentPage) throws FindException{
	/*
		//(1)일단 repository에 메서드를 호출한다. 이때, currentPage와 CNT_PER_PAGE를 전달한다
		//(2) 그걸 Board 타입의 List로 받아온다
		List<Board> list =repository.selectByPage(currentPage, CNT_PER_PAGE);
		//(3)이거 뿐만아니라 전체행수를 반환하는 메서드도 호출!
		int totalCnt = repository.selectCount(); //총 행수 12,13
		//(4)Math.ceil()함수이용!(<->Math.floor() )해서 총페이지 계산 double타입으로 잠깐 바꿔서
		//		연산한 다음 다시 걔를 int 타입으로 바꿔서 저장해주어야해!!
		
		int cntPerPageGroup =2; //페이지별 보여줄 페이지수
		/*
		int totalPage = (int)Math.ceil((double)totalCnt/CNT_PER_PAGE); //총페이지수 4, 5
		
		// currentPage	startPage	totalPage	endPage
		//		1					1				5				2
		//		2					1								2
		//		3					3								4
		//		4					3								4
		//		5					5								5
		//(5) 지금보려는 페이지가 어떤페이지냐에 따라서 그 페이지 그룹에 시작페이지, 끝페이지가 결정됨!!
		int endPage = (int)(Math.ceil((double)currentPage/cntPerPageGroup)*cntPerPageGroup);
		int startPage = endPage - cntPerPageGroup +1;
		if(totalPage < endPage) {
			endPage = totalPage;
		}	
		PageBean<Board> pb = new PageBean<>();
		pb.setList(list);
		pb.setCurrentPage(currentPage);
		pb.setTotalPage(totalPage);
		pb.setStartPage(startPage);
		pb.setEndPage(endPage);
		pb.setCntPerPageGroup(cntPerPageGroup);
		return pb;
		
		PageBean<Board> pb1 = new PageBean<Board>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
		return pb1;
		*/
//		Pageable pageable = PageRequest.of(currentPage, CNT_PER_PAGE, Direction.ASC);
//		repository.findAll(pageable);
		//전체행의 currentPage를 볼건데 CNT_PER_PAGE만큼 보겠다라는 뜻
//		return null;
		int endRow = currentPage * CNT_PER_PAGE;
		int starRow = endRow - CNT_PER_PAGE+1;
		List<Board> list = repository.findByPage(starRow, endRow);
		long totalCnt = repository.count();
		int cntPerPageGroup = 2; //페이지별 보여줄 페이지수
		PageBean<Board> pb1 = new PageBean<Board>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
		return pb1;
	}
	
	/**
	 * 검색어를 이용한 게시글 검색 목록과 페이지 그룹정보를 반환한다
	 * @param word 검색어
	 * @param currentPage 검색할 페이지
	 * @return
	 * @throws FindException
	 */
	public PageBean<Board> searchBoard(String word, int currentPage) throws FindException{
		/*
		List<Board> list = repository.selectByWord(word, currentPage, CNT_PER_PAGE);
		int totalCnt = repository.selectCount(word);
		int cntPerPageGroup = 2;
		PageBean<Board> pb1 = new PageBean<>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
		return pb1;
		*/
		return null;
	}
	
	/**
	 * 게시글 번호의 조회수를 1증가한다
	 * 게시글 번호의 게시글을 반환한다
	 * @param boardNo 게시글번호
	 * @return
	 * @throws FindException
	 */
	public Board viewBoard(Long boardNo) throws FindException{
			//조회수를 1증가한다
			Optional<Board> optB = repository.findById(boardNo);
			if(optB.isPresent()) {
				Board b = optB.get();
				b.setBoardViewcount(b.getBoardViewcount()+1);
				repository.save(b);
			} else {
				throw new FindException("게시글이 없습니다.");
			}
			
			//게시글번호의 게시글 조회한다
			Optional<Board> optB1 = repository.findById(boardNo);
			if(optB1.isPresent()) {
				Board b1 = optB1.get();
				return b1;
			}else {
				throw new FindException("게시글이 없습니다.");
			}
		
	}
	/**
	 * 글쓰기
	 * @param repBoard
	 * @throws AddException
	 */
	public void writeBoard(Board board) throws AddException{
		board.setBoardParentNo(0L);
//		repository.insert(board);
		repository.save(board);
	}
	
	/**
	 * 답글쓰기
	 * @param board
	 * @throws AddException
	 */
	public void replyBoard(Board board) throws AddException{
		if(board.getBoardParentNo() == 0) {
			throw new AddException("답글쓰기의 부모글번호가 없습니다");
		}
		repository.save(board);
	}

	public void modifyBoard(Board board) throws ModifyException {
		Optional<Board> optB = repository.findById(board.getBoardNo());
		if(!optB.isPresent()) {
			throw new ModifyException("글이 없습니다.");
		}else {
			Board b = optB.get();
			b.setBoardContent(board.getBoardContent());
			repository.save(b);
		}
//		optB.ifPresent((b) ->{
//			
//		});
		
	}
//할일 : 조회수 1증가 완성, 글삭제 완성
	public void removeBoard(Long boardNo) {
		repository.deleteReply(boardNo);
		repository.deleteById(boardNo);
	}


}
