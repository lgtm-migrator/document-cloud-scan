package com.dynamsoft.twaindirect.local.entity;

public class Const {
	public static final String winTitle = "TWAIN Direct Simple Application";
	public class moreParts {
		public static final String lastPartInFile = "lastPartInFile";
		public static final String morePartsPending = "morePartsPending";
		public static final String lastPartInFileMorePartsPending = "lastPartInFileMorePartsPending";
	}

	public class source {
		public static final String feederFront = "feederFront";
		public static final String feederRear = "feederRear";
		public static final String flatbed = "flatbed";
		public static final String planetary = "planetary";
		public static final String storage = "storage";
	}

	public class method {
		public static final String createSession = "createSession";
		public static final String waitForEvents = "waitForEvents";
		public static final String getSession = "getSession";
		public static final String sendTask = "sendTask";
		public static final String startCapturing = "startCapturing";
		public static final String readImageBlockMetadata = "readImageBlockMetadata";
		public static final String readImageBlock = "readImageBlock";
		public static final String releaseImageBlocks = "releaseImageBlocks";
		public static final String stopCapturing = "stopCapturing";
		public static final String closeSession = "closeSession";
	}

	public class state {
		public static final String noSession = "noSession";
		public static final String ready = "ready";
		public static final String capturing = "capturing";
		public static final String draining = "draining";
		public static final String closed = "closed";
	}

	public class event {
		public static final String commandComplete = "commandComplete";
		public static final String commandUpdate = "commandUpdate";
		public static final String imageBlocks = "imageBlocks";
	}

	public class code {
		public static final String aborted = "aborted";
		public static final String badValue = "badValue";
		public static final String busy = "busy";
		public static final String commandPending = "commandPending";
		public static final String critical = "critical";
		public static final String invalidJson = "invalidJson";
		public static final String invalidSessionId = "invalidSessionId";
		public static final String invalidState = "invalidState";
		public static final String invalidTask = "invalidTask";
		public static final String invalid_x_privet_token = "invalid_x_privet_token";
		public static final String timeout = "timeout";
		public static final String waking = "waking";
	}
}
