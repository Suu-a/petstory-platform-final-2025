package com.petservice.main.user.controller;

import com.petservice.main.user.database.dto.*;
import com.petservice.main.user.database.entity.RefreshToken;
import com.petservice.main.user.database.entity.User;
import com.petservice.main.user.database.mapper.UserMapper;
import com.petservice.main.user.jwt.JwtService;
import com.petservice.main.user.service.Interface.CustomUserServiceInterface;
import com.petservice.main.user.service.Interface.RefreshTokenServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final JwtService jwtService;
  private final RefreshTokenServiceInterface refreshTokenService;
  private final CustomUserServiceInterface customUserService;
  private final UserDetailsService userDetailsService;

  private final UserMapper userMapper;

  private static final Long JWT_EXPIRATION = 1000L * 60 * 15;

  //토큰 리프레시
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(HttpServletRequest request,
      HttpServletResponse response) {

    Map<String, Object> result = new HashMap<>();

    String tokenInfo = null;
    if(request.getCookies() != null){
      for (var cookie : request.getCookies()) {
        if ("refreshToken".equals(cookie.getName())) {
          tokenInfo = cookie.getValue();
          break;
        }
      }
    }
    if (tokenInfo == null) {
      result.put("result", false);
      result.put("message", "리프레시 토큰이 쿠키에 없습니다.");
      return new ResponseEntity<>("refresh token null", HttpStatus.UNAUTHORIZED);
    }

    Optional<RefreshToken> findToken = refreshTokenService.findByToken(tokenInfo);
    if (findToken.isEmpty()) {
      result.put("result", false);
      result.put("message", "토큰이 존재하지 않습니다.");
      return new ResponseEntity<>("refresh token null", HttpStatus.UNAUTHORIZED);
    }

    RefreshToken refreshToken = findToken.get();
    RefreshToken verifiedToken = refreshTokenService.verifyExpiration(refreshToken);
    if (verifiedToken == null) {
      result.put("result", false);
      result.put("message", "토큰이 이미 만료되었습니다.");
      return new ResponseEntity<>("refresh token expired", HttpStatus.UNAUTHORIZED);
    }
    try {
      User user = verifiedToken.getUser();
      String newAccessToken =
          jwtService.createJwt(user.getUserLoginId(), user.getRole().name(), user.getName(),
          JWT_EXPIRATION, "ACCESS");

      ResponseCookie.ResponseCookieBuilder token =
          ResponseCookie.from("accessToken", newAccessToken);
      token.httpOnly(true);
      token.secure(true);
      token.path("/");
      //token.maxAge(JWT_EXPIRATION / 1000);
      token.sameSite("None");
      ResponseCookie accessCookie = token.build();

      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

      return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, tokenInfo, "Bearer"));
    }catch (Exception e){
      log.error("Token Refresh Error: {}",e.getMessage());
      result.put("result", false);
      result.put("message","토큰 생성도중 오류 발생");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
  }

  //회원 로그인
  @PostMapping("/login")
  public ResponseEntity<?> AuthenticateUser(@RequestBody LoginRequest loginRequest,
      HttpServletResponse response) {
    try {
      String userLoginId = loginRequest.getUsername();
      String userPassword = loginRequest.getPassword();

      if(userLoginId.isBlank() || userPassword.isBlank()){
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated",false);
        result.put("message", "Login Fail");
        return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
      }

      UserDTO user = customUserService.UserLogin(userLoginId, userPassword);
      String accessToken = jwtService.createAccessToken(user.getUserLoginId(),user.getName(), user.getRole().name());
      RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(user.getUserLoginId());
      String refreshToken = refreshTokenEntity.getToken();

      ResponseCookie.ResponseCookieBuilder token =
          ResponseCookie.from("accessToken", accessToken);
      token.httpOnly(true);
      token.secure(true);
      token.path("/");
      //token.maxAge(5);
      token.sameSite("None");
      ResponseCookie accessCookie = token.build();

      long refreshMaxAge = refreshTokenEntity.getExpiryDate().getEpochSecond() -
          (System.currentTimeMillis() / 1000);
      ResponseCookie.ResponseCookieBuilder
          refreshCookieBuilder = ResponseCookie.from("refreshToken", refreshToken);
      refreshCookieBuilder.httpOnly(true);
      refreshCookieBuilder.secure(true);
      refreshCookieBuilder.path("/");
      refreshCookieBuilder.maxAge(refreshMaxAge);
      refreshCookieBuilder.sameSite("None");
      ResponseCookie refreshCookie = refreshCookieBuilder.build();

      response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

      Map<String, Object> result = new HashMap<>();
      result.put("tokenType", "Bearer");
      result.put("authenticated", true);
      result.put("name", user.getName());
      result.put("LoginId", user.getUserLoginId());
      result.put("LoginUser",user);

      return ResponseEntity.status(HttpStatus.CREATED)
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .body(result);

    }catch (Exception e){
      log.error("Auth Fail: {}", e.getMessage());
      Map<String, Object> result = new HashMap<>();
      result.put("authenticated",false);
      result.put("message", "Login Fail");
      return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
    }
  }

  //회원 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser(@AuthenticationPrincipal CustomUserDetails principal,
      HttpServletResponse response){

    Map<String, Object> result = new HashMap<>();
    try {

      String userLoginId = principal.getUsername();
      refreshTokenService.deleteByUserId(userLoginId);

      ResponseCookie deleteAccessCookie =
          ResponseCookie.from("accessToken", "").httpOnly(true).secure(true).path("/").maxAge(0)
              .sameSite("None").build();
      ResponseCookie deleteRefreshCookie =
          ResponseCookie.from("refreshToken", "").httpOnly(true).secure(true)
              .path("/").maxAge(0).sameSite("None").build();
      ResponseCookie deleteSessionCookie =
          ResponseCookie.from("JSESSIONID", "").httpOnly(true).secure(true)
              .path("/").maxAge(0).sameSite("None").build();

      response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
      response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionCookie.toString());

      result.put("result",true);
      return ResponseEntity.ok(result);

    }catch (Exception e){
      log.error("로그아웃에 실패했습니다: {}", e.getMessage());
      result.put("result",false);
      result.put("message", "logout fail");
      return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  //회원 등록
  @PostMapping("/register")
  public ResponseEntity<?> RegisterUser(@RequestBody UserDTO user){
    Map<String,Object> result=new HashMap<>();
    if(user==null){
      result.put("result",false);
      result.put("message","register user fail");
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    UserDTO newuserDto= customUserService.registerUser(user);
    if(newuserDto!=null) {
      result.put("result", true);
      result.put("UserInfo", newuserDto);
    }else{
      result.put("result",false);
      result.put("message","register user fail");
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    return  ResponseEntity.ok(result);
  }

  //회원 탈퇴(모든 토큰들도 삭제)
  @PostMapping("/delete")
  public ResponseEntity<?> DeleteUser(
      @AuthenticationPrincipal CustomUserDetails principal,
      @RequestBody Map<String,String> deleteRequest,
      HttpServletResponse response){

    Map<String,Object> result=new HashMap<>();

    if(principal==null){
      result.put("result",false);
      result.put("message", "none login");
      return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
    }

    String userLoginId = principal.getUsername();
    refreshTokenService.deleteByUserId(userLoginId);

    ResponseCookie deleteAccessCookie =
        ResponseCookie.from("accessToken", "").httpOnly(true).secure(true).path("/").maxAge(0)
            .sameSite("Strict").build();
    ResponseCookie deleteRefreshCookie =
        ResponseCookie.from("refreshToken", "").httpOnly(true).secure(true)
            .path("/").maxAge(0).sameSite("None").build();
    ResponseCookie deleteSessionCookie =
        ResponseCookie.from("JSESSIONID", "").httpOnly(true).secure(true)
            .path("/").maxAge(0).sameSite("None").build();

    response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, deleteSessionCookie.toString());

    if(customUserService.DeleteUser(principal, deleteRequest.get("LoginId")
        ,deleteRequest.get("Password"))){
      result.put("result", true);
    }else{
      result.put("result", false);
      result.put("message","delete user fail");
      return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(result);
  }

  //회원 정보 수정
  @PutMapping("/update")
  public ResponseEntity<?> UpdateUserInfo(@AuthenticationPrincipal Object principal,
      @RequestBody UserDTO user){
    Map<String,Object> result=new HashMap<>();
    UserDTO updateUser=customUserService.UpdateUser((CustomUserDetails)principal,user);

    if(updateUser==null) {
      result.put("result",false);
      result.put("message","update user fail");
      return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
    }else {
      result.put("result", true);
      result.put("updateUser", updateUser);
      return ResponseEntity.ok(result);
    }
  }

  //회원 정보 읽기
  @GetMapping("/info")
  public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal Object principal){
    Map<String,Object> result=new HashMap<>();
    log.info(principal.getClass().getName());
    if(principal==null){
      result.put("auth", false);
      result.put("message", "none login");
      return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
    }

    if (principal instanceof CustomOAuth2UserDetail) {
      CustomOAuth2UserDetail oAuth2UserDetail=(CustomOAuth2UserDetail)principal;
      result.put("loginMethod", "google");
      result.put("auth", true);
      result.put("loginId", oAuth2UserDetail.getName());
      result.put("UserName", oAuth2UserDetail.getUsername());
      result.put("Role", oAuth2UserDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining()));

    } else if(principal instanceof CustomUserDetails ){
      result.put("loginMethod", "normal");
      CustomUserDetails customUserDetails = (CustomUserDetails) principal;
      result.put("auth", true);
      result.put("loginId", customUserDetails.getUsername());
      result.put("UserName", customUserDetails.getName());
      result.put("Role", customUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining()));

    }else {
      log.warn("Unknown principal type: {}", principal.getClass().getName());
      result.put("auth", false);
      result.put("message", "알 수 없는 사용자 타입");
      return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
    }

    return ResponseEntity.ok(result);
  }

  //회원 세부정보 읽기
  @GetMapping("/detailInfo")
  public ResponseEntity<?> getUserDetailInfo(
      @AuthenticationPrincipal CustomUserDetails principal,
      @RequestParam(name = "userLoginId") String UserLoginId){
    Map<String,Object> result=new HashMap<>();
    if(principal==null||principal.getUsername().compareTo(UserLoginId)!=0){
      result.put("auth",false);
      result.put("message","비로그인 상태거나, 잘못된 요청입니다.");
      return new ResponseEntity<>(result,HttpStatus.UNAUTHORIZED);
    }
    UserDTO userDetailInfo=customUserService.getUserFromPrincipal(principal);

    result.put("auth",true);
    result.put("userDetail",userDetailInfo);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/updatePassword")
  public ResponseEntity<?> updatePassword(
      @AuthenticationPrincipal CustomUserDetails principal,
      @RequestBody UpdatePasswordRequest updatePasswordRequest){
    Map<String,Object> result=new HashMap<>();
    if(principal != null && customUserService.UpdatePassword(
        updatePasswordRequest.getUserLoginId(),
        updatePasswordRequest.getOldPassword(),
        updatePasswordRequest.getNewPassword())){
      result.put("auth", true);
      result.put("message","비밀번호 변경 완료");
    }else{
      result.put("auth", false);
      result.put("message","비밀번호 변경 실패");
    }

    return ResponseEntity.ok(result);
  }
}
