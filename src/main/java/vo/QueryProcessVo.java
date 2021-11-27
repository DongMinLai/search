package vo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

@Data
public class QueryProcessVo<T> {

    private QueryWrapper<T> queryWrapper;

    private IPage<T> ipage;

}
