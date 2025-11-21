package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.domain.ConcertDate;
import kr.hhplus.be.server.domain.Seat;
import kr.hhplus.be.server.service.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AvailabilityController.class)
@Import(AvailabilityControllerTest.TestConfig.class)
class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AvailabilityService availabilityService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AvailabilityService availabilityService() {
            return Mockito.mock(AvailabilityService.class);
        }
    }

    @Test
    void getConcertDates_shouldReturnDateList() throws Exception {
        // given
        UUID concertDateId = UUID.fromString("f4e2b1ad-9c6d-4c3b-8e3e-2f6a2e7d9b11");
        UUID concertId = UUID.fromString("f4e2b1ad-9c6d-4c3b-8e3e-2f6a2e7d9b12");

        LocalDate eventDate = LocalDate.of(2025, 12, 25);
        LocalDateTime createdOrUpdate = LocalDateTime.of(2025, 12, 12, 12, 12, 12);

        ConcertDate concertDate = new ConcertDate(concertDateId, concertId, eventDate, createdOrUpdate, createdOrUpdate, false);

        when(availabilityService.listConcertDates(concertId)).thenReturn(List.of(concertDate));

        // when & then
        mockMvc.perform(get("/concerts/{concertId}/dates", concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(concertDate.getId().toString()));
    }

    @Test
    void getAvailableSeats_shouldReturnSeatList() throws Exception {
        UUID seatId1 = UUID.fromString("8c0a4f6e-3b7e-4e57-a9c9-2d4f1df0fb32");
        UUID seatId2 = UUID.fromString("8c0a4f6e-3b7e-4e57-a9c9-2d4f1df0fb33");
        UUID concertDateId = UUID.fromString("8c0a4f6e-3b7e-4e57-a9c9-2d4f1df0fb34");
        UUID concertId=UUID.fromString("8c0a4f6e-3b7e-4e57-a9c9-2d4f1df0fb35");

        LocalDateTime createdOrUpdate = LocalDateTime.of(2025, 12, 12, 12, 12, 12);

        Seat s1 = new Seat(seatId1, concertDateId, "A", "1", "1", "VIP", createdOrUpdate, createdOrUpdate, false);
        Seat s2 = new Seat(seatId2, concertDateId, "A", "1", "2", "VIP", createdOrUpdate, createdOrUpdate, false);

        when(availabilityService.listAvailableSeats(concertDateId, null, null, null))
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/concerts/{cid}/dates/{did}/seats", concertId, concertDateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].section").value("A"))
                .andExpect(jsonPath("$[1].number").value("2"));
    }

    @Test
    void getAvailableSeats_withFilters_shouldPassParams() throws Exception {
        UUID concertId = UUID.fromString("d3a8f1c2-6e9b-47b4-9f56-1c7e3ad4b821");
        UUID concertDateId = UUID.fromString("d3a8f1c2-6e9b-47b4-9f56-1c7e3ad4b822");

        when(availabilityService.listAvailableSeats(concertDateId, "A", "1", "VIP")).thenReturn(List.of());

        mockMvc.perform(get("/concerts/{concertId}/dates/{concertDateId}/seats", concertId, concertDateId)
                                .param("section", "A")
                                .param("row", "1")
                                .param("grade", "VIP")
                ).andExpect(status().isOk());
    }
}
