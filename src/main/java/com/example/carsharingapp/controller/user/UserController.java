package com.example.carsharingapp.controller.user;

import com.example.carsharingapp.dto.user.UpdateRoleRequestDto;
import com.example.carsharingapp.dto.user.UserRegistrationRequestDto;
import com.example.carsharingapp.dto.user.UserResponseDto;
import com.example.carsharingapp.model.user.User;
import com.example.carsharingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update a role for user", description = "Change an existing "
            + "role for user (manager only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role was changed  successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Role was not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}/role")
    public UserResponseDto updateRole(@PathVariable Long id,
                                      @Valid @RequestBody UpdateRoleRequestDto requestDto) {
        return userService.updateRoleForUser(id, requestDto);
    }

    @Operation(summary = "Get info about profile", description = "Get all "
            + "information about users profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all information about profile"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User was not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public UserResponseDto getAllInfo(@AuthenticationPrincipal User user) {
        return userService.getAllInfo(user.getId());
    }

    @Operation(summary = "Update info about profile", description = "Update "
            + "information about user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information was "
                    + "changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User was not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/me")
    public UserResponseDto updateInfoAboutProfile(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody
                                                  UserRegistrationRequestDto requestDto) {
        return userService.updateInformationAboutUser(user.getId(), requestDto);
    }
}
