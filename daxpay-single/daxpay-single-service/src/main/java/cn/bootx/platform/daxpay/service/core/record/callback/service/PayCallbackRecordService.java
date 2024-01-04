package cn.bootx.platform.daxpay.service.core.record.callback.service;

import cn.bootx.platform.common.core.exception.DataNotExistException;
import cn.bootx.platform.daxpay.service.core.record.callback.dao.PayCallbackRecordManager;
import cn.bootx.platform.daxpay.service.core.record.callback.entity.PayCallbackRecord;
import cn.bootx.platform.daxpay.service.dto.record.callback.PayCallbackRecordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 接收到的支付回调通知
 * @author xxm
 * @since 2023/12/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayCallbackRecordService {

    private final PayCallbackRecordManager callbackRecordManager;

    /**
     * 根据id查询
     */
    public PayCallbackRecordDto findById(Long id) {
        return callbackRecordManager.findById(id).map(PayCallbackRecord::toDto).orElseThrow(DataNotExistException::new);
    }
}