package com.musai.musai.service.community;

import com.musai.musai.dto.bookmark.BookmarkDTO;
import com.musai.musai.dto.community.CommentDTO;
import com.musai.musai.dto.community.CommentRequestDTO;
import com.musai.musai.dto.community.CommentUpdateDTO;
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
                    dto.setReplies(getRepliesRecursively(comment.getCommentId(), parentChildMap));
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

    // 재귀적으로 답글을 처리하는 메서드
    private List<CommentDTO> getRepliesRecursively(Long parentCommentId, Map<Long, List<Comment>> parentChildMap) {
        List<Comment> replies = parentChildMap.get(parentCommentId);
        if (replies == null || replies.isEmpty()) {
            return new ArrayList<>();
        }
        
        return replies.stream()
                .map(reply -> {
                    CommentDTO replyDto = toDTO(reply);
                    replyDto.setReplies(getRepliesRecursively(reply.getCommentId(), parentChildMap));
                    return replyDto;
                })
                .collect(Collectors.toList());
    }

    public CommentDTO addComment(CommentRequestDTO requestDTO) {
        LocalDateTime now = LocalDateTime.now();
        Comment comment = Comment.builder()
                .userId(requestDTO.getUserId())
                .postId(requestDTO.getPostId())
                .content(requestDTO.getContent())
                .parentCommentId(requestDTO.getParentCommentId())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return toDTO(savedComment);
    }

    public CommentDTO updateComment(Long commentId, CommentUpdateDTO requestDTO) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        comment.setContent(requestDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return toDTO(updatedComment);
    }

    public CommentDTO deleteComment(Long commentId) {
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        commentRepository.delete(comment);
        return toDTO(comment);
    }

    public Long getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
