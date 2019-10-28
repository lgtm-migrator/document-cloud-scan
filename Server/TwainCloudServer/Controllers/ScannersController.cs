using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using System.Web.Script.Serialization;
using TwainCloudServer.ApiModels;
using TwainCloudServer.Models;
using TwainCloudServer.Utils;

namespace TwainCloudServer.Controllers
{
	public class ScannersController : ControllerBasecs
	{
		[Route("api/scanners")]
		public IEnumerable<TCScanner> Get()
		{
			IEnumerable<TCScanner> list;

			String username;
			var tokens = GetAuthen(out username);
			if (false != tokens)
			{
				String accessToken = TokenProvider.getInstance().takeToken(username);
				String refreshToken = TokenProvider.getInstance().takeToken(username);

				list = from dbScanner in db.DbScanners
					   join dbUserScanner in db.DbUserScanners
					   on dbScanner.id equals dbUserScanner.scannerId
					   where dbUserScanner.name == username
					   select new TCScanner
					   {
						   id = dbScanner.id,
						   name = dbScanner.name,
						   manufacturer = dbScanner.manufacturer,
						   accessToken = accessToken,
						   refreshToken = refreshToken
					   };
			}
			else
			{
				list = new TCScanner[0];
			}

			return list;
		}
		[Route("api/scanners/{id}")]
		public object Get(string id)
		{
			object ret = null;
			String username;
			var tokens = GetAuthen(out username);

			if (false != tokens)
			{
				String accessToken = TokenProvider.getInstance().takeToken(username);
				String refreshToken = TokenProvider.getInstance().takeToken(username);

				ret = (from dbScanner in db.DbScanners
					   join dbUserScanner in db.DbUserScanners
					   on dbScanner.id equals dbUserScanner.scannerId
					   where dbUserScanner.name == username
					   && dbScanner.id == id
					   select new
					   {
						   success = true,
						   accessToken = accessToken,
						   refreshToken = refreshToken
					   }).FirstOrDefault();
			}
			return ret;
		}
		[Route("api/scanners/{id}/{method}")]
		public object Get(string id, string method)
		{
			return Get(id, method, null, null);
		}
		[Route("api/scanners/{id}/{method}/{ex1}")]
		public object Get(string id, string method, string ex1)
		{
			return Get(id, method, ex1, null);
		}

		[Route("api/scanners/{id}/{method}/{ex1}/{ex2}")]
		public object Get(string id, string method, string ex1, string ex2)
		{
			if (method == "info")
			{
				return Info(id);
			}
			else if (method == "infoex")
			{
				return InfoEx(id);
			}
			else if (method == "twaindirect" && ex1 == "session")
			{
				// error
			}

			return String.Format("{0}_{1}_{2}_{3}", id, method, ex1, ex2);
		}

		[Route("api/scanners")]
		public void Post()
		{
			System.Console.WriteLine("post scanners ");
		}

		[Route("api/scanners/{id}")]
		public void Post(String id)
		{
			System.Console.WriteLine("post scanners " + id);
		}

		// blocks
		[Route("api/scanners/{id}/{method}")]
		public object Post(string id, string method)
		{
			return Post(id, method, null, null, null);
		}

		[Route("api/scanners/{id}/{method}/{ex1}")]
		public object Post(string id, string method, string ex1, [FromBody] string value)
		{
			return Post(id, method, ex1, null, value);
		}

		[Route("api/scanners/{id}/{method}/{ex1}/{ex2}")]
		public object Post(string id, string method, string ex1, string ex2, [FromBody] string value)
		{
			if (method == "info")
			{
				return Info(id);
			}
			else if (method == "infoex")
			{
				return InfoEx(id);
			}
			else if (method == "blocks")
			{
				return PostBlocks(id);
			}
			else if (method == "twaindirect" && ex1 == "session")
			{
				if (ex2 == null) {
					return doSession(id);
				}
			}

			return String.Format("{0}_{1}_{2}_{3}", id, method, ex1, ex2);
		}

		[Route("api/scanners/{id}")]
		public void Delete(string id)
		{
			String username;
			var tokens = GetAuthen(out username);

			if (false != tokens)
			{
				bool bDeleted = false;
				// find scanner to delete
				var dbScannerToDelete = db.DbScanners.FirstOrDefault(scanner => scanner.id == id);
				if (dbScannerToDelete != null)
				{
					// delete scanner
					db.DbScanners.Remove(dbScannerToDelete);
					bDeleted = true;
				}

				// delete user scanner
				DbUserScanner dbUserScanner = db.DbUserScanners.FirstOrDefault(userScanner => userScanner.scannerId == id && userScanner.name == username);
				if (null != dbUserScanner)
				{
					db.DbUserScanners.Remove(dbUserScanner);
					bDeleted = true;
				}

				// delete blocks
				var aryDbImageBlock = db.DbImageBlocks.Where(blockImg => blockImg.scannerId == id);
				if (null != aryDbImageBlock)
				{
					db.DbImageBlocks.RemoveRange(aryDbImageBlock);
					bDeleted = true;
				}

				if (bDeleted)
				{
					db.SaveChanges();
				}

				MqttPool.getInstance().Close(username);
			}
		}


		protected TCScannerInfo Info(string id)
		{
			TCScannerInfo ret = null;

			String username;
			String XPrivetToken;
			var tokens = GetAuthen(out username, out XPrivetToken);

			if (false != tokens)
			{
				ret = (from dbScanner in db.DbScanners
					   join dbUserScanner in db.DbUserScanners
					   on dbScanner.id equals dbUserScanner.scannerId
					   where dbUserScanner.name == username
					   && dbScanner.id == id
					   select new TCScannerInfo
					   {
						   version = "1.0",
						   name = dbScanner.name,
						   description = dbScanner.description,
						   url= "",
						   type = "twaindirect",
						   id= "",
						   device_state = dbScanner.device_state, //  "idle",
						   connection_state = dbScanner.connection_state, // "offline"
						   manufacturer = dbScanner.manufacturer,
						   model= "",
						   serial_number= "",
						   firmware= "",
						   uptime= "",
						   setup_url= "",
						   support_url= "",
						   update_url= "",
						   XPrivetToken = "",
						   api = { "/twaindirect/session" },
						   semantic_state = ""
					   }).FirstOrDefault();

				if (null != ret)
				{
					// get token from Local Scan
					TCSessionMessage msg = new TCSessionMessage();
					msg.url = "https://cloud.dynamsoft.com/" + id;     // just for check
					msg.headers = "x-privet-token:\"\"";

					String sendData = JsonConvert.SerializeObject(msg);
					MqttResult mqttResult = MqttPool.getInstance().SendMqttMessage(id, username, sendData, null);

					if (!mqttResult.bTimeout)
					{
						ret.XPrivetToken = mqttResult.result;
					}
					else {
						
					}
					
				}
			}

			return ret;
		}
		
		protected TCScannerInfoEx InfoEx(string id)
		{
			TCScannerInfoEx ret = null;

			String username;
			String XPrivetToken;
			var tokens = GetAuthen(out username, out XPrivetToken);

			if (false != tokens)
			{
				ret = (from dbScanner in db.DbScanners
					   join dbUserScanner in db.DbUserScanners
					   on dbScanner.id equals dbUserScanner.scannerId
					   where dbUserScanner.name == username
					   && dbScanner.id == id
					   select new TCScannerInfoEx
					   {
						   version = "1.0",
						   name = dbScanner.name,
						   description = dbScanner.description,
						   url = dbScanner.url,
						   type = "twaindirect",
						   id = dbScanner.id,
						   device_state = dbScanner.device_state, //  "idle",
						   connection_state = dbScanner.connection_state,// "online"
						   manufacturer = dbScanner.manufacturer,
						   model = dbScanner.model,
						   serial_number = dbScanner.serial_number,
						   firmware = dbScanner.firmware,
						   uptime = dbScanner.uptime,
						   setup_url = dbScanner.setup_url,
						   support_url = dbScanner.support_url,
						   update_url = dbScanner.update_url,
						   XPrivetToken = "",
						   api = new List<String>(){ "/twaindirect/session" },
						   semantic_state = ""
					   }).FirstOrDefault();


				if (null != ret)
				{
					// get token from Local Scan
					TCSessionMessage msg = new TCSessionMessage();
					msg.url = "https://twaincloud.dynamsoft.com/" + id;     // just for check
					msg.headers = "x-privet-token:\"\"";
					String sendData = JsonConvert.SerializeObject(msg);

					MqttResult mqttResult = MqttPool.getInstance().SendMqttMessage(id, username, sendData, null);
					TCTokenSession res = null;
					if (mqttResult != null) {
						if (!mqttResult.bTimeout)
						{
							res = JsonConvert.DeserializeObject<TCTokenSession>(mqttResult.result);
						}
						else {
							// timed out
							ret.error = mqttResult.errorString;
						}
					}

					if (res != null) {
						ret.XPrivetToken = res.token;
						ret.sessionid = res.sessionid;
					}

				}
			}

			return ret;
		}


		protected object PostBlocks(string scannerId)
		{
			HttpContextBase context = (HttpContextBase)Request.Properties["MS_HttpContext"];
			context.Response.BufferOutput = false;

			{
				HttpRequestBase request = GetHttpRequest();
				byte[] data = null;
				String imageType = null;
				if (request.Files.Count > 0)
				{
					using (var stream = request.Files[0].InputStream)
					{
						using (var br = new BinaryReader(stream))
						{
							if (br.BaseStream.Length < Int32.MaxValue)
							{
								imageType = request.Files[0].ContentType;
								data = new byte[br.BaseStream.Length];
								br.Read(data, 0, (int)br.BaseStream.Length);
							}
						}
					}
				}

				if (null != data)
				{

					String blockId = TokenProvider.getInstance().takeUUID();

					DbImageBlock imageBlockInfo = new DbImageBlock();

					imageBlockInfo.blockId = blockId;
					imageBlockInfo.imageType = imageType;
					imageBlockInfo.data = data;
					imageBlockInfo.scannerId = scannerId;

					db.DbImageBlocks.Add(imageBlockInfo);
					db.SaveChanges();

					String root = HttpContext.Current.Request.Url.GetLeftPart(UriPartial.Authority);

					String imageBlockUrl = String.Format("{0}/api/imageblock/{1}", root, blockId);
					context.Response.Write(imageBlockUrl);
				}
			}

			context.Response.Flush();
			context.Response.Close();
			return null;
		}


		protected object doSession(string id)
		{
			String username;
			String XPrivetToken;
			var tokens = GetAuthen(out username, out XPrivetToken);

			string ret = null;
			if (false != tokens) {

				String strBody = GetPostDataAsString();
				String cmdId = StringUtil.GetCmdId(strBody);
				String method = StringUtil.GetCmdMethod(strBody);

				TCSessionMessage msg = new TCSessionMessage();
				msg.url = "https://twaincloud.dynamsoft.com/" + id;     // just for check
				msg.body = strBody;
				msg.headers = GetHeadersAsString();
				String sendData = JsonConvert.SerializeObject(msg);

				MqttResult mqttResult = MqttPool.getInstance().SendMqttMessage(id, username, sendData, cmdId);
				ret = mqttResult.result;

			}

			return ret;
		}

		protected object waitForEvent(string id, string username, string strBody, string cmdId, string method)
		{
			HttpContextBase context = (HttpContextBase)Request.Properties["MS_HttpContext"];
			context.Response.BufferOutput = false;

			do
			{
				TCSessionMessage msg = new TCSessionMessage();
				msg.url = "https://cloud.dynamsoft.com/" + id;     // just for check
				msg.body = strBody;
				msg.headers = GetHeadersAsString();
				String sendData = JsonConvert.SerializeObject(msg);

				MqttResult mqttResult = MqttPool.getInstance().SendMqttMessage(id, username, sendData, cmdId);
				string ret = mqttResult.result;

				if(null != ret)
					context.Response.Write(ret);
				context.Response.Flush();

			} while (false);

			context.Response.Close();
			return null;
		}

	}
}
