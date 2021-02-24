package com.paradigm.demo.dao;

import com.paradigm.demo.bean.Attribute;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;


@Mapper
@Component
public interface AttributeDao {
	@Insert("insert into attributes (task_id, file_name, attr, concept, value)" +
			"values(#{task_id}, #{file_name}, #{attr}, #{concept}, #{value})")
	void insert(@Param("task_id") int task_id, @Param("file_name") String file_name, @Param("attr") String attr,
	            @Param("concept") String concept, @Param("value") String value);

	@Delete("delete from attributes where task_id=#{task_id}")
	void delete(@Param("task_id") int task_id);

	@Select("select attr, concept, value from attributes where task_id=#{task_id} and file_name=#{file_name}")
	Attribute select(@Param("task_id") int task_id, @Param("file_name") String file_name);
}
