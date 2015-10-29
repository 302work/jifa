//查看详情
// @Bind #orderDetailBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",true);
    saveOrderBtn.set("visible",false);
    orderDetailDialog.show();
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
!function(self,arg,resultDialog,dsOrderRecord,resultIFrame,picDataGrid,dsRecordTestCondition) {
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
}

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
}


//编辑
// @Bind #editOrderBtn.onClick
!function(self,arg,orderDetailDialog,orderDetailAutoForm,saveOrderBtn) {
    orderDetailAutoForm.set("readOnly",false);
    //设置某些字段不可编辑
    view.get("^readOnly").set("readOnly",true);
    saveOrderBtn.set("visible",true);
    orderDetailDialog.show();
};

//监听样品编号输入框的按键事件
// @Bind #sampleNoInput.onKeyDown
!function(self,arg,inputTestDataDialog,dsRecordTestCondition,dsOrderRecord,addResultIFrame) {
    //如果是回车
    if(arg.keyCode==13){
        var sampleNoInput = $("#d_sampleNoInput input").val();
        if(sampleNoInput){
            //根据样品编号查找recordId
            var recordId = 9;
            setAddResultIFrameParam(recordId,addResultIFrame,dsRecordTestCondition);
            inputTestDataDialog.show();
        }
    }
};

//检测数据录入
// @Bind #inputTestDataBtn.onClick
!function(self,arg,dsOrderRecord,addResultIFrame,inputTestDataDialog,dsRecordTestCondition) {
    var currRecord = dsOrderRecord.getData("#");
    if(currRecord) {
        var recordId = currRecord.get("id");
        setAddResultIFrameParam(recordId,addResultIFrame,dsRecordTestCondition);
        inputTestDataDialog.show();
    }

}

//设置iframe的recordId
function setAddResultIFrameParam(recordId,addResultIFrame,dsRecordTestCondition){
    var path = addResultIFrame.get("path");
    var index = path.indexOf("?");
    if (index != -1) {
        path = path.substring(0, index);
    }
    path += "?recordId=" + recordId;
    addResultIFrame.set("path", path);
    dsRecordTestCondition.set("parameter",recordId).flushAsync();
}
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

