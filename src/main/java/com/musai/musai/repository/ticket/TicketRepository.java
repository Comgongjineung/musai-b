package com.musai.musai.repository.ticket;

import com.musai.musai.entity.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * 사용자 ID로 티켓 목록 조회
     */
    List<Ticket> findByUserIdOrderByCreatedAtDesc(Long userId);
}
