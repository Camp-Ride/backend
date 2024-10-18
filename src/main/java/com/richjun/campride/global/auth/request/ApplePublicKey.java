package com.richjun.campride.global.auth.request;

public record ApplePublicKey(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e, String use) {
}
