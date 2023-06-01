package edu.ou.authcommandservice.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EndPoint {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Password {
        public static final String BASE = "/password";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class AccountSetting {
        public static final String BASE = "/account-setting";
        public static final String MULTIPLE = "/multiple";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Role {
        public static final String BASE = "/role";
    }
}
