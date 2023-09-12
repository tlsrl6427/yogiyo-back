package toy.yogiyo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.yogiyo.core.Member.domain.Member;
import toy.yogiyo.core.Member.dto.MemberJoinRequest;
import toy.yogiyo.core.Member.dto.MemberMypageResponse;
import toy.yogiyo.core.Member.dto.MemberUpdateRequest;
import toy.yogiyo.core.Member.service.MemberService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @MockBean
    MemberService memberService;

    ObjectMapper objectMapper;
    MockMvc mockMvc;
    Member member;
    @BeforeEach
    void beforeEach(WebApplicationContext context){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();

        objectMapper = new ObjectMapper();

        member = Member.builder()
                .id(1L)
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .build();
    }

    @DisplayName("회원가입 API")
    @Test
    void join() throws Exception{
        MemberJoinRequest memberJoinRequest = MemberJoinRequest.builder()
                .nickname("test")
                .email("test@gmail.com")
                .password("1234")
                .providerType(null)
                .build();

        given(memberService.join(any())).willReturn(member.getId());

        mockMvc.perform(
                    post("/member/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(memberJoinRequest))
                )
                .andExpect(status().isOk())
                //.andExpect(jsonPath("id").value(member.getId()))
                .andExpect(content().string("1"))
                .andDo(print());

        verify(memberService).join(any());
    }

    @DisplayName("마이페이지 조회 API")
    @Test
    void showMypage() throws Exception{
        MemberMypageResponse memberMypageResponse = MemberMypageResponse.builder()
                .nickname("test")
                .email("test@gmail.com")
                .build();

        given(memberService.findOne(any())).willReturn(memberMypageResponse);

        mockMvc.perform(
                    get("/member/mypage/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(print());

        verify(memberService).findOne(any());
    }

    @DisplayName("회원정보 업데이트")
    @Test
    void update() throws Exception{
        MemberUpdateRequest memberUpdateRequest = MemberUpdateRequest.builder()
                .nickname("test2")
                .build();

        mockMvc.perform(
                    patch("/member/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(memberUpdateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().string("update success"))
                .andDo(print());
    }

    @DisplayName("회원삭제 API")
    @Test
    void delete_member() throws Exception{
        mockMvc.perform(
                    delete("/member/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("delete success"))
                .andDo(print());
    }
}