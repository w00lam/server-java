package kr.hhplus.be.server.unit.domain.concert.model;

import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.domain.concert.model.Concert;
import kr.hhplus.be.server.domain.concert.model.ConcertDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ConcertDateTest extends BaseUnitTest {
    @Test
    @DisplayName("ConcertDate.create()로 생성 시 필수 필드와 기본값이 올바르게 초기화되어야 한다")
     void testConcertDateCreate() {
        // given
        Concert concert = Concert.builder()
                .id(fixedUUID())
                .title("Test Concert")
                .description("Test Description")
                .build();

        LocalDate eventDate = LocalDate.of(2025, 12, 25);

        // when
        ConcertDate concertDate = ConcertDate.create(concert, eventDate);

        // then
        assertNotNull(concertDate.getId(), "UUID가 생성되어야 한다");
        assertEquals(concert, concertDate.getConcert(), "Concert 연관관계가 올바르게 설정되어야 한다");
        assertEquals(eventDate, concertDate.getEventDate());
        assertFalse(concertDate.isDeleted(), "deleted 기본값은 false여야 한다");


        assertNull(concertDate.getCreatedAt(), "createdAt은 JPA에서 설정되므로 테스트 시 null 가능");
        assertNull(concertDate.getUpdatedAt(), "updatedAt은 JPA에서 설정되므로 테스트 시 null 가능");
    }
}
