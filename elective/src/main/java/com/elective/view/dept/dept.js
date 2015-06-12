var dirtyEntityNum = 0;


function refreshActions() {
	var node = view.get("#dataTreeAccount.currentNode");
	view.set({
		"^onCurrentNode.disabled" : node == null
	});
	var disabled = true;
	if (node != null) {
		var entity = node.get("data");
		disabled = !entity.isDirty();
	}
	view.set({
		"^onDirtyNode.disabled" : disabled,
		"^onDirty.disabled" : dirtyEntityNum == 0
	});
}

// @Bind @Dept.onStateChange
// @Bind @User.onStateChange
!function(arg) {
	if (arg.oldState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum++;
	} else if (arg.newState == dorado.Entity.STATE_NONE) {
		dirtyEntityNum--;
	} else if (arg.oldState == dorado.Entity.STATE_NEW
			&& arg.newState == dorado.Entity.STATE_DELETED) {
		dirtyEntityNum--;
	}
	refreshActions();
};

//@Bind @Account.onInsert
!function(arg) {
	var entityList = arg.entityList;
	var maxSortFlag = 0;
	entityList.each(function(entity) {
		var sortFlag = entity.get("sortFlag");
		if (sortFlag <= maxSortFlag) {
			maxSortFlag++;
			entity.set("sortFlag", maxSortFlag);
		} else {
			maxSortFlag = sortFlag;
		}
	});
};

//@Bind @Account.onRemove
!function(arg) {
	arg.entity.set("sortFlag", 0);
};


//@Bind #recordDataGrid.#name.onRenderFooterCell
!function(arg) {
	arg.dom.innerText = "支出合计：";
	
};
//@Bind #recordDataGrid.#type.onRenderFooterCell
!function(arg) {
	arg.dom.innerText = "收入合计：";
};
//@Bind #recordDataGrid.#remark.onRenderFooterCell
!function(arg) {
	arg.dom.innerText = "剩余资金：";
};

//@Bind #recordDataGrid.#money.onRenderFooterCell
!function(arg) {
	//支出合计
	arg.dom.innerText = window.zhichu;
};
//@Bind #recordDataGrid.#doDate.onRenderFooterCell
!function(arg) {
	//收入合计
	arg.dom.innerText = window.shouru;
};
//@Bind #recordDataGrid.#accountName.onRenderFooterCell
!function(arg) {
	//剩余资金
	arg.dom.innerText = window.shengyu;
};

//@Bind #dataTreeAccount.onDataRowClick
!function(dsAccounts,dataTreeAccount,recordDataPilot,recordDataGrid) {
	var currEntity = dsAccounts.getData("!CURRENT_ACCOUNT");
	var id = currEntity.get("id");
	if(id){
		//点击树重新加载数据
		currEntity.reset("records");
		//dsAccounts.flushAsync();
		//currEntity.reset("child");
	}
	var hasChild = currEntity.get("hasChild");
	if(hasChild){
		//判断如果有子分类则隐藏添加、删除、取消按钮，表格设置为只读
		//recordDataPilot.set("itemCodes","pages,pageSize");
		recordDataGrid.set("readOnly",true);
	}else{
		//recordDataPilot.set("itemCodes","pages,pageSize,+,-,x");
		recordDataGrid.set("readOnly",false);
	}
	view.id("ajaxActionSum").set("parameter", id).execute(function(data) {
		window.zhichu = dorado.util.Common.formatFloat(data.zhichu, "#,##0.00");
		window.shouru = dorado.util.Common.formatFloat(data.shouru, "#,##0.00");
		window.shengyu = dorado.util.Common.formatFloat(data.shengyu, "#,##0.00");
	});
};
// @Bind #dataTreeAccount.onContextMenu
!function(self, arg) {
	view.id("menuAccounts").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};
//@Bind #dataTreeAccount.onDataNodeCreate
!function(self, arg) {
	var entity = arg.data;
	if (entity.state == dorado.Entity.STATE_NEW) {
		setTimeout(function() {
			self.set("currentNode", arg.node);
		}, 50);
	}
};
//@Bind #dataTreeAccount.onDraggingSourceMove
!function(arg) {
	var draggingInfo = arg.draggingInfo;
	var sourceNode = draggingInfo.get("object");
	var sourceType = sourceNode.get("bindingConfig.name");

	var targetNode = draggingInfo.get("targetObject");
	if (targetNode) {
		var accept = false;
		if (sourceType == "Account") {
			accept = (targetNode.get("bindingConfig.name") == "Account");
		}
		draggingInfo.set("accept", accept);
	}
};

//@Bind #actionAdd.onExecute
!function(dsAccounts, dataTreeAccount) {
	var currentEntity = dsAccounts.getData("!CURRENT_ACCOUNT");	
	if (currentEntity) {
		var data = {
				name : "<新分类>",
				parentAccountId : currentEntity.get("id"),
				sortFlag:0
			};
//		currentEntity.createChild("child", data);
		//插入
		currentEntity.get("child").insert(data,"before");
	} else {
		//dsAccounts.getData().createChild({name:"<新分类>"});
		dsAccounts.getData().insert({name:"<新分类>",sortFlag:0},"before");
	}
	var currentNode = dataTreeAccount.get("currentNode");
	if (currentNode) {
		currentNode.expandAsync();
	}
	
};

//@Bind #actionRemove.onExecute
!function(dsAccounts) {
	var currentEntity = dsAccounts.getData("!CURRENT_ACCOUNT");
	if (currentEntity) {
	  view.id("ajaxActionCheckAccountChildren").set("parameter", currentEntity.get("id")).execute(function(count) {
	    if (count > 0) {
	      dorado.MessageBox.alert("请先删除子分类");
	    } else {
	      currentEntity.remove();        
	    }
	  });
	}
};

//@Bind #actionMove.onExecute
!function(dialogSelectAccount) {
	dialogSelectAccount.show();
};

// @Bind #actionCancel.onExecute
!function(dsAccounts) {
	var entity = dsAccounts.getData("!CURRENT_ACCOUNT");
	if (entity) {
		entity.cancel();
	}
};

// @Bind #actionSaveAll.onSuccess
!function() {
	dirtyEntityNum = 0;
	refreshActions();
};

// @Bind #dataTreeAccount.onCurrentChange
!function(self, arg) {
	view.id("cardbook").set("currentControl", 1);
	refreshActions();

};

//打印
// @Bind #printBtn.onClick
!function(self, arg) {
	$("#d_recordDataGrid").printArea();
};

//日期选择不能选择将来时间
//@Bind #dateDropDown1.onFilterDate
!function(self, arg) {
    if (arg.date>new Date()) {
        arg.selectable = false;
    }
};

//@Bind #buttonOk.onClick
!function(selectDataTreeAccount, dataTreeAccount, dialogSelectAccount) {
	//用户选择的分类
	var account = selectDataTreeAccount.get("currentNode.data");
	if (!account) {
		dorado.MessageBox.alert("请选择一个有效的分类。");
		return;
	}
	//要移动的分类
	var currentAccountNode = dataTreeAccount.get("currentNode");
	if(account == currentAccountNode.get("data")){
		dorado.MessageBox.alert("不能选择自己。");
		return;
	}
	//不能parent
	var parentCurrentAccountNode = currentAccountNode.get("parent");
	if(account == parentCurrentAccountNode.get("data")){
		dorado.MessageBox.alert("不能选择自己的父分类。");
		return;
	}
	var newAccount = dataTreeAccount.get("currentNode.data");
	newAccount = newAccount.clone();
	newAccount.set("sortFlag", 0);
	account.get("child").insert(newAccount,"before");
	currentAccountNode.remove();	
	dialogSelectAccount.hide();
};

// @Bind #buttonCancel.onClick
!function(dialogSelectAccount) {
	dialogSelectAccount.hide();
};


