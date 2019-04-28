package com.springapi.springapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springapi.springapi.SpringApplicationContext;
import com.springapi.springapi.model.request.UserDetailsRequestModel;
import com.springapi.springapi.service.UserService;
import com.springapi.springapi.shared.dto.UserDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {




    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            UserDetailsRequestModel creds=new ObjectMapper()
                    .readValue(request.getInputStream(), UserDetailsRequestModel.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );


        }
        catch (IOException e){
            throw new RuntimeException(e);

        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String username=((User)auth.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+SecurityConstans.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512,SecurityConstans.getTokenSecret())
                .compact();
        UserService userService= (UserService)SpringApplicationContext.getBean("userServiceImpl");
        UserDto userDto=userService.getUser(username);
        response.addHeader(SecurityConstans.HEADER_STRING,SecurityConstans.TOKEN_PREFIX+token);
        response.addHeader("userId",userDto.getUserId());
    }
}
