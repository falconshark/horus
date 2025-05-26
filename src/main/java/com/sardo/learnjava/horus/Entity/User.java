package com.sardo.learnjava.horus.Entity;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // Getter,Setterが不要になる
@NoArgsConstructor  // デフォルトコンストラクターの自動生成
@AllArgsConstructor // 全フィールドに対する初期化値を引数に取るコンストラクタを自動生成
public class User {         // 主キーに当たるフィールドに付与する(今回はid)に付与
    @Id    
	private Integer id;
	private String username;
	private String password;
	private String fullName;
    private String phone;
    private String emailAddress;
}