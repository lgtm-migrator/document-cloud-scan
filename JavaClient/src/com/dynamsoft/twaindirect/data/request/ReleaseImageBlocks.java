package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseParams;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;

public class ReleaseImageBlocks extends BaseSession {
	public Params params;

	public static class Params extends BaseParams {
		public Integer imageBlockNum;
		public Integer lastImageBlockNum;
	}

	public ReleaseImageBlocks(String commandId, String sessionId) {
		super(commandId, Const.method.releaseImageBlocks);
		this.params = new Params();
		this.params.sessionId = sessionId;
	}
}
