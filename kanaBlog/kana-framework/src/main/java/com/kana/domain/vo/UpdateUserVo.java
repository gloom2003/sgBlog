package com.kana.domain.vo;

import com.kana.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserVo {
    private List<Long> roleIds;
    private List<UserRoleVo> roles;
    private User user;
}
