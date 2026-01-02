package com.example.netapp.entity;



import jakarta.persistence.Id;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "users")
public class UserEntity implements UserDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;
	@Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String username;
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    // this makes the customer as the default value for the role
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;
    
    
    
    
    
    
    
    
    
    
    
    
    
    public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
	
	
	
	@JsonIgnore
	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	System.out.println("inside the getAuthorities function from the UserEntity");
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    } 
   /*
    * These methods are hard coded to spring security , and there functions with these values needs to be like this . 
    */
    @JsonIgnore
	@Override public boolean isAccountNonExpired() { return true; }
    @JsonIgnore
    @Override public boolean isAccountNonLocked() { return true; }
    @JsonIgnore
    @Override public boolean isCredentialsNonExpired() { return true; }
    @JsonIgnore
    @Override public boolean isEnabled() { return true; } 
    
    
    
    
}
