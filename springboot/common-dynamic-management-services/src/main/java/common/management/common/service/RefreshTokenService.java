package common.management.common.service;

import common.management.common.model.RefreshToken;
import common.management.common.util.OpWrapper;

public interface RefreshTokenService {
    OpWrapper<String> createRefreshToken(String userId);

    OpWrapper<RefreshToken> isRefreshTokenValid(String token);
}
