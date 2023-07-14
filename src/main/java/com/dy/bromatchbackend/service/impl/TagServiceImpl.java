package com.dy.bromatchbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.bromatchbackend.model.domain.Tag;
import com.dy.bromatchbackend.service.TagService;
import com.dy.bromatchbackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author LEGION
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-07-12 20:13:35
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




