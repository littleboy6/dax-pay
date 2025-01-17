package cn.daxpay.single.service.core.payment.adjust.strategy.pay;

import cn.daxpay.single.core.code.PayChannelEnum;
import cn.daxpay.single.service.core.channel.wechat.entity.WeChatPayConfig;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayCloseService;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayConfigService;
import cn.daxpay.single.service.func.AbsPayAdjustStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 微信支付订单调整策略类
 * @author xxm
 * @since 2023/12/29
 */
@Slf4j
@Scope(SCOPE_PROTOTYPE)
@Service
@RequiredArgsConstructor
public class WeChatPayAdjustStrategy extends AbsPayAdjustStrategy {
    private final WeChatPayCloseService closeService;

    private final WeChatPayConfigService weChatPayConfigService;

    private WeChatPayConfig weChatPayConfig;

    /**
     * 策略标识
     */
    @Override
    public String getChannel() {
        return PayChannelEnum.WECHAT.getCode();
    }


    /**
     * 调整前处理
     */
    @Override
    public void doBeforeHandler() {
        this.weChatPayConfig = weChatPayConfigService.getConfig();
    }


    /**
     * 关闭本地支付和网关支付
     */
    @Override
    public void doCloseRemoteHandler() {
        closeService.close(this.getOrder(),this.weChatPayConfig);
    }
}
