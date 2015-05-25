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

// @Bind @Account.onStateChange
// @Bind @Record.onStateChange
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

// @Bind #dataTreeAccount.onContextMenu
!function(self, arg) {
	view.id("menuAccounts").show({
		position : {
			left : arg.event.pageX,
			top : arg.event.pageY
		}
	});
};

//@Bind #actionAdd.onExecute
!function(dsAccounts, dataTreeAccount) {
	var currentEntity = dsAccounts.getData("!CURRENT_ACCOUNT");	
	if (currentEntity) {
		var data = {
				name : "<新分类>",
				parentAccountId : currentEntity.get("id")
			};
		currentEntity.createChild("child", data);
	} else {
		dsAccounts.getData().createChild({name:"<新分类>"});
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
