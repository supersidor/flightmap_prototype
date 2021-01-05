package com.supersidor.flightmap.config.oauth;

import com.supersidor.flightmap.exception.OAuth2AuthenticationProcessingException;
import com.supersidor.flightmap.model.AuthProvider;
import com.supersidor.flightmap.model.User;
import com.supersidor.flightmap.repository.UserReactiveRepository;
import com.supersidor.flightmap.security.UserPrincipal;
import com.supersidor.flightmap.security.oauth2.user.OAuth2UserInfo;
import com.supersidor.flightmap.security.oauth2.user.OAuth2UserInfoFactory;
import com.supersidor.flightmap.service.SequenceGeneratorService;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class CustomReactiveOAuth2UserService extends DefaultReactiveOAuth2UserService {
    private UserReactiveRepository userRepository;
    private SequenceGeneratorService sequenceGeneratorService;

    CustomReactiveOAuth2UserService(UserReactiveRepository userRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.userRepository = userRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {
        return super.loadUser(userRequest)
                .flatMap(user -> processOAuth2User(userRequest, user));
    }

    private Mono<OAuth2User> processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        String email = oAuth2UserInfo.getEmail();
        if (StringUtils.isEmpty(email)) {
            return Mono.error(new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider"));
        }
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                        return Mono.error(new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                                user.getProvider() + " account. Please use your " + user.getProvider() +
                                " account to login."));
                    }
                    return updateExistingUser(user, oAuth2UserInfo);
                })
                .switchIfEmpty(registerNewUser(oAuth2UserRequest, oAuth2UserInfo))
                .map(user -> UserPrincipal.create(user, oAuth2User.getAttributes()));

//        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
//        User user;
//        if(userOptional.isPresent()) {
//            user = userOptional.get();
//            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
//                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
//                        user.getProvider() + " account. Please use your " + user.getProvider() +
//                        " account to login.");
//            }
//            user = updateExistingUser(user, oAuth2UserInfo);
//        } else {
//            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
//        }

//        User user = new User();
//        user.setId(101L);
//        user.setEmail("sidorka@gmail.com");
//        user.setImageUrl("https://kor.ill.in.ua/m/190x120/2576722.jpg");
//        user.setEmailVerified(true);
//        user.setName("andriy");
//        user.setProvider(AuthProvider.github);
//        return Mono.just(UserPrincipal.create(user, oAuth2User.getAttributes()));
    }

    private Mono<User> updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setName(oAuth2UserInfo.getName());
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

    private Mono<User> registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        return sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME).flatMap(newUserId -> {
            User user = new User();
            user.setId(newUserId);
            user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
            user.setProviderId(oAuth2UserInfo.getId());
            user.setName(oAuth2UserInfo.getName());
            user.setEmail(oAuth2UserInfo.getEmail());
            user.setImageUrl(oAuth2UserInfo.getImageUrl());
            return userRepository.save(user);
        });


    }


}
