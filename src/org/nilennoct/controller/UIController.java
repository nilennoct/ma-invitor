package org.nilennoct.controller;

import org.eclipse.swt.widgets.Display;
import org.nilennoct.view.*;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Neo
 * Date: 13-9-5
 * Time: 上午3:18
 */
public class UIController {
	private static UIController uc = null;
	private InviteComposite inviteComposite = null;
	private OutputComposite outputComposite = null;

	private UIController() {}

	public static UIController getInstance() {
		if (uc == null) {
			uc = new UIController();
		}

		return uc;
	}

	public void log(String log) {
		this.getOutputComposite().output.add(new Date().toString() + " | " +  log, 0);
	}

	public void logInThread(final String log) {
		final UIController that = this;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				that.getOutputComposite().output.add(new Date().toString() + " | " +  log, 0);
			}
		});
	}

	public void setInviteComposite(InviteComposite inviteComposite) {
		this.inviteComposite = inviteComposite;
	}

	public OutputComposite getOutputComposite() {
		return outputComposite;
	}

	public void setOutputComposite(OutputComposite outputComposite) {
		this.outputComposite = outputComposite;
	}
}
