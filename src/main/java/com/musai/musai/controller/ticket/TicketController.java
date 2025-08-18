package com.musai.musai.controller.ticket;

import com.musai.musai.dto.ticket.ColorDTO;
import com.musai.musai.dto.ticket.TicketDTO;
import com.musai.musai.service.ticket.ColorService;
import com.musai.musai.service.ticket.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "티켓", description = "티켓 기능 API")
public class TicketController {

    private final TicketService ticketService;
    private final ColorService colorService;

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

    @Operation(summary = "티켓 이미지 업로드", description = "티켓 이미지를 서버에 업로드하고 URL을 반환합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTicketImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = ticketService.uploadTicketImage(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이미지 업로드 실패: " + e.getMessage());
        }
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

    @Operation(summary = "티켓 색상 추천", description = "티켓 배경 색상을 추천합니다.")
    @PostMapping(value = "/color", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ColorDTO recommendColor(@RequestPart("image") MultipartFile image) throws Exception {
        return colorService.getColorFromAiServer(image);
    }
}
