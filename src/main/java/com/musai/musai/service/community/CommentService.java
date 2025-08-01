package com.musai.musai.service.community;

import com.musai.musai.dto.bookmark.BookmarkDTO;
import com.musai.musai.dto.community.CommentDTO;
import com.musai.musai.dto.ticket.TicketDTO;
import com.musai.musai.entity.community.Comment;
import com.musai.musai.entity.ticket.Ticket;
import com.musai.musai.repository.community.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public List<CommentDTO> getAllCommentByPostId(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 페이징 + 계층형 댓글 조회 (통합)
    public Page<CommentDTO> getHierarchicalCommentsWithPaging(Long postId, Pageable pageable) {
        // 모든 댓글을 가져와서 계층형으로 구성
        List<Comment> allComments = commentRepository.findByPostId(postId);
        
        // 댓글을 부모-자식 관계로 그룹화
        Map<Long, List<Comment>> parentChildMap = allComments.stream()
                .filter(comment -> comment.getParentCommentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentCommentId));
        
        // 최상위 댓글만 계층형으로 구성
        List<CommentDTO> hierarchicalComments = allComments.stream()
                .filter(comment -> comment.getParentCommentId() == null)
                .map(comment -> {
                    CommentDTO dto = toDTO(comment);
                    // 해당 댓글의 답글들 추가
                    List<Comment> replies = parentChildMap.get(comment.getCommentId());
                    if (replies != null) {
                        dto.setReplies(replies.stream()
                                .map(this::toDTO)
                                .collect(Collectors.toList()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        // 페이징 처리
        int totalElements = hierarchicalComments.size();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, totalElements);
        
        List<CommentDTO> pageContent = start < totalElements ? 
                hierarchicalComments.subList(start, end) : 
                new ArrayList<>();
        
        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    public CommentDTO addComment(CommentDTO requestDTO) {
        Comment comment = Comment.builder()
                .userId(requestDTO.getUserId())
                .postId(requestDTO.getPostId())
                .content(requestDTO.getContent())
                .parentCommentId(requestDTO.getParentCommentId())
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return toDTO(savedComment);
    }

    public CommentDTO updateComment(Long commentId, CommentDTO requestDTO) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        comment.setContent(requestDTO.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return toDTO(updatedComment);
    }

    public CommentDTO deleteComment(Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        commentRepository.delete(comment);
        return toDTO(comment);
    }

    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
