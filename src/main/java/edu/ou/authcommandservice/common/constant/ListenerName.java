package edu.ou.authcommandservice.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ListenerName {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Account {
        public static final String ADD = "accountAddListener";
        public static final String DELETE = "accountDeleteListener";
    }
}
