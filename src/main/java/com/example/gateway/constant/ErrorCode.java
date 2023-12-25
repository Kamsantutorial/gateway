package com.example.gateway.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Chuob Bunthoeurn
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    E401("Unauthorized"),
    E001("Generate Exception"),
    E002("Security Exception"),
    E003("User Not Found"),
    E004("Incorrect username or password"),
    E005("Record Not Found");

    private final String desc;
}
