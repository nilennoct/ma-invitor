package org.nilennoct.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.nilennoct.controller.NetworkController;

/**
 * Created with IntelliJ IDEA.
 * User: Neo
 * Date: 13-9-4
 * Time: 下午3:33
 */
public class InviteComposite extends Composite {
	private final NetworkController nc = NetworkController.getInstance();

	public InviteComposite(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayoutTwoColumn = new GridLayout(2, false);
		this.setLayout(gridLayoutTwoColumn);

		GridData compositeGD = new GridData(GridData.FILL, GridData.FILL, true, true);
//		compositeGD.verticalIndent = 0;

		Composite leftComposite = new Composite(this, SWT.NONE);
		leftComposite.setLayout(gridLayoutTwoColumn);
		leftComposite.setLayoutData(compositeGD);
		Composite rightComposite = new Composite(this, SWT.NONE);
		rightComposite.setLayout(gridLayoutTwoColumn);
		rightComposite.setLayoutData(compositeGD);

		Label nameLabel = new Label(leftComposite, SWT.RIGHT);
		final Text nameText = new Text(leftComposite, SWT.BORDER);
		Label passwordLabel = new Label(leftComposite, SWT.RIGHT);
		final Text passwordText = new Text(leftComposite, SWT.BORDER | SWT.PASSWORD);
		Label invitorLabel = new Label(leftComposite, SWT.RIGHT);
		final Text invitorText = new Text(leftComposite, SWT.BORDER);
		Label sessionLabel = new Label(leftComposite, SWT.RIGHT);
		final Text sessionText = new Text(leftComposite, SWT.BORDER);

		Button registerButton = new Button(leftComposite, SWT.PUSH);

		Label proxyHostLabel = new Label(rightComposite, SWT.RIGHT);
		final Text proxyHostText = new Text(rightComposite, SWT.BORDER);
		Label proxyPortLabel = new Label(rightComposite, SWT.RIGHT);
		final Text proxyPortText = new Text(rightComposite, SWT.BORDER);

		final Button usingProxyButton = new Button(rightComposite, SWT.CHECK);

		GridData textGD = new GridData(GridData.FILL, GridData.FILL, true, false);
		GridData loginButtonGD = new GridData(GridData.FILL, GridData.FILL, false, false);
		loginButtonGD.horizontalSpan = 2;

		nameLabel.setText("Username: ");
		nameText.setLayoutData(textGD);
		passwordLabel.setText("Password: ");
		passwordText.setLayoutData(textGD);
		invitorLabel.setText("Invitor: ");
		invitorText.setLayoutData(textGD);
		sessionLabel.setText("Session: ");
		sessionText.setText("");
		sessionText.setLayoutData(textGD);
		registerButton.setText("Register");
		registerButton.setLayoutData(loginButtonGD);

		proxyHostLabel.setText("Proxy Host: ");
		proxyPortLabel.setText("Proxy Port: ");
		proxyHostText.setText("127.0.0.1");
		proxyHostText.setLayoutData(textGD);
		proxyPortText.setText("8087");
		proxyPortText.setLayoutData(textGD);
		usingProxyButton.setText("Using Proxy");

		registerButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				nc.register(nameText.getText(), passwordText.getText(), invitorText.getText(), sessionText.getText());
			}
		});

		usingProxyButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				nc.usingProxy = usingProxyButton.getSelection();
				if (nc.usingProxy) {
					nc.proxyHost = proxyHostText.getText();
					nc.proxyPort = Integer.parseInt(proxyPortText.getText());
				}
			}
		});

		this.pack();
	}
}
