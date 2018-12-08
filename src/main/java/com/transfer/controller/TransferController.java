package com.transfer.controller;

import com.transfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController {
    @Autowired
    TransferService transferService;

    @RequestMapping("startTransfer")
    public String startTransferData() {
        transferService.startTransfer();
        return "";
    }
}
