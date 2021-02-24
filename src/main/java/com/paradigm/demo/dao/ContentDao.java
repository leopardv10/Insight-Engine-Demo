package com.paradigm.demo.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;


@Component
@Mapper
public interface ContentDao {
	@Insert("insert into content(task_id, file_name, status, create_time, last_update) " +
			"values(#{task_id}, #{file_name}, #{status}, now(), now())")
	void init(@Param("task_id") int task_id, @Param("file_name") String file_name, @Param("status") String status);

	@Update("update content set text=#{text}, last_update=now()")
	void ocrUpdate(@Param("text") String content);

	@Update("update content set last_update=now(), status=#{status} where task_id=#{task_id} and file_name=#{file_name}")
	void finish(@Param("task_id") int task_id, @Param("file_name") String file_name, @Param("status") String status);

	@Select("select text from content where task_id=#{task_id} and file_name=#{file_name}")
	String getText(@Param("task_id") int task_id, @Param("file_name") String file_name);

	@Delete("delete from content where task_id=#{task_id}")
	void delete(@Param("task_id") int task_id);
}
