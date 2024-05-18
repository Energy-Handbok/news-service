package com.khaphp.common.entity;

import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserSystem {
    private String id;
    private String name;
    private String username;
    private String password;
    private String email;
    private Date birthday;
    private String imgUrl;
    private String gender;
    private String status;
    private String role;
    private boolean premium;

    public static UserSystem linkedHashMapToEntity(LinkedHashMap<String, Object> linkedHashMap) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return UserSystem.builder()
                .id((String) linkedHashMap.get("id"))
                .name((String) linkedHashMap.get("name"))
                .username((String) linkedHashMap.get("username"))
                .password((String) linkedHashMap.get("password"))
                .email((String) linkedHashMap.get("email"))
                .birthday(simpleDateFormat.parse((String) linkedHashMap.get("birthday")))
                .imgUrl((String) linkedHashMap.get("imgUrl"))
                .gender((String) linkedHashMap.get("gender"))
                .status((String) linkedHashMap.get("status"))
                .role((String) linkedHashMap.get("role"))
                .premium((boolean) linkedHashMap.get("premium"))
                .build();
    }
}
