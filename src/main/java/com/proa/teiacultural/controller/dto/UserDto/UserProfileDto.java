package com.proa.teiacultural.controller.dto.UserDto;

public record UserProfileDto(
    String username,
    String ProfilePicture,
    String email,
    String telephone,
    String professionalName,
    String category,
    String aboutMe,
    String socialMedia,
    String localization
) {
}