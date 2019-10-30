package com.dynamsoft.dwt.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import com.dynamsoft.DLogger;
import com.dynamsoft.IniConfig;
import com.dynamsoft.could.ICloudCallback;
import com.dynamsoft.could.TwainCloudClient;
import com.dynamsoft.could.TwainCloudMqttDevice;
import com.dynamsoft.could.TwainCloudMqttMsgProcesser;
import com.dynamsoft.could.TwainLocalHttpServer;
import com.dynamsoft.could.entity.TCScannerRegister;
import com.dynamsoft.could.entity.TCTokens;
import com.dynamsoft.could.entity.TCUserInfo;
import com.dynamsoft.dialog.CancelableDialog;
import com.dynamsoft.dwt.Common;
import com.dynamsoft.dwt.DWTClient;
import com.dynamsoft.dwt.evt.DWTEventHandler;
import com.dynamsoft.dwt.evt.EventType;

public class DlgSelectSource extends JFrame implements ICloudCallback {

	private static final long serialVersionUID = 1L;
	
	private JComboBox<String> cmbScanners;
	private JButton btnRegist;
	private JButton btnRefresh;
	
	private boolean bClosed = false;
	
	private DWTClient objDWTClient = null;
	private TwainCloudClient tcClient = null;
	private String strCurrentScannerId = null;
	private String strUserTopic = null;
	
	private TCTokens tokens;
	private TwainCloudMqttDevice deviceSubscribe;
	
	private boolean bShowErrMsg = true;
	private CancelableDialog objCancelableDialog = null;

    protected void processWindowEvent(WindowEvent e) {
       if (e.getID() == WindowEvent.WINDOW_CLOSING) {
    	   this.bClosed = true;
			if(null != tcClient && null != this.strCurrentScannerId) {
				
				if(null != this.strUserTopic && !this.strUserTopic.isEmpty()) {
					MqttMessage closemsg = new MqttMessage();
					closemsg.setPayload("close".getBytes());
					if(null != this.deviceSubscribe)
						this.deviceSubscribe.send(this.strUserTopic, closemsg);
				}
				
				this.tcClient.deleteScanner(this.strCurrentScannerId);
				this.tcClient = null;
				this.strCurrentScannerId = null;
				this.strUserTopic = null;
			}
       }
       super.processWindowEvent(e);
    }
    
	public DlgSelectSource(TCTokens tokens) {
		
		this.tcClient = null;
		this.tokens = tokens;
		this.objCancelableDialog = new CancelableDialog(this, "Register Scanner");
		
		this.createUI();

		new Thread(new Runnable() {
			@Override
			public void run() {
				objCancelableDialog.setCallback(null);
				objCancelableDialog.setModal(true);
				objCancelableDialog.setVisible(true);
			}
		}).start();
		
	    new Thread(new Runnable() {
			@Override
			public void run() {
				objDWTClient = new DWTClient();
				objDWTClient.addEvent(EventType.OnReady, new DWTEventHandler() {

					@Override
					public void callback(List<String> a_) {
						
						objCancelableDialog.setVisible(false);
						
						objDWTClient.SetProductKey(Common.DWT_ProductKey);
						objDWTClient.IfShowFileDialog(false);
						objDWTClient.IfShowUI(false);

						setButtonsEnable(true);

						btnRefresh.doClick();
					}
				});
				
			}
		}).start();

		this.setButtonsEnable(false);
		
	    this.pack();
		this.setLocationRelativeTo(null);
	    this.setVisible(true);
	}

	private void createUI() {
		setTitle("TWAIN Direct Cloud Application - Regist a scanner");
		setDefaultCloseOperation(3);
		setBounds(100, 100, 500, 300);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		
		
		JPanel selectPanel = new JPanel();

		JLabel lblScannerName = new JLabel("Select Scanner Name: ");
		selectPanel.add(lblScannerName);
		
		cmbScanners = new JComboBox<String>();
		cmbScanners.setPreferredSize(new Dimension(260, 25));
		selectPanel.add(cmbScanners);

		JPanel buttonPanel = new JPanel();
		
		btnRefresh = new JButton("Refresh");
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 240, 0, 0));
		buttonPanel.add(btnRefresh);
		
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				setButtonsEnable(false);

				List<String> list = objDWTClient.GetSourceNames();
				cmbScanners.setModel(new DefaultComboBoxModel<String>(list.toArray(new String[0])));

				setButtonsEnable(true);
				
			}
		});
		
		btnRegist = new JButton("Register");
		buttonPanel.add(btnRegist);
		btnRegist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = cmbScanners.getSelectedIndex();
				if(-1 != selectedIndex) {
					String scanner = cmbScanners.getModel().getElementAt(selectedIndex);

					setButtonsEnable(false);
					registScanner(scanner);
					setButtonsEnable(true);
				}
			}
		});

		JPanel middlePanel = new JPanel();
	    middlePanel.add(selectPanel);
	    middlePanel.add(buttonPanel);
	    
	    setLayout(new BorderLayout());
	    getContentPane().add(topPanel, BorderLayout.NORTH);
	    getContentPane().add(middlePanel, BorderLayout.CENTER);
	    getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	    
	    setPreferredSize(new Dimension(500, 300));
	    setResizable(false);
	}
	
	private void setButtonsEnable(boolean bEnable)
	{
		btnRefresh.setEnabled(bEnable);
		btnRegist.setEnabled(bEnable);
	}

	private void registScanner(String scanner)
	{
		this.clearErrMessage();
		if(null != tcClient) {
			tcClient.deleteScanner(this.strCurrentScannerId);
			tcClient = null;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				objCancelableDialog.setVisible(true);
			}
		}).start();
		
		String server = IniConfig.getInstance().getString("Cloud", "server");
		tcClient = new TwainCloudClient(server, this.tokens, this);

		TCScannerRegister register = tcClient.register(scanner);
		if(null == register)
		{
			objCancelableDialog.setVisible(false);
			if(bShowErrMsg)
				JOptionPane.showMessageDialog(null, "Error: Failed to register local scanner.", "Error", JOptionPane.ERROR_MESSAGE);

			return ;
		}
		
		String claimUrl = register.inviteUrl;
		if(null == claimUrl)
		{
			objCancelableDialog.setVisible(false);
			if(bShowErrMsg)
				JOptionPane.showMessageDialog(null, "Error: The claim url from Server is empty.", "Error", JOptionPane.ERROR_MESSAGE);

			return ;
		}
		
		tcClient.claim(claimUrl);
		strCurrentScannerId = register.scannerId;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JOptionPane.showMessageDialog(null, "Succeeded scanner registration.", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
		}).start();

    	TCUserInfo userInfo = tcClient.GetUserInfo();
		objCancelableDialog.setVisible(false);
		
		if(null != userInfo) {
			this.strUserTopic = userInfo.topic;
		}
		
		// MQTT Subscribe
    	if(userInfo.type.equals("mqtt")) {
    		Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {

			    	try {

			    		TwainLocalHttpServer localHttpServer = new TwainLocalHttpServer(objDWTClient, scanner);
			    		
			    		String subscribeTopic = "twain/devices/" + register.scannerId;
			        	int qos = 2; // 0:<=1, 1:>=1, 2:==1
						deviceSubscribe = new TwainCloudMqttDevice(userInfo.url);
				        deviceSubscribe.subscribe(subscribeTopic, qos, 
				        		new TwainCloudMqttMsgProcesser(deviceSubscribe, localHttpServer, userInfo.topic, register.scannerId, tcClient));
						if(DLogger.debug) {
							DLogger.println("Mqtt url:" + userInfo.url);
							DLogger.println("subscribe topic:" + subscribeTopic);
						}
						
						while(true) {
							if(bClosed) {
						    	try {
									deviceSubscribe.unsubscribe();
									deviceSubscribe.close();
								} catch (MqttSecurityException e) {
									e.printStackTrace();
								} catch (MqttException e) {
									e.printStackTrace();
								}
								break;
							}
							Thread.sleep(300);	
						}

						
					} catch (MqttSecurityException e) {
						e.printStackTrace();
					} catch (MqttException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
    		thread.start();
    	}
	}

	protected void clearErrMessage()
	{
		bShowErrMsg = true;
	}
	
	@Override
	public void onError(String err) {
		JOptionPane.showMessageDialog(null, err, "Error", JOptionPane.ERROR_MESSAGE);
		bShowErrMsg = false;
	}
	
	@Override
	public void onSuccess(Object obj)
	{
		
	}
}