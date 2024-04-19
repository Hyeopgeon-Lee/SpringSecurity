package kopo.poly.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info(this.getClass().getName() + ".PasswordEncoder Start!");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info(this.getClass().getName() + ".filterChain Start!");

        http.csrf(AbstractHttpConfigurer::disable)         // POST 방식 전송을 위해 csrf 막기
                .authorizeHttpRequests(authz -> authz // 페이지 접속 권한 설정
                                // USER 권한
                                .requestMatchers("/notice/**").hasAnyAuthority("ROLE_USER")
                                .requestMatchers("/html/user/**").authenticated() // Spring Security 인증된 사용자만 접근
//                                .antMatchers("/user/**", "/notice/**").hasAnyAuthority("ROLE_USER")

                                // 관리자 권한
                                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
//                        .anyRequest().authenticated() // 그외 나머지 url 요청은 인증된 사용자만 가능
                                .anyRequest().permitAll() // 그 외 나머지 url 요청은 인증 받지 않아도 접속 가능함
                )
                .formLogin(login -> login // 로그인 페이지 설정
                        .loginPage("/html/ss/login.html")
                        .loginProcessingUrl("/login/v1/loginProc")
                        .usernameParameter("userId") // 로그인 ID로 사용할 html의 input객체의 name 값
                        .passwordParameter("password") // 로그인 패스워드로 사용할 html의 input객체의 name 값
                        .successForwardUrl("/login/v1/loginSuccess") // Web MVC, Controller 사용할 때 적용 / 로그인 성공 URL
                        .failureForwardUrl("/login/v1/loginFail") // Web MVC, Controller 사용할 때 적용 / 로그인 실패 URL
                )
                .logout(logout -> logout
                        .logoutUrl("/user/v1/logout") // 로그이웃 요청 URL
                        .clearAuthentication(true) // Spring Security 저장된 인증 정보 초기화
                        .logoutSuccessUrl("/user/v1/logoutSuccess") // 로그아웃 성공 처리 URL(세션 값 삭제)
                );

        return http.build();
    }
}