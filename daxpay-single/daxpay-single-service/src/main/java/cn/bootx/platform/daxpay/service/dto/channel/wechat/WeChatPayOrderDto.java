package cn.bootx.platform.daxpay.service.dto.channel.wechat;

import cn.bootx.platform.daxpay.service.dto.record.pay.PayOrderDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author xxm
 * @since 2021/6/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(title = "微信支付记录")
public class WeChatPayOrderDto extends PayOrderDto implements Serializable {

    private static final long serialVersionUID = -2400358210732595795L;

    @Schema(description = "微信交易号")
    private String tradeNo;

}