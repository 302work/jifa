//查看详情
// @Bind #orderDetailBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",true);
    saveOrderBtn.set("visible",false);
    orderDetailDialog.show();
};

//编辑订单
// @Bind #editOrderBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",false);
    //设置某些字段不可编辑
    view.get("^readOnly").set("readOnly",true);
    saveOrderBtn.set("visible",true);
    orderDetailDialog.show();
};

//审核订单
// @Bind #auditOrderBtn.onClick
!function(self,arg,auditOrderDialog) {
    auditOrderDialog.show();
};

//审核通过订单
// @Bind #auditOrderPassBtn.onClick
!function(self,arg,auditOrderDialog,auditOrderAjaxAction,auditOrderAutoForm) {
    audit(1,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm);
};

//审核不通过订单
// @Bind #auditOrderUnPassBtn.onClick
!function(self,arg,auditOrderDialog,auditOrderAjaxAction,auditOrderAutoForm) {
    audit(2,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm);
};

//审核订单
function audit(status,auditOrderAjaxAction,auditOrderDialog,auditOrderAutoForm){
    //当前任务号
    var taskId = "${request.getParameter('taskId')}";
    //备注
    var remark = auditOrderAutoForm.get("entity").get("remark");
    if(!remark || remark==""){
        remark = "无";
    }
    auditOrderAjaxAction.set("parameter",
        {
            status:status,
            taskIds:taskId,
            remark:remark
        }
    ).execute(function(){
        auditOrderDialog.hide();
    });
}
//取消
// @Bind #closeAuditOrderDialog.onClick
!function(self,arg,auditOrderDialog) {
    auditOrderDialog.hide();
};

//保存订单按钮
// @Bind #saveOrderBtn.onClick
!function(self,arg,orderDetailDialog,saveOrderUpdateAction) {
    saveOrderUpdateAction.execute(function(){
        orderDetailDialog.hide();
    });
};

//关闭
// @Bind #closeOrderDetailBtn.onClick
!function(self,arg,orderDetailDialog) {
    orderDetailDialog.hide();
};

//首次加载订单
// @Bind #dsOrder.onReady
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//行点击
// @Bind #orderDataGrid.onDataRowClick
!function(self,arg,dsOrder,dsOrderProject,dsOrderRecord) {
    var currOrder = dsOrder.getData("#");
    if(currOrder){
        dsOrderProject.set("parameter",currOrder.get("id")).flushAsync();
        dsOrderRecord.set("parameter",currOrder.get("id")).flushAsync();
    }
};

//记录的检测结果
// @Bind #recordResultBtn.onClick
!function(self,arg,resultDialog,dsOrderRecord,resultIFrame,picDataGrid,dsRecordTestCondition,dsDevice) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        //
        var recordId = currRecord.get("id");
        var path = resultIFrame.get("path");
        var index = path.indexOf("?");
        if(index!=-1){
            path = path.substring(0,index);
        }
        path += "?recordId="+recordId;
        resultIFrame.set("path",path);

        //显示图片
        //原样
        var samplePic = currRecord.get("samplePic");
        //测试样
        var testSamplePic = currRecord.get("testSamplePic");
        var data = [{
            samplePic:samplePic,
            testSamplePic:testSamplePic
        }];
        picDataGrid.set("items",data);
        dsRecordTestCondition.set("parameter",recordId).flushAsync();
        dsDevice.set("parameter",recordId).flushAsync();
        resultDialog.show();
    }
};

//关闭检测结果弹窗
// @Bind #closeResultDialogBtn.onClick
!function(self,arg,resultDialog) {
    resultDialog.hide();
};

// @Bind #picDataGrid.#samplePic.onRenderCell
!function(arg) {
    var samplePic = arg.data.samplePic;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
};

// @Bind #picDataGrid.#testSamplePic.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.testSamplePic;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
};

//监听样品编号输入框的按键事件
// @Bind #sampleNoInput.onKeyDown
!function(self,arg,inputTestDataDialog,dsRecordTestCondition,dsOrderRecord,addResultIFrame,dsDevice,samplePicDataGrid) {
    //如果是回车
    if(arg.keyCode==13){
        var sampleNoInput = $("#d_sampleNoInput input").val();
        if(sampleNoInput){
            var entityList = dsOrderRecord.getData();
            var currRecord;
            entityList.each(function(entity){
                if(entity.get("sampleNo")==sampleNoInput){
                    currRecord = entity;
                    return false;
                }
            });
            if(currRecord){
                setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid);
                inputTestDataDialog.show();
            }else{
                dorado.MessageBox.alert("样品编号不存在");
            }
        }
    }
};

//检测数据录入
// @Bind #inputTestDataBtn.onClick
!function(self,arg,dsOrderRecord,addResultIFrame,inputTestDataDialog,dsRecordTestCondition,dsDevice,samplePicDataGrid) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord) {
        setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid);
        inputTestDataDialog.show();
    }

}

//设置iFrame的recordId
function setAddResultIFrameParam(currRecord,addResultIFrame,dsRecordTestCondition,dsDevice,samplePicDataGrid){
    var path = addResultIFrame.get("path");
    var index = path.indexOf("?");
    if (index != -1) {
        path = path.substring(0, index);
    }
    var recordId = currRecord.get("id");
    path += "?recordId=" + recordId;
    addResultIFrame.set("path", path);
    dsRecordTestCondition.set("parameter",recordId).flushAsync();
    dsDevice.set("parameter",{recordId:recordId}).flushAsync();
    if(currRecord){
        //显示图片
        //原样
        var samplePic = currRecord.get("samplePic");
        //测试样
        var testSamplePic = currRecord.get("testSamplePic");
        var data = [{
            samplePic2:samplePic,
            testSamplePic2:testSamplePic
        }];
        samplePicDataGrid.set("items",data);
    }
}
// @Bind #samplePicDataGrid.#samplePic2.onRenderCell
!function(arg) {
    var samplePic = arg.data.samplePic2;
    $(arg.dom).empty();
    if(samplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: samplePic,
            width:350
        });
    }
};

// @Bind #samplePicDataGrid.#testSamplePic2.onRenderCell
!function(arg) {
    var testSamplePic = arg.data.testSamplePic2;
    $(arg.dom).empty();
    if(testSamplePic){
        $(arg.dom).empty().xCreate({
            tagName: "IMG",
            src: testSamplePic,
            width:350
        });
    }
};
//关闭检测数据录入弹窗
// @Bind #closeInputTestDataDialogBtn.onClick
!function(self,arg,inputTestDataDialog) {
    inputTestDataDialog.hide();
}

//保存检测条件之前，设置recordId
// @Bind #saveTestConditionUpdateAction.beforeExecute
!function(self,arg,dsOrderRecord) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord){
        self.set("parameter",currRecord.get("id"));
    }
}

//保存成功刷新检测条件
// @Bind #saveTestConditionUpdateAction.onSuccess
!function(self,arg,dsRecordTestCondition) {
    dsRecordTestCondition.flushAsync();
}

//添加检测仪器
// @Bind #addDeviceBtn.onClick
!function(selectDeviceDialog) {
    selectDeviceDialog.show();
}

//关闭选择检测仪器弹窗
// @Bind #closeSelectDeviceDialogBtn.onClick
!function(selectDeviceDialog) {
    selectDeviceDialog.hide();
}

//选择检测仪器
// @Bind #saveDeviceBtn.onClick
!function(deviceSubView,dsOrderRecord,selectDeviceDialog,addDeviceAjaxAction,dsDevice) {
    var subView = deviceSubView.get("subView");
    var currDevice = subView.id("dsDevice").getData("#");
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    if(currDevice){
        var status = currDevice.get("status");
        if(status==2){
            dorado.MessageBox.alert("该仪器已停用,无法使用");
            return false;
        }
        var deviceId = currDevice.get("id");
        var recordId = currRecord.get("id");
        if(deviceId && recordId){
            addDeviceAjaxAction.set("parameter",{recordId:recordId,deviceId:deviceId}).execute(function(){
                dsDevice.flushAsync();
                selectDeviceDialog.hide();
            });
        }
    }

}

//删除检测仪器
// @Bind #delDeviceBtn.onClick
!function(dsOrderRecord,dsDevice,delDeviceAjaxAction) {
    var currDevice = dsDevice.getData("#");
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    if(currDevice){
        var deviceId = currDevice.get("id");
        var recordId = currRecord.get("id");
        if(deviceId && recordId){
            delDeviceAjaxAction.set("parameter",{recordId:recordId,deviceId:deviceId}).execute(function(){
                dsDevice.flushAsync();
            });
        }
    }

}

//上传原样照片前设置记录id
// @Bind #samplePicUploadAction.beforeFileUpload
!function(self,dsOrderRecord){
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    var recordId = currRecord.get("id");
    //动态设置参数
    self.set("parameter", {
        recordId: recordId,
        type:1
    });
};

//上传测试样照片前设置记录id
// @Bind #testSamplePicUploadAction.beforeFileUpload
!function(self,dsOrderRecord){
    var currRecord = dsOrderRecord.getData("#");
    if(!currRecord){
        return false;
    }
    var recordId = currRecord.get("id");
    //动态设置参数
    self.set("parameter", {
        recordId: recordId,
        type:2
    });
};

//上传原样图片
// @Bind #samplePicUploadAction.onFileUploaded
!function(arg,samplePicDataGrid,dsOrderRecord) {
    var returnValue = arg.returnValue;//获取FileResolver方法返回的信息
    var items = samplePicDataGrid.get("items");
    var data = [{
        samplePic2:returnValue,
        testSamplePic2:items[0].testSamplePic2
    }];
    samplePicDataGrid.set("items",data);
    dsOrderRecord.getData("#").set("samplePic",returnValue);
};

//上传测试样图片
// @Bind #testSamplePicUploadAction.onFileUploaded
!function(arg,samplePicDataGrid,dsOrderRecord) {
    var returnValue = arg.returnValue;//获取FileResolver方法返回的信息
    var items = samplePicDataGrid.get("items");
    var data = [{
        samplePic2:items[0].samplePic2,
        testSamplePic2:returnValue
    }];
    samplePicDataGrid.set("items",data);
    dsOrderRecord.getData("#").set("testSamplePic",returnValue);
};


//生成报告
// @Bind #createReportBtn.onClick
!function() {


};