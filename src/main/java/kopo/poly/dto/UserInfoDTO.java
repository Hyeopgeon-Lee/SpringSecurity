package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kopo.poly.repository.entity.UserInfoEntity;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import kopo.poly.util.EncryptUtil;
import lombok.Builder;

import java.io.Serializable;

/**
 * 사용자 정보 DTO (Data Transfer Object)
 * - 클라이언트 ↔ 서버 간 데이터 전송을 위한 객체
 * - 회원가입, 로그인, 사용자 정보 조회 등에 활용됨
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoDTO(

        /** 사용자 아이디 (로그인 ID) */
        @NotBlank(message = "아이디는 필수 입력 사항입니다.")
        @Size(min = 4, max = 16, message = "아이디는 최소 4글자에서 16글자까지 입력가능합니다.")
        String userId,

        /** 사용자 이름 */
        @NotBlank(message = "이름은 필수 입력 사항입니다.")
        @Size(max = 10, message = "이름은 10글자까지 입력가능합니다.")
        String userName,

        /** 비밀번호 (암호화되어 저장됨) */
        @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
        @Size(max = 16, message = "비밀번호는 16글자까지 입력가능합니다.")
        String password,

        /** 이메일 주소 */
        @NotBlank(message = "이메일은 필수 입력 사항입니다.")
        @Size(max = 30, message = "이메일은 30글자까지 입력가능합니다.")
        @Email String email,

        /** 기본 주소 */
        @NotBlank(message = "주소는 필수 입력 사항입니다.")
        @Size(max = 30, message = "주소는 30글자까지 입력가능합니다.")
        String addr1,

        /** 상세 주소 */
        @NotBlank(message = "상세 주소는 필수 입력 사항입니다.")
        @Size(max = 100, message = "상세 주소는 100글자까지 입력가능합니다.")
        String addr2,

        /** 등록자 ID */
        String regId,

        /** 등록일시 */
        String regDt,

        /** 수정자 ID */
        String chgId,

        /** 수정일시 */
        String chgDt,

        /** 사용자 권한 (예: ROLE_USER, ROLE_ADMIN 등) */
        String roles,

        /**
         * 회원가입 시 중복가입 여부 확인을 위한 필드
         * DB에 존재하지 않는 가상의 컬럼으로 사용됨 (ALIAS 컬럼)
         */
        String existsYn
) implements Serializable {

    /**
     * 회원가입용 사용자 정보 생성 메서드
     * - 비밀번호 암호화, 권한 설정, 등록일/수정일 자동 세팅
     */
    public static UserInfoDTO createUser(UserInfoDTO pDTO, String password, String roles) throws Exception {
        return UserInfoDTO.builder()
                .userId(pDTO.userId())
                .userName(pDTO.userName())
                .password(password) // Spring Security로 암호화된 비밀번호 저장
                .email(EncryptUtil.encAES128CBC(pDTO.email())) // 이메일 암호화 저장
                .addr1(pDTO.addr1())
                .addr2(pDTO.addr2())
                .roles(roles) // 권한 설정
                .regId(pDTO.userId())
                .regDt(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"))
                .chgId(pDTO.userId())
                .chgDt(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss"))
                .build();
    }

    /**
     * DTO → JPA Entity 변환 메서드
     * - 데이터베이스에 저장할 때 사용됨
     */
    public static UserInfoEntity of(UserInfoDTO dto) {
        return UserInfoEntity.builder()
                .userId(dto.userId())
                .userName(dto.userName())
                .password(dto.password())
                .email(dto.email())
                .addr1(dto.addr1())
                .addr2(dto.addr2())
                .roles(dto.roles())
                .regId(dto.regId())
                .regDt(dto.regDt())
                .chgId(dto.chgId())
                .chgDt(dto.chgDt())
                .build();
    }

    /**
     * JPA Entity → DTO 변환 메서드
     * - 이메일 복호화 포함 (조회 시 사용자에게 복호화된 값 제공)
     */
    public static UserInfoDTO from(UserInfoEntity entity) throws Exception {
        return UserInfoDTO.builder()
                .userId(entity.getUserId())
                .userName(entity.getUserName())
                .password(entity.getPassword())
                .email(EncryptUtil.decAES128CBC(CmmUtil.nvl(entity.getEmail()))) // 복호화된 이메일 반환
                .addr1(entity.getAddr1())
                .addr2(entity.getAddr2())
                .roles(entity.getRoles())
                .regId(entity.getRegId())
                .regDt(entity.getRegDt())
                .chgId(entity.getChgId())
                .chgDt(entity.getChgDt())
                .build();
    }
}