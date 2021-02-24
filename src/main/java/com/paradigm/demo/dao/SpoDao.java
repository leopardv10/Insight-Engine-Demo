package com.paradigm.demo.dao;


import com.paradigm.demo.bean.Result;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import java.util.List;


@Component
@Mapper
public interface SpoDao {
	@Insert("insert into spo (task_id, file_name, predicate, subject, object, sub_begin, sub_end, obj_begin, obj_end)" +
			"values(#{task_id}, #{file_name}, #{predicate}, #{subject}, #{object}, #{sub_begin}, #{sub_end}, #{obj_begin}, #{obj_end})")
	void insert(@Param("task_id") int task_id, @Param("file_name") String file_name, @Param("predicate") String predicate,
	            @Param("subject") String subject, @Param("object") String object, @Param("sub_begin") int sub_begin,
	            @Param("sub_end") int sub_end, @Param("obj_begin") int obj_begin, @Param("obj_end") int obj_end);

	@Delete("delete from spo where task_id=#{task_id}")
	void delete(@Param("task_id") int task_id);

	@Select("select predicate, subject, object from spo where task_id=#{task_id} and file_name=#{file_name}")
	List<Result> select(@Param("task_id") int task_id, @Param("file_name") String file_name);
}
