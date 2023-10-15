package com.kana.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/***
 * SpringSecurity框架会使用这个接口的方法获取用户名与密码、权限等等
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUser implements UserDetails {
    private User user;
    private List<String> permissions;

    /***
     * SpringSecurity框架会使用这个接口的方法获取权限信息
     * 重写以给框架提供数据库中查询到的权限信息,也可以不写使用自定义的权限验证信息
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /***
     * SpringSecurity框架会使用这个接口的方法获取用户名与密码
     * 重写以给框架提供数据库中查询到的用户信息
     * @return
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /***
     * 全部改为true方便测试
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
