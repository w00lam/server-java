package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.Seat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private UUID id;
    private String section;
    private String row;
    private String number;
    private String grade;

    public static SeatDto fromEntity(Seat entity) {
        return new SeatDto(entity.getId(), entity.getSection(), entity.getRow(),
                entity.getNumber(), entity.getGrade());
    }
}
