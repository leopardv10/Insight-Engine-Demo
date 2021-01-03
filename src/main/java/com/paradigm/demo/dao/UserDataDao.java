package com.paradigm.demo.dao;


import com.paradigm.demo.bean.UserData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;


@Component
@Mapper
public interface UserDataDao {
	@Select("select spo from demo where id = #{id}")
	String selectById(@Param("id") int id);

	@Insert("insert into demo(file_name, content, spo) values(#{file_name}, #{content}, #{spo})")
	void insertData(@Param("file_name") String file_name, @Param("content") String content, @Param("spo") String spo);

	@Delete("delete from demo where id = #{id}")
	void delete(@Param("id") int id);
}
