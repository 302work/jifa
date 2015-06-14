var dirtyEntityNum = 0;

function refreshActions() {
	var node = view.get("#dataTreeDept.currentNode");
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

// @Bind @deptDataType.onStateChange
// @Bind @userDataType.onStateChange
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

//@Bind @deptDataType.onInsert
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

//@Bind @deptDataType.onRemove
!function(arg) {
	arg.entity.set("sortFlag", 0);
};


//@Bind #dataTreeDept.onDataRowClick
!function(dsDepts,dataTreeDept,userDataPilot,userDataGrid) {
	var currEntity = dsDepts.getData("!CURRENT_DEPT");
	var id = currEntity.get("id");
	if(id){
		//点击树重新加载数据
		currEntity.reset("users");
		//dsDepts.flushAsync();
		//currEntity.reset("child");
	}
	refreshActions();
};
// @Bind #dataTreeDept.onContextMenu
!function(self, arg) {
	view.id("menuDepts").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};
//@Bind #dataTreeDept.onDataNodeCreate
!function(self, arg) {
	var entity = arg.data;
	if (entity.state == dorado.Entity.STATE_NEW) {
		setTimeout(function() {
			self.set("currentNode", arg.node);
		}, 50);
	}
};
//@Bind #dataTreeDept.onDraggingSourceMove
!function(arg) {
	var draggingInfo = arg.draggingInfo;
	var sourceNode = draggingInfo.get("object");
	var sourceType = sourceNode.get("bindingConfig.name");

	var targetNode = draggingInfo.get("targetObject");
	if (targetNode) {
		var accept = false;
		if (sourceType == "Dept") {
			accept = (targetNode.get("bindingConfig.name") == "Dept");
		}
		draggingInfo.set("accept", accept);
	}
};

//@Bind #actionAdd.onExecute
!function(dsDepts, dataTreeDept) {
	var currentEntity = dsDepts.getData("!CURRENT_DEPT");	
	if (currentEntity) {
		var data = {
				name : "<新部门>",
				parentDeptId : currentEntity.get("id"),
				sortFlag:0
			};
//		currentEntity.createChild("child", data);
		//插入
		currentEntity.get("child").insert(data,"before");
	} else {
		//dsDepts.getData().createChild({name:"<新部门>"});
		dsDepts.getData().insert({name:"<新部门>",sortFlag:0},"before");
	}
	var currentNode = dataTreeDept.get("currentNode");
	if (currentNode) {
		currentNode.expandAsync();
	}
	
};

//@Bind #actionRemove.onExecute
!function(dsDepts) {
	var currentEntity = dsDepts.getData("!CURRENT_DEPT");
	if (currentEntity) {
		if (currentEntity.get("hasChild")) {
		      dorado.MessageBox.alert("请先删除子部门");
		      return false;
	    } else {
	      currentEntity.remove();        
	    }
	}
};

//@Bind #actionMove.onExecute
!function(dialogSelectDept) {
	dialogSelectDept.show();
};

// @Bind #actionCancel.onExecute
!function(dsDepts) {
	var entity = dsDepts.getData("!CURRENT_DEPT");
	if (entity) {
		entity.cancel();
	}
};

// @Bind #actionSaveAll.onSuccess
!function() {
	dirtyEntityNum = 0;
	refreshActions();
};


//@Bind #buttonOk.onClick
!function(selectDataTreeDept, dataTreeDept, dialogSelectDept) {
	//用户选择的部门
	var dept = selectDataTreeDept.get("currentNode.data");
	if (!dept) {
		dorado.MessageBox.alert("请选择一个有效的部门。");
		return;
	}
	//要移动的部门
	var currentDeptNode = dataTreeDept.get("currentNode");
	if(dept == currentDeptNode.get("data")){
		dorado.MessageBox.alert("不能选择自己。");
		return;
	}
	//不能parent
	var parentCurrentDeptNode = currentDeptNode.get("parent");
	if(dept == parentCurrentDeptNode.get("data")){
		dorado.MessageBox.alert("不能选择自己的父部门。");
		return;
	}
	var newDept = dataTreeDept.get("currentNode.data");
	newDept = newDept.clone();
	newDept.set("sortFlag", 0);
	dept.get("child").insert(newDept,"before");
	currentDeptNode.remove();	
	dialogSelectDept.hide();
};

// @Bind #buttonCancel.onClick
!function(dialogSelectDept) {
	dialogSelectDept.hide();
};


