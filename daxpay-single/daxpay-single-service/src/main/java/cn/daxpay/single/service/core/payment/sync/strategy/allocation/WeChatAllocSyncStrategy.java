package cn.daxpay.single.service.core.payment.sync.strategy.allocation;

import cn.daxpay.single.core.code.PayChannelEnum;
import cn.daxpay.single.service.core.channel.wechat.entity.WeChatPayConfig;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayAllocService;
import cn.daxpay.single.service.core.channel.wechat.service.WeChatPayConfigService;
import cn.daxpay.single.service.core.payment.sync.result.AllocRemoteSyncResult;
import cn.daxpay.single.service.func.AbsAllocSyncStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * 微信分账同步策略
 * @author xxm
 * @since 2024/7/16
 */
@Scope(SCOPE_PROTOTYPE)
@Component
@RequiredArgsConstructor
public class WeChatAllocSyncStrategy extends AbsAllocSyncStrategy {

    private final WeChatPayAllocService weChatPayAllocService;
    private final WeChatPayConfigService weChatPayConfigService;

    @Override
    public String getChannel() {
        return PayChannelEnum.WECHAT.getCode();
    }

    /**
     * 同步状态
     */
    @Override
    public AllocRemoteSyncResult doSync() {
        WeChatPayConfig config = weChatPayConfigService.getConfig();
        return weChatPayAllocService.sync(this.getAllocOrder(),config);
    }
}
