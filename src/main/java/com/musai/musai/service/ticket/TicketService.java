package com.musai.musai.service.ticket;

import com.musai.musai.dto.ticket.TicketDTO;
import com.musai.musai.entity.ticket.Ticket;
import com.musai.musai.repository.ticket.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<TicketDTO> getAllTicketsByUser(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return tickets.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDTO getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);
        return ticket != null ? toDTO(ticket) : null;
    }

    public TicketDTO addTicket(TicketDTO requestDTO) {
        Ticket ticket = Ticket.builder()
                .userId(requestDTO.getUserId())
                .createdAt(LocalDateTime.now())
                .ticketImage(requestDTO.getTicketImage())
                .title(requestDTO.getTitle())
                .artist(requestDTO.getArtist())
                .place(requestDTO.getPlace())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);
        return toDTO(savedTicket);
    }

    public String uploadTicketImage(MultipartFile file) throws IOException {
        return s3Service.uploadImage(file);
    }

    public TicketDTO deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElse(null);
        
        if (ticket != null) {
            if (ticket.getTicketImage() != null) {
                s3Service.deleteImage(ticket.getTicketImage());
            }
            
            ticketRepository.delete(ticket);
            return toDTO(ticket);
        }
        return null;
    }

    private TicketDTO toDTO(Ticket ticket) {
        return TicketDTO.builder()
                .ticketId(ticket.getTicketId())
                .userId(ticket.getUserId())
                .createdAt(ticket.getCreatedAt())
                .ticketImage(ticket.getTicketImage())
                .title(ticket.getTitle())
                .artist(ticket.getArtist())
                .place(ticket.getPlace())
                .build();
    }

}
