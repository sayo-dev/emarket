package org.example.e_market.security;

import lombok.RequiredArgsConstructor;
import org.example.e_market.entity.User;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmailIgnoreCase(username).orElseThrow(() -> new CustomNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getAccountType().name())
                .build();
//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                getRole(user)
//        );
    }

//    private Collection<? extends GrantedAuthority> getRole(User user) {
//
//        var grantedAuthority = new SimpleGrantedAuthority(user.getAccountType().name());
//        return List.of(grantedAuthority);
//
//    }
}
