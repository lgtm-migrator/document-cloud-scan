using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Http;
using TwainCloudServer.ApiModels;
using TwainCloudServer.Models;

namespace TwainCloudServer.Controllers
{
	// just test upload block

	public class ImageBlockController : ControllerBasecs
	{
		[Route("api/imageblock/{blockId}")]
		public byte[] Get(String blockId)
		{
			HttpContextBase context = (HttpContextBase)Request.Properties["MS_HttpContext"];
			context.Response.BufferOutput = false;
			context.Response.ContentType = "application/octet-stream";

			{
				var dbImageBlock = db.DbImageBlocks.FirstOrDefault(evt => (evt.blockId == blockId));
				if (null != dbImageBlock) {
					context.Response.BinaryWrite(dbImageBlock.data);
					db.DbImageBlocks.Remove(dbImageBlock);
					db.SaveChanges();
				}
			}

			context.Response.Flush();
			context.Response.Close();
			return null;
		}

		// response user token
		public string Post()
		{
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

				if (null != data) {

					String blockId = TokenProvider.getInstance().takeUUID();

					DbImageBlock imageBlockInfo = new DbImageBlock();

					imageBlockInfo.blockId = blockId;
					imageBlockInfo.imageType = imageType;
					imageBlockInfo.data = data;

					db.DbImageBlocks.Add(imageBlockInfo);
					db.SaveChanges();

					return blockId;
				}
			}

			return "";
		}

	}
}