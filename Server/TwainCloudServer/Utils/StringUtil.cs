using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Utils
{
	public class StringUtil
	{
		public static String GetTopic_RecieveMsgFromDevice(String username)
		{
			return String.Format("dynamsoft/clouduser/{0}", username);
		}

		// ScannerSessionTopic
		public static String GetTopic_SendMsgToDevice(String scannerId)
		{
			return String.Format("twain/devices/{0}", scannerId);
		}


		public static String GetCmdId(String body)
		{
			return StringUtil.GetCmdValue(body, "commandId");
		}
		public static String GetCmdMethod(String body)
		{
			String method = StringUtil.GetCmdValue(body, "method");
			return method.Trim('"');
		}
		private static String GetCmdValue(String body, String name)
		{ 
			String id = null;
			int index = (body != null) ? body.IndexOf(name) : -1;

			if (index > 0)
			{
				int indexNameStart = index - 1;
				int indexNameEnd = index + name.Length;
				if ((body[indexNameStart] == body[indexNameEnd]) &&
					((body[indexNameStart] == '\'') || (body[indexNameStart] == '"')))
				{
					// ok

					for (; index < body.Length; index++)
					{
						if (body[index] == ':')
						{
							index++;
							break;
						}
					}

					int lastIndex = index;
					for (; lastIndex < body.Length; lastIndex++)
					{
						if (body[lastIndex] == ',' || body[lastIndex] == '}')
						{
							break;
						}
					}

					id = body.Substring(index, lastIndex - index);

				}
			}

			if (id == null)
			{
				id = "";
			}

			return id;
		}

		
	}
}