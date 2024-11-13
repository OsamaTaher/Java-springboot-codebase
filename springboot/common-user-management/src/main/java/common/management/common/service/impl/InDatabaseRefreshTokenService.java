package common.management.common.service.impl;

import common.management.common.model.RefreshToken;
import common.management.common.properties.ExternalConfig;
import common.management.common.repository.RefreshTokenRepository;
import common.management.common.repository.UserRepository;
import common.management.common.service.RefreshTokenService;
import common.management.common.util.OpWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static common.management.common.util.OperationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class InDatabaseRefreshTokenService implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ExternalConfig externalConfig;

    @Override
    public OpWrapper<String> createRefreshToken(String userId){
        RefreshToken token;
        var refreshToken = refreshTokenRepository.findByUserId(userId);
        if(refreshToken.isPresent()){
            token = refreshToken.get().update(Instant.now().plusMillis(externalConfig.getRefreshTokenExpirationMs()));
        }else{
           var user =  userRepository.findById(userId);
            if(user.isEmpty()){
                return new OpWrapper<>(OP_STATUS_USER_NOT_FOUND,null);
            }
            token = new RefreshToken(Instant.now().plusMillis(externalConfig.getRefreshTokenExpirationMs()), user.get());
        }
       return new OpWrapper<>(OP_STATUS_SUCCESS,token.getRefreshToken());
    }

    @Override
    public OpWrapper<RefreshToken> isRefreshTokenValid(String token){
        var refreshToken= refreshTokenRepository.findByRefreshToken(token);
        if(refreshToken.isEmpty()) {
            return new OpWrapper<>(OP_STATUS_REFRESH_TOKEN_INVALID, null);
        }
        if(isRefreshTokenExpired(refreshToken.get())){
            return new OpWrapper<>(OP_STATUS_REFRESH_TOKEN_INVALID,null);
        }
        return new OpWrapper<>(OP_STATUS_SUCCESS,refreshToken.get());
    }

    private boolean isRefreshTokenExpired(RefreshToken refreshToken){
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return true;
        }
        return false;
    }
}
