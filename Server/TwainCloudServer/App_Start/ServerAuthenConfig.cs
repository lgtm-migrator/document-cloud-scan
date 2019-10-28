using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace TwainCloudServer
{
	public class ServerAuthenConfig
	{
		private static String m_szDeviceSecret = null;
		protected static String InnerEncrypt(String str)
		{
			var tmp = Encoding.Default.GetBytes(str);
			tmp = MD5.Create().ComputeHash(tmp);
			tmp = SHA1.Create().ComputeHash(tmp);
			tmp = SHA256.Create().ComputeHash(tmp);
			return Convert.ToBase64String(tmp);
		}
		public static String GetDeviceSecret()
		{
			if (null == m_szDeviceSecret) {
				String serverToken = System.Configuration.ConfigurationManager.AppSettings["serverToken"];
				m_szDeviceSecret = InnerEncrypt(serverToken);
			}

			return m_szDeviceSecret;
		}
		public static string encryptUsername(string username)
		{
			DESCryptoServiceProvider des = new DESCryptoServiceProvider();
			byte[] inputByteArray;
			inputByteArray = Encoding.Default.GetBytes(username);

			String secret = GetDeviceSecret();
			des.Key = ASCIIEncoding.ASCII.GetBytes(secret.Substring(0, 8));
			des.IV = ASCIIEncoding.ASCII.GetBytes(secret.Substring(16, 8));

			MemoryStream ms = new MemoryStream();
			CryptoStream cs = new CryptoStream(ms, des.CreateEncryptor(), CryptoStreamMode.Write);
			cs.Write(inputByteArray, 0, inputByteArray.Length);
			cs.FlushFinalBlock();
			StringBuilder ret = new StringBuilder();
			foreach (byte b in ms.ToArray())
			{
				ret.AppendFormat("{0:X2}", b);
			}
			return ret.ToString();
		}

		public static string decryptUsername(string pToDecryptUsername)
		{
			DESCryptoServiceProvider des = new DESCryptoServiceProvider();

			byte[] inputByteArray = new byte[pToDecryptUsername.Length / 2];
			for (int x = 0; x < pToDecryptUsername.Length / 2; x++)
			{
				int i = (Convert.ToInt32(pToDecryptUsername.Substring(x * 2, 2), 16));
				inputByteArray[x] = (byte)i;
			}

			String secret = GetDeviceSecret();
			des.Key = ASCIIEncoding.ASCII.GetBytes(secret.Substring(0, 8));
			des.IV = ASCIIEncoding.ASCII.GetBytes(secret.Substring(16, 8));

			MemoryStream ms = new MemoryStream();
			CryptoStream cs = new CryptoStream(ms, des.CreateDecryptor(), CryptoStreamMode.Write);
			cs.Write(inputByteArray, 0, inputByteArray.Length);
			cs.FlushFinalBlock();

			StringBuilder ret = new StringBuilder();
			return System.Text.Encoding.Default.GetString(ms.ToArray());
		}
	}
}