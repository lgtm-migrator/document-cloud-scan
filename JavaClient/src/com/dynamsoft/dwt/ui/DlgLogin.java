package com.dynamsoft.dwt.ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.dynamsoft.IniConfig;
import com.dynamsoft.could.ICloudCallback;
import com.dynamsoft.could.TwainCloudLoginClient;
import com.dynamsoft.could.entity.TCTokens;
import com.dynamsoft.dialog.CancelableDialog;
import com.dynamsoft.dialog.ICancelable;

public class DlgLogin extends JFrame implements ICloudCallback {

	private static final long serialVersionUID = 1L;
	
	private JTextField txtUsername;
	private JTextField txtPassword;
	private JButton btnLogin;
	
	public DlgLogin() {
		setTitle("TWAIN Direct Cloud Application - Login");
		setDefaultCloseOperation(3);
		setBounds(100, 100, 500, 300);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		
		JPanel middlePanel = new JPanel();
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		
		
		JPanel usernamePanel = new JPanel();

		JLabel lbUsername = new JLabel("Username: ");
		lbUsername.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		usernamePanel.add(lbUsername);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(15);
		usernamePanel.add(txtUsername);

		txtUsername.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					txtPassword.requestFocus();
				}
			}
		});
		

		JPanel passwordPanel = new JPanel();
		JLabel lbPassword = new JLabel(" Password: ");
		lbPassword.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
		passwordPanel.add(lbPassword);
		txtPassword = new JTextField();
		txtPassword.setColumns(15);
		passwordPanel.add(txtPassword);
		txtPassword.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnLogin.doClick();
				}
			}
		});


		JPanel buttonPanel = new JPanel();
		JLabel lbPad = new JLabel("");
		lbPad.setBorder(BorderFactory.createEmptyBorder(0, 180, 0, 0));
		buttonPanel.add(lbPad);
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				RegistAsync();
			}
		});
		buttonPanel.add(btnLogin);
		
	    middlePanel.setLayout(new GridLayout(3,1));
	    middlePanel.add(usernamePanel);
	    middlePanel.add(passwordPanel);
	    middlePanel.add(buttonPanel);
	    
	    setLayout(new BorderLayout());
	    getContentPane().add(topPanel, BorderLayout.NORTH);
	    getContentPane().add(middlePanel, BorderLayout.CENTER);
	    getContentPane().add(bottomPanel, BorderLayout.SOUTH);

		this.setLocationRelativeTo(null);
	    setPreferredSize(new Dimension(500, 300));
	    setResizable(false);
	    pack();

	    objCancelableDialog = new CancelableDialog(this, "Cancel Dialog");
	    bShowErrMsg = true;
	    this.setVisible(true);
	}
	
	private CancelableDialog objCancelableDialog = null;
	private void RegistAsync()
	{
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		
		this.clearErrMessage();
		if(username.isEmpty() || password.isEmpty()) {
			this.onError("Error: Invalid Username or Password.");
			this.txtUsername.requestFocus();
			return;
		}

		btnLogin.setEnabled(false);
		txtUsername.setEnabled(false);
		txtPassword.setEnabled(false);
		
		ICloudCallback _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				String server = IniConfig.getInstance().getString("Cloud", "server");
				TwainCloudLoginClient tcLoginClient = new TwainCloudLoginClient(server, _this);
				
				new Thread(new Runnable() {
					@Override
					public void run() {

						objCancelableDialog.setCallback(new ICancelable() {
							
							@Override
							public void cancel() {
								tcLoginClient.cancelPost();
								
							}
						});
						objCancelableDialog.setModal(true);
						objCancelableDialog.setVisible(true);
					}
				}).start();
				
				TCTokens tokens = tcLoginClient.login(username, password);
				if(tokens == null)
				{
					_this.onError("Error: Invalid Username or Password.");
					txtUsername.requestFocus();
				} else {
					_this.onSuccess(tokens);
				}

			}
		}).start();
	}
	
	protected String GetDeviceRequestTopic(String scannerId)
	{
		return String.format("twain/devices/%s", scannerId);
	}
	
	protected void clearErrMessage()
	{
		bShowErrMsg = true;
	}

	private boolean bShowErrMsg = true;
	
	@Override
	public void onError(String err) {

		objCancelableDialog.setVisible(false);
		
		if(bShowErrMsg)
			JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
		bShowErrMsg = false;
		
		
		btnLogin.setEnabled(true);
		txtUsername.setEnabled(true);
		txtPassword.setEnabled(true);
	}
	
	@Override
	public void onSuccess(Object obj) {
		if(null != obj) {

			if(null != objCancelableDialog) {
				objCancelableDialog.setModal(false);
				objCancelableDialog.setVisible(false);
			}
			
			TCTokens tokens = (TCTokens) obj;
			
			JFrame frame = new DlgSelectSource(tokens);
			frame.setVisible(true);

			new Thread(new Runnable() {
				@Override
				public void run() {
					if(null != objCancelableDialog) {
						objCancelableDialog.dispose();
					}
					dispose();
				}
			}).start();
		} else {
			this.onError("Cannot find login token.");
		}
	}

	@Override
	protected void processWindowEvent(WindowEvent e) { 
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {

			if(null != this.objCancelableDialog) {
				this.objCancelableDialog.dispose();
			}
			this.dispose();
		} else { 
			super.processWindowEvent(e); 
		}
	} 
}
