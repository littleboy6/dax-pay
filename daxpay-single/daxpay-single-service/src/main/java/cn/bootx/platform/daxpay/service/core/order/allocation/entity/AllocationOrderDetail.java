package cn.bootx.platform.daxpay.service.core.order.allocation.entity;

import cn.bootx.platform.common.core.function.EntityBaseFunction;
import cn.bootx.platform.common.mybatisplus.base.MpBaseEntity;
import cn.bootx.platform.daxpay.code.AllocationReceiverTypeEnum;
import cn.bootx.platform.daxpay.service.core.order.allocation.convert.AllocationConvert;
import cn.bootx.platform.daxpay.service.dto.order.allocation.AllocationOrderDetailDto;
import cn.bootx.platform.starter.data.perm.sensitive.SensitiveInfo;
import cn.bootx.table.modify.annotation.DbColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分账订单明细
 * @author xxm
 * @since 2024/4/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class AllocationOrderDetail extends MpBaseEntity implements EntityBaseFunction<AllocationOrderDetailDto> {

    /** 分账订单ID */
    @DbColumn(comment = "分账订单ID")
    private Long orderId;

    /** 接收者ID */
    @DbColumn(comment = "接收者ID")
    private Long receiverId;

    /** 分账比例 */
    @DbColumn(comment = "分账比例(万分之多少)")
    private Integer rate;

    /** 分账金额 */
    @DbColumn(comment = "分账金额")
    private Integer amount;

    /**
     * 分账接收方类型
     * @see AllocationReceiverTypeEnum
     */
    @DbColumn(comment = "分账接收方类型")
    private String receiverType;

    /** 接收方账号 */
    @DbColumn(comment = "接收方账号")
    @SensitiveInfo
    private String receiverAccount;

    /** 接收方姓名 */
    @DbColumn(comment = "接收方姓名")
    private String receiverName;

    /** 状态 */
    @DbColumn(comment = "状态")
    private String status;

    /**
     * 转换
     */
    @Override
    public AllocationOrderDetailDto toDto() {
        return AllocationConvert.CONVERT.convert(this);
    }
}
