package com.ambrygen.adx.dto.security;

import java.util.Set;

public record SignupRequest(String emailAddress,
                           String password,
                           String firstName,
                           String lastName,
                           Set<String> roles) { }
