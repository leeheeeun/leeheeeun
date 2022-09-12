package com.my.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.my.dto.Board;
@SpringBootTest
class BoardRepositoryTest {
	@Autowired
	BoardRepository repository;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	@Test
	void testFindByIdValid() {
		Long boardNo = 1L;
		//게시글번호의 게시글 조회한다
		Optional<Board> optB1 = repository.findById(boardNo);
		assertTrue(optB1.isPresent());
	}
	@Test
	void testFindByIdInvalid() {
		Long boardNo = 1L;
		Optional<Board> optB1 = repository.findById(boardNo);
		assertFalse(optB1.isPresent());
	}
	
	@Test
//	@Transactional
	void testWrite() {
		Board b = new Board();
		b.setBoardTitle("title_t1");
		b.setBoardContent("content_t1");
		b.setBoardId("id1");
		b.setBoardParentNo(0L);
		repository.save(b);
	}

	@Test
	void testReply() {
		Board b = new Board();
		
		b.setBoardParentNo(1L); //1번글의 답글
		b.setBoardTitle("1_re_title");
		b.setBoardContent("1_re_content");
		b.setBoardId("id2");
		repository.save(b);
	}
	
	@Test
	void testModify() {
//		Board board =new Board();
//		board.setBoardNo(2L);
//		board.setBoardContent("글2내용 수정");
//		repository.save(board);
		
		Optional<Board> optB = repository.findById(4L);
		optB.ifPresent((b)->{
			b.setBoardContent("글4내용수정");
			repository.save(b);
		});
	}
	
	@Test
	void testUpdateViewCount() {
		Long boardNo = 1L;
		Optional<Board> optB = repository.findById(boardNo);
		optB.ifPresent((b)->{
			logger.error(b.toString());
			int oldViewCount = b.getBoardViewcount();
			int newViewCount = oldViewCount+1;
			b.setBoardViewcount(newViewCount);
			repository.save(b);
			
			int expectedNewViewCount = newViewCount;
			assertEquals(expectedNewViewCount, repository.findById(boardNo).get().getBoardViewcount());
		});
	}
	
	@Test
	void testDelete() {
		Long boardNo = 4L;
		repository.deleteReply(boardNo);
		repository.deleteById(boardNo);
		
		assertFalse(repository.findById(boardNo).isPresent());
	}
	@Test
	void testFindAllPage() {
		int currentPage = 1;
		Pageable pageable = PageRequest.of(currentPage-1, 3);//, Direction.ASC);
		List<Board> list = repository.findAll(pageable);
		list.forEach((b)->{
			logger.error(b.toString());
		});
	}
	
	@Test
	void testFindByPage() {
		int currentPage = 1;
		int cntPerPage = 3;
		int endRow = currentPage * cntPerPage;
		int startRow = endRow - cntPerPage+1;
		List<Board> list = repository.findByPage(startRow, endRow);
		list.forEach((b)->{
			logger.error(b.toString());
		});
	}
}
