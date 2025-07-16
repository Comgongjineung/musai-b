package com.musai.musai.dto.exhibition;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ExhibitionDTO {
    private String title; //제목
    private String cntc_instt_nm; //연계기관명
    private String description; //설명
    private String image_object; //이미지 주소 (전시회 이미지)
    private String genre; //전시회 장르
    private String url; //홈페이지 주소
    private String duration; //관람 시간
    private String period; //전시회 기간
    private String event_period; //전시회 시간
    private String table_of_contents; //안내 및 유의사항
    private String event_site; //장소
    private String contributor; //주최
}
