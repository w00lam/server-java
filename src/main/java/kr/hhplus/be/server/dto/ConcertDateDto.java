package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.ConcertDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertDateDto {
    private UUID id;
    private LocalDate eventDate;

    public static ConcertDateDto fromEntity(ConcertDate entity){
        return new ConcertDateDto(entity.getId(), entity.getEventDate());
    }
}
