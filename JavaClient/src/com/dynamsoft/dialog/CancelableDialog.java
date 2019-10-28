package com.dynamsoft.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CancelableDialog extends JDialog implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private static Color foregroundColor = new Color(64, 128, 64);
	private ICancelable objCancelable;
	private JButton btnCancel = null;

	public CancelableDialog(Component parent, String title) {
		
		this.objCancelable = null;
		
		Box vBox = Box.createVerticalBox();
        vBox.add(Box.createVerticalStrut(15));
        vBox.setSize(new Dimension(350, 150));
        
        vBox.add(Box.createVerticalStrut(30));
       
		JProgressBar jpb = new JProgressBar();
		jpb.setUI(new CancelableUI());
		jpb.setForeground(foregroundColor);
		jpb.setIndeterminate(true);
		jpb.setAlignmentX(CENTER_ALIGNMENT);
		jpb.setPreferredSize(new Dimension(250, 25));

        vBox.add(jpb);
        vBox.add(Box.createVerticalStrut(15));
        

		btnCancel = new JButton();
		btnCancel.setText("Cancel");
		btnCancel.setAlignmentX(CENTER_ALIGNMENT);
		btnCancel.addActionListener(this);
        vBox.add(btnCancel);
        vBox.add(Box.createVerticalStrut(15));
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(350, 150));
        panel.add(vBox);

        this.setTitle(title);
		this.setContentPane(panel);
		this.pack();
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		this.setVisible(false);
	}
	
	public void setCallback(ICancelable objCancelable)
	{
		if(objCancelable == null) {
			if(null != this.btnCancel) {
				this.btnCancel.setVisible(false);
			}
		}
		
		this.objCancelable = objCancelable;
	}
	

	protected boolean canHandleShellCloseEvent() {
	    return false;
	}

	@Override
	protected void processWindowEvent(WindowEvent e) { 
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			
		} else { 
			super.processWindowEvent(e); 
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(null != objCancelable) {
			objCancelable.cancel();
		}
		this.dispose();
	}
	
}
