package com.dynamsoft.twaindirect.local;

import com.dynamsoft.DLogger;
import com.dynamsoft.IDProvider;
import com.dynamsoft.twaindirect.local.entity.TDLSession;

public class TDLSessionActions {

	protected TDLSession session;
	protected TDLSessionState state;

	public TDLSessionActions()
	{
		this(TDLSessionState.ready);
	}
	
	public TDLSessionActions(TDLSessionState state)
	{
		this.session = new TDLSession();
		this.state = state;
	}
	
	public TDLSession getSession()
	{
		return this.session;
	}
	
	public TDLSessionState getSessionState()
	{
		return this.state;
	}
	
	protected boolean createSession()
	{
		boolean bHandled = false;

		if(this.session.state.equals(TDLSessionState.draining.toString())) {
			this.releaseImageBlocks();
		}
		
		if(this.session.state.equals(TDLSessionState.ready.toString())) {
			bHandled = true;
		} else {
			if(!this.session.state.equals(TDLSessionState.noSession.toString())) {
				this.closeSession(false);
			}
			bHandled = transfer(TDLSessionState.noSession, TDLSessionState.ready);
		}
		
		if(bHandled) {
			this.session.sessionId = String.valueOf(IDProvider.getInstance().getRamdomIDAsLong());
			this.session.revision = 1;
			this.session.state = this.state.toString();
		}

		return bHandled;
	}

	protected void gotoNoSession() {
		this.session.gotoNoSession();
		this.state = TDLSessionState.noSession;
	}
	
	protected boolean closeSession(boolean hasBlocks)
	{
		boolean bHandled = false;
		bHandled = transfer(TDLSessionState.ready, TDLSessionState.noSession);
		if(bHandled) {
			this.gotoNoSession();
			return bHandled;
		}
		
		bHandled = transfer(TDLSessionState.draining, TDLSessionState.closed);
		if(bHandled) { 
			return bHandled;
		}

		if(this.state.equals(TDLSessionState.capturing)) {
			if(hasBlocks) {
				bHandled = transfer(TDLSessionState.capturing, TDLSessionState.closed);
			} else {
				bHandled = transfer(TDLSessionState.capturing, TDLSessionState.noSession);
				if(bHandled) {
					this.gotoNoSession();
				}
			}
		}
		
		return bHandled;
	}
	
	protected boolean startCapturing()
	{
		return transfer(TDLSessionState.ready, TDLSessionState.capturing);
	}

	protected boolean stopCapturing(boolean hasBlocks)
	{
		boolean bHandled = false;
		if(hasBlocks)
			bHandled = transfer(TDLSessionState.capturing, TDLSessionState.draining);
		else
			bHandled = transfer(TDLSessionState.capturing, TDLSessionState.ready);
		return bHandled;
	}
	
	protected boolean releaseImageBlocks()
	{
		boolean bHandled = false;
		if(this.state.equals(TDLSessionState.closed))
			bHandled = transfer(TDLSessionState.closed, TDLSessionState.noSession);
		else if(this.state.equals(TDLSessionState.draining))
			bHandled = transfer(TDLSessionState.draining, TDLSessionState.ready);
		
		if(bHandled) {
			return bHandled;
		}
		
		bHandled = transfer(TDLSessionState.draining, TDLSessionState.ready);
		return bHandled;
	}

	protected boolean transfer(TDLSessionState from, TDLSessionState to) {

		DLogger.GetLogger().LogInfo(String.format("Change the state %s: %s => %s, success: %s", this.state, from.toString(), to.toString(), String.valueOf(this.state.equals(from))));
		
		if(this.state.equals(from))
		{
			this.state = to;
			return true;
		}
		
		return false;
	}

}
