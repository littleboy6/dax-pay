package cn.bootx.platform.daxpay.service.core.channel.voucher.entity;

import cn.bootx.platform.common.core.function.EntityBaseFunction;
import cn.bootx.platform.common.mybatisplus.base.MpBaseEntity;
import cn.bootx.platform.daxpay.service.code.VoucherCode;
import cn.bootx.platform.daxpay.service.core.channel.voucher.convert.VoucherConvert;
import cn.bootx.platform.daxpay.service.dto.channel.voucher.VoucherLogDto;
import cn.bootx.table.modify.annotation.DbColumn;
import cn.bootx.table.modify.annotation.DbTable;
import cn.bootx.table.modify.mysql.annotation.DbMySqlIndex;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 储值卡日志
 *
 * @author xxm
 * @since 2022/3/17
 */
@EqualsAndHashCode(callSuper = true)
@Data
@DbTable(comment = "储值卡日志")
@Accessors(chain = true)
@TableName("pay_voucher_log")
public class VoucherLog extends MpBaseEntity implements EntityBaseFunction<VoucherLogDto> {

    /** 储值卡id */
    @DbMySqlIndex(comment = "储值卡ID")
    @DbColumn(comment = "储值卡id")
    private Long voucherId;

    /** 储值卡号 */
    @DbColumn(comment = "储值卡号")
    private String voucherNo;

    /** 金额 */
    @DbColumn(comment = "金额")
    private Integer amount;

    /**
     * 类型
     * @see VoucherCode#LOG_PAY
     */
    @DbColumn(comment = "类型")
    private String type;

    /** 交易记录ID */
    @DbColumn(comment = "交易记录ID")
    private Long paymentId;

    /** 业务ID */
    @DbColumn(comment = "业务ID")
    private String businessId;

    /** 备注 */
    @DbColumn(comment = "备注")
    private String remark;

    /**
     * 转换
     */
    @Override
    public VoucherLogDto toDto() {
        return VoucherConvert.CONVERT.convert(this);
    }
}