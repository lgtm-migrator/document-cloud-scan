package com.dynamsoft.twaindirect.rest;

import com.dynamsoft.twaindirect.data.response.Root;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

import okhttp3.Call;

public interface TwainLocalHttpCallback {
	void onFailure(Call paramCall, IOException paramIOException);

	void onInfoex(JsonNode paramJsonNode);

	void onSession(Root paramRoot);

	void onCreateSession(Root paramRoot);

	void onWaitForEvents(Root paramRoot);

	void onGetSession(Root paramRoot);

	void onSendTask(Root paramRoot);

	void onStartCapturing(Root paramRoot);

	void onReadImageBlockMetadata(Root paramRoot);

	void onReleaseImageBlocks(Root paramRoot);

	void onStopCapturing(Root paramRoot);

	void onCloseSession(Root paramRoot);

	void onReadImageBlock(Root paramRoot, byte[] paramArrayOfByte);
}
