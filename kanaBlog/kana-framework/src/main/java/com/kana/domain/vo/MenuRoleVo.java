package com.kana.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuRoleVo {

    private List<MenuRoleVo> children;
    private Long id;
    private String label;
    private Long parentId;
}
