package org.nilennoct.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.nilennoct.controller.UIController;

/**
 * Created with IntelliJ IDEA.
 * User: Neo
 * Date: 13-9-4
 * Time: 下午4:56
 */
class ControlTabFolder extends TabFolder {
	public ControlTabFolder(Composite parent) {
		super(parent, SWT.BORDER);
		TabItem inviteTabItem = new TabItem(this, SWT.NONE);
		InviteComposite inviteComposite = new InviteComposite(this);

		inviteTabItem.setText("Invite");
		inviteTabItem.setControl(inviteComposite);
		UIController.getInstance().setInviteComposite(inviteComposite);
	}

	@Override
	protected void checkSubclass () {}
}
