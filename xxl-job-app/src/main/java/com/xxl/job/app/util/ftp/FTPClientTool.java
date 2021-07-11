package com.xxl.job.app.util.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP类. <br>
 * 描述了实现FTP操作的方法.<br>
 * 从1.0版本开始使用Apache commons net 2.0 API。<br>
 * <br>
 * <br>
 * 注意：
 * <ul>
 * <li>本组件不提供CD方法 。<br/>
 * 防止出现使用相对路径时的混乱，所以本组件所有方法中的路径参数都是从根开始的绝对路径。</li>
 * <li>本组件不提供上传、下载目录的方法。<br/>
 * FTP操作时间较长、步骤多，所以为了防止在执行出错时无法正确的处理异常，所以不提供上传、下载目录的方法。<br/>
 * 上传下载文件前应当使用List等方法将需要操作的文件清单记录下来，并输出日志。然后针对单个文件操作。</li>
 * <li>本组件不提供递归删除目录的方法。<br/>
 * 为了安全原因，防止误删除整个目录。</li>
 * </ul>
 * <a href="doc-files/ftp工具介绍.rar">FTP组件使用示例下载[ftp工具介绍.rar]</a>
 * 
 * <pre>
 * 
 * public class Test {
 * 
 * 	public static void main(String[] args) throws Exception {
 * 		if (1 == 1) {
 * 			// 创建FTP客户端对象.
 * 			FTPClientTool ftp = new FTPClientTool();
 * 			// 连接服务器.
 * 			ftp.connect(&quot;192.168.10.85&quot;, 21, &quot;root&quot;, &quot;123456&quot;);
 * 			// 创建目录（多级）.
 * 			ftp.mkdir(&quot;/ftp/test//a/1/2/3/4/5/6/7/8/../9/&quot;);
 * 			// 上传文件.
 * 			if (1 == 1) {
 * 				List&lt;File&gt; files = new ArrayList&lt;File&gt;();
 * 				files.addAll(FileFinder.findFiles(&quot;src&quot;, &quot;*&quot;, 0));
 * 				for (File file : files) {
 * 					// 上传文件到指定目录.
 * 					ftp.put(file.getPath(), &quot;/ftp/test/&quot; + file.getParent());
 * 					// 上传文件到指定目录，并且修改名字.
 * 					ftp.put(file.getPath(), &quot;/ftp/test/&quot;, file.getPath() + &quot;.abc&quot;);
 * 				}
 * 			}
 * 			// 上传指定内容.
 * 			ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/&quot;, &quot;1.txt&quot;);
 * 			ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/&quot;, &quot;2.txt&quot;);
 * 			// 获取指定目录下的目录清单.
 * 			{
 * 				System.out.println(&quot;=============&quot;);
 * 				List&lt;String&gt; dirs = ftp.getDirList(&quot;/ftp/test&quot;);
 * 				for (String dir : dirs) {
 * 					System.out.println(dir);
 * 				}
 * 			}
 * 			// 获取指定目录下的文件清单.
 * 			{
 * 				System.out.println(&quot;=============&quot;);
 * 				List&lt;String&gt; files = ftp.getFileList(&quot;/ftp/test&quot;);
 * 				for (String file : files)
 * 					System.out.println(file);
 * 			}
 * 			// 下载指定文件.
 * 			{
 * 				System.out.println(&quot;=============&quot;);
 * 				String ftpDir = &quot;/ftp/test/&quot;;
 * 				String localDir = &quot;./temp/ftp/001&quot;;
 * 				List&lt;String&gt; files = ftp.getFileList(ftpDir);
 * 				for (String file : files)
 * 					ftp.get(ftpDir + &quot;/&quot; + file, localDir);
 * 			}
 * 			// 删除目录、文件.
 * 			{
 * 				System.out.println(&quot;=============&quot;);
 * 				//
 * 				ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/b&quot;, &quot;1.txt&quot;);
 * 				ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/b&quot;, &quot;2.txt&quot;);
 * 				ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/c&quot;, &quot;1.txt&quot;);
 * 				ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/c&quot;, &quot;2.txt&quot;);
 * 				ftp.put(&quot;つづく&quot;.getBytes(), &quot;/ftp/test/c/k&quot;, &quot;3.txt&quot;);
 * 				//
 * 				List&lt;String&gt; files;
 * 				files = ftp.getFileList(&quot;/ftp/test&quot;);
 * 				for (String file : files)
 * 					System.out.println(file);
 * 				// 删除目录.
 * 				ftp.delDirFiles(&quot;/ftp/test/b&quot;);
 * 				// 删除文件.
 * 				ftp.deleteFile(&quot;/ftp/test/c/k/3.txt&quot;);
 * 				// 删除文件.
 * 				ftp.deleteFile(&quot;/ftp/test/c/1.txt&quot;);
 * 				//
 * 				files = ftp.getFileList(&quot;/ftp/test&quot;);
 * 				for (String file : files)
 * 					System.out.println(file);
 * 			}
 * 			//
 * 			ftp.close();
 * 		}
 * 	}
 * }
 * </pre>
 * 
 */
public class FTPClientTool {

	/** FTP操作错误码 */
	private static final int FTP_ERROR_CODE = 550;

	/** IO读写缓冲大小:64k */
	private final int BUFFER_SIZE = 1024 * 24;

	/** FTP客户端 */
	private FTPClient ftpClient;

	/** 超时时间 */
	private int timeOut = 10 * 1000;

	/** 日志 */
	private static final Logger logger = Logger.getLogger(FtpUtil.class);
	/**
	 * 构造器
	 */
	public FTPClientTool() {
		ftpClient = new FTPClient();
		ftpClient.setDataTimeout(timeOut);
		ftpClient.setDefaultTimeout(timeOut);
	}

	/**
	 * cd至指定ftp目录，必须先登录
	 * 
	 * @param remotePath
	 *            ftp目录 例如：/data/ftp
	 * @throws Exception
	 *             异常
	 */
	private boolean cd(String remotePath) throws Exception {
		remotePath = remotePath.replaceAll("\\\\", "/");
		if (!ftpClient.changeWorkingDirectory(remotePath)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 关闭ftp实例连接
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		if (ftpClient.isConnected()) {
			try {
				ftpClient.logout();
			}
			catch (Exception e) {
				throw e;
			}
			finally {
				ftpClient.disconnect();
			}
		}
	}

	/**
	 * 方法会连接自动尝试使用PassiveMode如失败则转为ActiveMode
	 * 
	 * @param hostName
	 *            ftp hostname
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param passWord
	 *            密码
	 * @throws Exception
	 *             连接失败抛出异常
	 */
	public void connect(String hostName, int port, String userName, String passWord) throws Exception {
		try {
			if (ftpClient.isConnected()) {
				close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("1111");
		ftpClient.connect(hostName, port);
		logger.info("2222");
		int code = ftpClient.getReplyCode();
		logger.info("code:"+code);
		String reply = ftpClient.getReplyString();
		logger.info("reply:"+reply);
		if (!FTPReply.isPositiveCompletion(code)) {
			ftpClient.disconnect();
			throw new Exception(reply);
		}
		ftpClient.setSoTimeout(timeOut);
		ftpClient.setDataTimeout(timeOut);
		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		ftpClient.setBufferSize(BUFFER_SIZE);
		ftpClient.login(userName, passWord);// 登录
		code = ftpClient.getReplyCode();
	    reply = ftpClient.getReplyString();
		logger.info("reply:"+reply);
		if (!FTPReply.isPositiveCompletion(code)) {
			ftpClient.disconnect();
			throw new Exception(reply);
		}
		
//		if (ftpClient.login(userName, passWord)) {
//			logger.info("userName:"+userName);
//			logger.info("passWord:"+passWord);
//			// 设置为被动模式
//			ftpClient.enterLocalPassiveMode();
//			// 进入根目录
//			try {
//				logger.info("1111");
//				ftpClient.listFiles();
//				logger.info("2222");
//			}
//			catch (Exception e) {
//				logger.info("44444");
//				// 设置为主动模式
//				connectActiveMode(hostName, port, userName, passWord);
//				logger.info("3333");
//			}
//			if (ftpClient.getReplyCode() == FTP_ERROR_CODE) {
//				// 设置为主动模式
//				connectActiveMode(hostName, port, userName, passWord);
//			}
//			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
//		} else {
//			throw new Exception("login fail:" + ftpClient.getReplyString());
//		}
	}

	/**
	 * 使用主动模式连接FTP
	 * 
	 * @param hostname
	 *            ip
	 * @param port
	 *            端口号
	 * @param userName
	 *            用户名
	 * @param passWord
	 *            密码
	 * @throws Exception
	 *             连接异常
	 */
	private void connectActiveMode(String hostname, int port, String userName, String passWord) throws Exception {
		// 关闭上次连接
		try {
			ftpClient.logout();
		}
		catch (Exception e) {
		}
		finally {
			try {
				ftpClient.disconnect();
			}
			catch (IOException e) {

			}
		}
		try {
			ftpClient.connect(hostname, port);
			int code = ftpClient.getReplyCode();
			String reply = ftpClient.getReplyString();
			if (!FTPReply.isPositiveCompletion(code)) {
				ftpClient.disconnect();
				throw new Exception(reply);
			}
			ftpClient.setSoTimeout(timeOut);
			ftpClient.setDataTimeout(timeOut);
			ftpClient.setBufferSize(BUFFER_SIZE);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftpClient.login(userName, passWord)) {
				ftpClient.enterLocalActiveMode();
			}
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		}
		catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 删除远端目录。只删除该目录下文件和该目录.鉴于安全原因，本方法不会递归删除指定目录下的子目录；
	 * 
	 * @param dir
	 *            目录名 例如:/data/ftp
	 * @throws Exception
	 *             删除失败
	 */
	public void delDirFiles(String dir) throws Exception {
		dir = dir.replaceAll("\\\\", "/");
		if (cd(dir)) {
			FTPFile[] ftpfiles = ftpClient.listFiles(dir);
			for (FTPFile file : ftpfiles) {
				if (!file.getName().startsWith(".") && file.isFile()) {
					// 删除目录中的文件
					if (!ftpClient.deleteFile(file.getName())) {
						throw new Exception("delete file fail :" + file.getName() + " info:" + ftpClient.getReplyString());
					}
				}
			}
			// 删除目录
			ftpClient.removeDirectory(dir);
		} else {
			throw new Exception("entry into path fail :" + dir + " info:" + ftpClient.getReplyString());
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param pathname
	 *            文件路径 例如: /data/ftp/1.smil
	 * @return 指定文件是否被删除
	 * @throws Exception
	 *             删除异常
	 */
	public boolean deleteFile(String pathname) throws Exception {
		pathname = pathname.replaceAll("\\\\", "/");
		boolean succFlag = ftpClient.deleteFile(pathname);
		if (!succFlag) {
			succFlag = ftpClient.removeDirectory(pathname);
		}
		return succFlag;
	}

	/**
	 * 下载文件
	 * 
	 * @param remoteFilePath
	 *            远程所要获取的文件名全路径 例如:/data/ftp/1.txt
	 * @return 下载文件的字节数组
	 * @throws Exception
	 *             下载失败则抛出异常
	 */
	public byte[] get(String remoteFilePath) throws Exception {
		byte[] fileByte = null;
		if (remoteFilePath == null || remoteFilePath.trim().length() == 0) {
			throw new Exception("the param remoteFilePath can not be null or empty.");
		}
		remoteFilePath = remoteFilePath.replaceAll("\\\\", "/");
		File file = new File(remoteFilePath);
		// 获取远程下载路径
		String remotePath = file.getParent();
		// 获取远程下载文件名
		String remoteFileName = file.getName();
		// 进入远程下载路径
		if (!cd(remotePath)) {
			throw new Exception("entry into remotePath fail:" + ftpClient.getReplyString());
		}
		//
		ByteArrayOutputStream fos = new ByteArrayOutputStream(1024 * 10);
		try {
			if (ftpClient.retrieveFile(remoteFileName, fos)) {
				fileByte = fos.toByteArray();
			} else {
				throw new Exception("download file byte[] fail:" + ftpClient.getReplyString());
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			fos.close();
		}
		//
		return fileByte;
	}

	/**
	 * 下载文件 必须先登录
	 * 
	 * @param remoteFilePath
	 *            远程所要获取的文件名全路径 例如:/data/ftp/0.smil
	 * @param localDir
	 *            本地路径存放下载的路径 例如：/data/mms/
	 * @throws Exception
	 *             下载失败则抛出异常
	 */
	public void get(String remoteFilePath, String localDir) throws Exception {
		get(remoteFilePath, localDir, null);
	}

	/**
	 * 下载文件 必须先登录
	 * 
	 * @param remoteFilePath
	 *            远程所要获取的文件名全路径 例如:/data/ftp/0.smil
	 * @param localDir
	 *            本地路径存放下载的路径 例如:/data/mms
	 * @param localName
	 *            下载至本地文件名称 例如:1.smil
	 * @throws Exception
	 *             下载失败则抛出异常
	 */
	public void get(String remoteFilePath, String localDir, String localName) throws Exception {

		if (remoteFilePath == null || remoteFilePath.trim().length() == 0 || localDir == null) {
			throw new Exception("the param remoteFilePath can not be null or empty.");
		}
		remoteFilePath = remoteFilePath.replaceAll("\\\\", "/");
		File file = new File(remoteFilePath);
		// 获取远程下载路径
		String remotePath = file.getParent();
		// 获取远程下载文件名
		String remoteFileName = file.getName();
		if (localName == null || localName.trim().length() == 0) {
			localName = remoteFileName;
		}
		// 进入远程下载路径
		if (!cd(remotePath)) {
			throw new Exception("entry into remotePath fail:" + ftpClient.getReplyString());
		}
		if (localDir == null || localDir.trim().length() == 0) {
			localDir = ".";
		}
		// 如果本地目录不存在则创建
		if (!localDir.equals(".")) {
			File temp = new File(localDir);
			if (!temp.isDirectory()) {
				temp.mkdirs();
			}
		}
		if (!localDir.endsWith("/")) {
			localDir = localDir + "/";
		}

		FileOutputStream fos = new FileOutputStream(localDir + localName);
		try {
			if (!ftpClient.retrieveFile(remoteFileName, fos)) {
				throw new Exception("download file fail:" + ftpClient.getReplyString());
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * 获得远端目录下的目录列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @return 目录名列表
	 * @throws Exception
	 *             异常
	 */
	public List<String> getDirList(String remoteDir) throws Exception {
		return getDirList(remoteDir, null);
	}

	/**
	 * 获得远端目录下的目录列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @param dirNameRegex
	 *            目标目录名的正则表达式, 若为空，则获得当前ftp目录下所有目录列表 例如: \\d+.* 返回以数字开头的目录名
	 * @return 目录名列表
	 * @throws Exception
	 *             异常
	 */
	public List<String> getDirList(String remoteDir, String dirNameRegex) throws Exception {
		List<String> nameList = new ArrayList<String>();
		List<FTPFile> dirList = getFTPDirList(remoteDir, dirNameRegex);
		for (int i = 0; i < dirList.size(); i++) {
			nameList.add(dirList.get(i).getName());
		}
		return nameList;
	}

	/**
	 * 获取 字符集
	 * 
	 * @return 字符集
	 */
	public String getEncoding() {
		return ftpClient.getControlEncoding();
	}

	/**
	 * 获得远端目录下的文件列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @return 文件列表
	 * @throws Exception
	 *             异常
	 */
	public List<String> getFileList(String remoteDir) throws Exception {
		return getFileList(remoteDir, null);
	}

	/**
	 * 获得远端目录下的文件列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @param fileNameRegex
	 *            目标文件名的正则表达式, 若为空，则获得当前ftp目录下所有文件列表 例如: .+txt 返回以txt结尾的文件名
	 * @return 文件列表
	 * @throws Exception
	 *             异常
	 */
	public List<String> getFileList(String remoteDir, String fileNameRegex) throws Exception {
		List<String> al = new ArrayList<String>();
		List<FTPFile> fileList = getFTPFileList(remoteDir, fileNameRegex);
		for (int i = 0; i < fileList.size(); i++) {
			al.add(fileList.get(i).getName());
		}
		return al;
	}

	/**
	 * 获取原FtpClient
	 * 
	 * @return 原FtpClient
	 */
	public FTPClient getFtpClient() {
		return ftpClient;
	}

	/**
	 * 获得远端目录下的FTP目录列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @return FTP目录列表
	 * @throws Exception
	 *             异常
	 */
	public List<FTPFile> getFTPDirList(String remoteDir) throws Exception {
		return getFTPDirList(remoteDir, null);
	}

	/**
	 * 获得远端目录下的FTP目录列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @param dirNameRegex
	 *            目标目录名的正则表达式, 若为空，则获得当前ftp目录下所有FTP目录列表 例如: \\d+.* 返回以数字开头的目录名
	 * @return FTP目录列表
	 * @throws Exception
	 *             异常
	 */
	public List<FTPFile> getFTPDirList(String remoteDir, String dirNameRegex) throws Exception {
		List<FTPFile> nameList = new ArrayList<FTPFile>();
		remoteDir = remoteDir.replaceAll("\\\\", "/");
		remoteDir = remoteDir.replaceAll("//", "/");
		if (!remoteDir.startsWith("/")) {
			remoteDir = "/" + remoteDir;
		}

		if (remoteDir != null && remoteDir.length() > 0) {
			if (cd(remoteDir)) {
				FTPFile[] fileList = ftpClient.listFiles();
				if (dirNameRegex != null && dirNameRegex.trim().length() != 0) {
					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].getName().equals(".") || fileList[i].getName().endsWith("..")) {
							continue;
						}
						if (fileList[i].isDirectory() && fileList[i].getName().matches(dirNameRegex)) {
							nameList.add(fileList[i]);
						}
					}
				} else {
					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].isDirectory()) {
							if (fileList[i].getName().equals(".") || fileList[i].getName().endsWith("..")) {
								continue;
							}
							nameList.add(fileList[i]);
						}
					}
				}
			}
		}
		return nameList;
	}

	/**
	 * 获得远端目录下的FTP文件列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @return FTP文件列表
	 * @throws Exception
	 *             异常
	 */
	public List<FTPFile> getFTPFileList(String remoteDir) throws Exception {
		return getFTPFileList(remoteDir, null);
	}

	/**
	 * 获得远端目录下的FTP文件列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录
	 * @param fileNameRegex
	 *            目标文件名的正则表达式, 若为空，则获得当前ftp目录下所有FTP文件列表 例如: .+txt 返回以txt结尾的文件名
	 * @return FTP文件列表
	 * @throws Exception
	 *             异常
	 */
	public List<FTPFile> getFTPFileList(String remoteDir, String fileNameRegex) throws Exception {
		remoteDir = remoteDir.replaceAll("\\\\", "/");
		List<FTPFile> al = new ArrayList<FTPFile>();
		if (remoteDir != null && remoteDir.length() > 0) {
			if (cd(remoteDir)) {
				FTPFile[] fileList = ftpClient.listFiles();
				if (fileNameRegex != null && fileNameRegex.trim().length() != 0) {
					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].isFile() && fileList[i].getName().matches(fileNameRegex)) {
							al.add(fileList[i]);
						}
					}
				} else {
					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].isFile()) {
							al.add(fileList[i]);
						}
					}

				}
			}
		}
		return al;
	}

	/**
	 * 获取超时时间
	 * 
	 * @return timeOut 超时时间，单位为毫秒
	 */
	public final int getTimeOut() {
		return this.timeOut;
	}

	/**
	 * 建立远端目录(支持多级目录创建),如存在则不创建直接进入目录
	 * 
	 * @param name
	 *            目录路径 例如：/data/ftp/mms/200812/0126
	 * @throws Exception
	 *             创建目录失败则抛出异常
	 */
	public void mkdir(String name) throws Exception {

		if (name == null || name.trim().length() == 0) {
			throw new Exception("the param name can not be null or empty.");
		}
		name = name.replaceAll("\\\\", "/");
		name = name.replaceAll("//", "/");
		if (!name.startsWith("/")) {
			name = "/" + name;
		}
		if (!cd(name)) {
			String upDir = new File(name).getParent();
			if (upDir == null || upDir.equals("")) {
				return;
			}
			mkdir(upDir);
			ftpClient.makeDirectory(name);
		}
	}

	/**
	 * 上传文件到远端服务器
	 * 
	 * @param inputDate
	 *            待上传的字节数组
	 * @param remoteDirPath
	 *            远端文件路径（包含文件名），不存在会自动建立目录 例如:/data/ftp/1230/
	 * @param remoteFileName
	 *            上传至远端文件名 例如:do.txt
	 * @throws Exception
	 *             上传失败则抛出异常 异常
	 */
	public void put(byte[] inputDate, String remoteDirPath, String remoteFileName) throws Exception {
		if (inputDate == null || remoteFileName == null || remoteFileName.trim().length() == 0) {
			throw new Exception("params inputDate or remoteFileName  can not be null or empty.");
		}
		// 重新整合路径和文件名
		File ftpFile = new File(remoteDirPath + "/" + remoteFileName);
		remoteFileName = ftpFile.getName();
		remoteDirPath = ftpFile.getParent();
		remoteDirPath = remoteDirPath.replaceAll("\\\\", "/");
		remoteDirPath = remoteDirPath.replaceAll("//", "/");

		if (!remoteDirPath.startsWith("/")) {
			remoteDirPath = "/" + remoteDirPath;
		}

		cd("/");
		mkdir(remoteDirPath);
		cd(remoteDirPath);

		ByteArrayInputStream fis = null;
		try {
			fis = new ByteArrayInputStream(inputDate);
			if (!ftpClient.storeFile(remoteFileName, fis)) {
				throw new Exception("upload byte[] file fail:" + ftpClient.getReplyString());
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * 上传文件到远端服务器
	 * 
	 * @param localPathFile
	 *            文本待上传文件路径 例如：/data/mms/1.txt
	 * @param remoteDirPath
	 *            远端目录路径，不存在则自动建立目录 例如：/data/ftp/mms/200812/0126
	 * @throws Exception
	 *             异常
	 */
	public void put(String localPathFile, String remoteDirPath) throws Exception {
		put(localPathFile, remoteDirPath, null);
	}

	/**
	 * 上传文件到远端服务器
	 * 
	 * @param localPathFile
	 *            文本待上传文件路径 例如：/data/mms/1.txt
	 * @param remoteDirPath
	 *            远端目录路径，不存在会自动建立目录 例如：/data/ftp/mms/200812/0126
	 * @param remoteFileName
	 *            上传至远端文件名 例如：/data/mms/2.txt
	 * @throws Exception
	 *             上传失败则抛出异常 异常
	 */
	public void put(String localPathFile, String remoteDirPath, String remoteFileName) throws Exception {
		File localFile = new File(localPathFile);
		if (!localFile.isFile()) {
			throw new FileNotFoundException("文件" + localFile.getAbsolutePath() + "不存在或者没有读取权限");
		}
		if (remoteFileName == null || remoteFileName.trim().length() == 0) {
			remoteFileName = localFile.getName();
		}

		// 重新整合路径和文件名
		File ftpFile = new File(remoteDirPath + "/" + remoteFileName);
		remoteFileName = ftpFile.getName();
		remoteDirPath = ftpFile.getParent();
		remoteDirPath = remoteDirPath.replaceAll("\\\\", "/");
		remoteDirPath = remoteDirPath.replaceAll("//", "/");

		if (!remoteDirPath.startsWith("/")) {
			remoteDirPath = "/" + remoteDirPath;
		}

		cd("/");
		mkdir(remoteDirPath);
		cd(remoteDirPath);
		FileInputStream fis = new FileInputStream(localFile);
		try {
			if (!ftpClient.storeFile(remoteFileName, fis)) {
				throw new Exception("upload file fail:" + ftpClient.getReplyString());
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * 修改ftp目录下的文件名，必须先登录
	 * 
	 * @param srcPathFile
	 *            ftp上需要修改的文件路径 例如：/data/mms/1.txt
	 * @param destPathFile
	 *            ftp上目标文件路径 例如：/data/mms2/3.txt
	 * @return 修改ftp当前目录下的文件名是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean rename(String srcPathFile, String destPathFile) throws Exception {
		srcPathFile = srcPathFile.replaceAll("\\\\", "/");
		destPathFile = destPathFile.replaceAll("\\\\", "/");
		return ftpClient.rename(srcPathFile, destPathFile);
	}

	/**
	 * 设置 字符集
	 * 
	 * @param encoding
	 *            字符集
	 */
	public void setEncoding(String encoding) {
		ftpClient.setControlEncoding(encoding);
	}

	/**
	 * 设置连接与交互数据超时时间。默认值为10秒。
	 * 
	 * @param timeOut
	 *            ftp操作与连接超时时间，单位为毫秒
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
		ftpClient.setDataTimeout(timeOut);
		ftpClient.setDefaultTimeout(timeOut);
	}
}
