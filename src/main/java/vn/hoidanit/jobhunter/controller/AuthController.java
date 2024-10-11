package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SercurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SercurityUtil sercurityUtil;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Value("${duccute.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                          SercurityUtil sercurityUtil,
                          UserService userService,
                          PasswordEncoder passwordEncoder
                          ) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.sercurityUtil = sercurityUtil;
        this.userService = userService;
        this.passwordEncoder=passwordEncoder;

    }

    @PostMapping("/register")
    public ResponseEntity<ResCreateUserDTO> register(@RequestBody User user) throws IdInvalidException {
        boolean isExistUser = this.userService.isEmailExist(user.getEmail());
        if(isExistUser) {
            throw new IdInvalidException("Email :"+user.getEmail()+"disiss exist!");
        }
        String password = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
//        Nạp input username ,password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (loginDTO.getUserName(),loginDTO.getPassword());

//        Tiến hành xác thực
        Authentication authentication =authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userInDB = this.userService.handleGetUserByUserName(loginDTO.getUserName());

        ResLoginDTO res = new ResLoginDTO();
        if(userInDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    userInDB.getId(),
                    userInDB.getEmail(),
                    userInDB.getName(),
                    userInDB.getRole()
            );
            res.setUser(userLogin);
        }
        String access_token = this.sercurityUtil.createAccessToken(authentication.getName(),res);
        res.setAccessToken(access_token);

        //create token
        String refreshToken = sercurityUtil.createRefreshToken(loginDTO.getUserName(),res);

        //update in db
        this.userService.updateUserToken(refreshToken, loginDTO.getUserName());

        //set cookie
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token",refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();



        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,resCookie.toString()).body(res);
    }

    @GetMapping("/account")
    @ApiMessage("Fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ?
                SercurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByUserName(email);

        ResLoginDTO.UserLogin userLgin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if(currentUser != null) {
            userLgin.setId(currentUser.getId());
            userLgin.setName(currentUser.getName());
            userLgin.setEmail(currentUser.getEmail());
            userGetAccount.setUser(userLgin);

        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token",defaultValue = "notToken") String refreshToken) throws IdInvalidException {
        if(refreshToken.equals("notToken")) {
            throw new IdInvalidException("Không tồn tại refresh token trong cookie");
        }

        Jwt jwtDecodedToken =this.sercurityUtil.checkValidRefreshToken(refreshToken);
        String email = jwtDecodedToken.getSubject();

        User user = this.userService.getUserByRefreshTokenAndEmail(refreshToken,email);
        if(user == null) {
            throw  new IdInvalidException("User không hợp lệ");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUser = this.userService.handleGetUserByUserName(user.getEmail());
        if(currentUser !=null) {
            ResLoginDTO.UserLogin userLogin =new ResLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getEmail(),
                    currentUser.getName(),
                    currentUser.getRole()
            );
                    res.setUser(userLogin);

        }
        String accessToken = this.sercurityUtil.createAccessToken(email,res);
        res.setAccessToken(accessToken);

        String newRefreshToken = this.sercurityUtil.createRefreshToken(email,res);
        this.userService.updateUserToken(newRefreshToken,email);

        //set cookie
        ResponseCookie resCookie = ResponseCookie
                .from("refresh_token",newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,resCookie.toString()).body(res);

    }

    @PostMapping("/logout")
    @ApiMessage("logout")
    public ResponseEntity<Void> logout() throws IdInvalidException {
//        Lấy email để xác thực,sau đó update refresh token bên db lẫn cookie
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        if(email == "") throw new IdInvalidException("Access token khng hợp lệ");

        this.userService.updateUserToken(null,email);
        //set cookie
        ResponseCookie deletedCookie = ResponseCookie
                .from("refresh_token",null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,deletedCookie.toString()).body(null);
    }
}
