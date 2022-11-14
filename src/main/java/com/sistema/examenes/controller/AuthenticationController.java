package com.sistema.examenes.controller;

import com.sistema.examenes.dto.AuthResponse;
import com.sistema.examenes.security.JwtUtils;
import com.sistema.examenes.dto.JwtRequest;
import com.sistema.examenes.dto.JwtResponse;
import com.sistema.examenes.model.Usuario;
import com.sistema.examenes.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin("*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/generate-token")
    public ResponseEntity<?> generarToken(@RequestBody JwtRequest jwtRequest) throws Exception{

        boolean auth = autenticar(jwtRequest.getUsername(),jwtRequest.getPassword());

        if(auth){
            UserDetails userDetails =  this.userDetailsService.loadUserByUsername(jwtRequest.getUsername());
            String token = this.jwtUtils.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(true, "Loged!", jwtRequest.getUsername(),token));


        } else {

            return new ResponseEntity<>(new AuthResponse(false,"User or Password Incorrect",null,null),HttpStatus.FORBIDDEN);

        }


    }

    private boolean autenticar(String username,String password) throws Exception {
        try{

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            return true;

        }catch (Exception exception){
            return false;
        }
    }

    @GetMapping("/actual-usuario")
    public Usuario obtenerUsuarioActual(Principal principal){
        return (Usuario) this.userDetailsService.loadUserByUsername(principal.getName());
    }
}
