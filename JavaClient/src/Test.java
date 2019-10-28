

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.dynamsoft.DLogger;
import com.dynamsoft.IniConfig;
import com.dynamsoft.could.TwainLocalHttpServer;
import com.dynamsoft.could.entity.TCCmdInput;
import com.dynamsoft.could.json.JsonToEntity;
import com.dynamsoft.dwt.Common;
import com.dynamsoft.dwt.DWTClient;
import com.dynamsoft.dwt.ICmdCallback;
import com.dynamsoft.dwt.evt.DWTEventHandler;
import com.dynamsoft.dwt.evt.EventType;
import com.dynamsoft.dwt.ui.DlgLogin;

public class Test {

	public static void main(String[] args) {
		new Test().TestMain();
	}
	
	public void TestMain()
	{
		IniConfig.getInstance();
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
				new DlgLogin();
            }
        });
		
	}

	public void TestCreateSession(DWTClient objDWTClient) {
		
		String input = "{ \"kind\": \"twainlocalscanner\", \"commandId\": \"123123\", \"method\": \"createSession\" }";
		TwainLocalHttpServer core = new TwainLocalHttpServer(objDWTClient, "");

		TCCmdInput cmdInput = JsonToEntity.getParser().parseSessionCommand(input);
		String result = core.ProcessCmd(cmdInput);
		DLogger.GetLogger().LogInfo(result);
		
	}
	
	public void TestDWT() {
		try {
			ExecutorService executor = Executors.newCachedThreadPool();

			DWTClient objDWTClient = new DWTClient();

			FutureTask<Integer> futureTaskClose = new FutureTask<Integer>(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {

					Thread.sleep(500);
					if (null != objDWTClient)
						objDWTClient.dispose();

					executor.shutdown();
					return null;
				}
			});
			FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
				
				@Override
				public Integer call() throws Exception {

					objDWTClient.Acquire(new ICmdCallback() {

						@Override
						public void sFun(List<String> ret) {
							executor.submit(futureTaskClose);
						}

						@Override
						public boolean fFun(String errString) {

							executor.submit(futureTaskClose);
							return false;
						}
					});

					return null;
				}
			});

			objDWTClient.addEvent(EventType.OnReady, new DWTEventHandler() {

				@Override
				public void callback(List<String> a_) {
					objDWTClient.SetProductKey(Common.DWT_ProductKey);
					objDWTClient.IfShowFileDialog(false);
					objDWTClient.IfShowUI(false);

					List<String> list = objDWTClient.GetSourceNames();

					for (int i = 0; i < list.size(); i++)
						DLogger.GetLogger().LogDebug(list.get(i));

					executor.submit(futureTask);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}