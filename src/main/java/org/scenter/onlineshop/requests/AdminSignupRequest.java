package org.scenter.onlineshop.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignupRequest extends SignupRequest{
    @NotBlank
    private String role;

    public AdminSignupRequest(@NotBlank @Size(min = 1, max = 20) String name,
                              @NotBlank @Size(min = 1, max = 20) String surname,
                              @NotBlank @Size(max = 50) @Email String email,
                              @NotBlank @Size(min = 6, max = 40) String password,
                              String role) {
        super(name, surname, email, password);
        this.role = role;
    }
}
