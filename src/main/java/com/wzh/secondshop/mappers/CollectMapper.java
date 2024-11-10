package com.wzh.secondshop.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.wzh.secondshop.models.Collect;

public interface CollectMapper {
    @Select("select * from collect_table where good_id = #{0} and user_id = #{1};")
    Collect getCollect(Integer goodId, Integer userId);

    @Select("select * from collect_table where user_id = #{0};")
    List<Collect> getCollectByUserId(Integer userId);

    @Delete("delete from collect_table where id = #{0};")
    int deleteCollect(Integer collectId);

    @Insert("insert into collect_table(good_id, good_name, user_id) values(#{goodId}, #{goodName}, #{userId});")
    int insertCollect(Collect collect);
}
