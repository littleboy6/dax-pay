package cn.daxpay.single.service.core.record.adjust.dao;

import cn.daxpay.single.service.core.record.adjust.entity.TradeAdjustRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易调整记录
 * @author xxm
 * @since 2024/7/15
 */
@Mapper
public interface TradeAdjustRecordMapper extends BaseMapper<TradeAdjustRecord> {
}
