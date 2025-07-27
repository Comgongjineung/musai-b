package com.musai.musai.controller.ticket;

import com.musai.musai.dto.ticket.TicketDTO;
import com.musai.musai.service.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "티켓 목록 전체 조회", description = "티켓 리스트를 전체 조회합니다.")
    @GetMapping("/readAll/{userId}")
    public ResponseEntity<List<TicketDTO>> readTicketList(@PathVariable Long userId) {
        List<TicketDTO> tickets = ticketService.getAllTicketsByUser(userId);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "티켓 상세 조회", description = "티켓을 상세 조회합니다.")
    @GetMapping("/read/{ticketId}")
    public ResponseEntity<TicketDTO> readTicket(@PathVariable Long ticketId) {
        TicketDTO ticket = ticketService.getTicket(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "티켓 추가", description = "티켓을 추가합니다.")
    @PostMapping("/add")
    public ResponseEntity<TicketDTO> addTicket(@RequestBody TicketDTO requestDTO) {
        TicketDTO newTicket = ticketService.addTicket(requestDTO);
        return ResponseEntity.ok(newTicket);
    }

    @Operation(summary = "티켓 삭제", description = "티켓을 삭제합니다.")
    @DeleteMapping("/delete/{ticketId}")
    public ResponseEntity<TicketDTO> deleteTicket(@PathVariable Long ticketId) {
        TicketDTO ticket = ticketService.deleteTicket(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }
}
