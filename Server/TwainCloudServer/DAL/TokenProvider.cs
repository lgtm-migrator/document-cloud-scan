using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace TwainCloudServer
{
	public class TokenProvider
	{
		private static TokenProvider provider = null;
		public static TokenProvider getInstance()
		{
			if (provider == null)
				provider = new TokenProvider();
			return provider;
		}

		private TokenProvider()
		{
		}

		public String takeUUID()
		{
			return Guid.NewGuid().ToString();
		}
		private String byteToHexStr(byte[] bytes)
		{
			StringBuilder ret = new StringBuilder();
			if (bytes != null)
			{
				int length = bytes.Length;
				for (int i = 0; i < length; i++)
				{
					ret.Append(bytes[i].ToString("X2"));
				}
			}
			return ret.ToString();
		}

		/// <summary>
		/// Create an X-Privet-Token, we do this to generate a brand new value,
		/// and we do it to recreate a value that we want to validate...
		/// </summary>
		/// <param name="a_lTicks">0 to generate a new one, or the ticks from a previously created token</param>
		/// <returns>the token</returns>
		public string takeToken(String username, long a_lTicks = 0)
		{
			long lTicks;
			string szXPrivetToken;

			// Use our ticks, this is for validation...
			if (a_lTicks > 0)
			{
				lTicks = a_lTicks;
			}

			// Otherwise use the clock, this is for generation...
			else
			{
				lTicks = DateTime.Now.Ticks;
			}

			// This is what's recommended...
			// XSRF_token = base64( SHA1(device_secret + DELIMITER + issue_timecounter) + DELIMITER + issue_timecounter )      
			szXPrivetToken = String.Format("{0}:{1}:{2}", ServerAuthenConfig.GetDeviceSecret(), username, lTicks);
			using (SHA256Managed sha256managed = new SHA256Managed())
			{
				byte[] abHash = sha256managed.ComputeHash(Encoding.UTF8.GetBytes(szXPrivetToken));
				szXPrivetToken =  byteToHexStr(abHash);
			}

			szXPrivetToken = String.Format("{0}:{1}:{2}", szXPrivetToken, ServerAuthenConfig.encryptUsername(username), lTicks);

			return (szXPrivetToken);
		}


		public bool checkToken(String tokenIn, out String username, out Boolean bExpired)
		{
			bool bValid = false;
			bExpired = true;
			username = null;

			string[] arr = tokenIn.Split(':');
			if (arr.Length == 3)
			{
				long lTicksNow = DateTime.Now.Ticks;
				long lTicksFromToken = long.Parse(arr[2]);

				if (arr[1] != "" && lTicksFromToken > 0)
				{
					username = ServerAuthenConfig.decryptUsername(arr[1]);
					String mustBe = String.Format("{0}:{1}:{2}", ServerAuthenConfig.GetDeviceSecret(), username, lTicksFromToken);
					using (SHA256Managed sha256managed = new SHA256Managed())
					{
						byte[] abHash = sha256managed.ComputeHash(Encoding.UTF8.GetBytes(mustBe));
						mustBe = byteToHexStr(abHash);
					}

					if (mustBe == arr[0])
					{
						bValid = true;

						// 12 hours
						if ((lTicksNow - lTicksFromToken) < 432000000000)
						{
							bExpired = false;
						}
					}
				}
			}

			return bValid;
		}
	}
}