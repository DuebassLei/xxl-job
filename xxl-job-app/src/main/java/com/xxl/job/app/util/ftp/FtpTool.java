package com.xxl.job.app.util.ftp;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * FTP类. <br>
 * 描述了实现FTP操作的方法.<br>
 * 从1.1版本开始使用Apache commons net 2.0 API<br>
 * <br>
 * History 2008-12-31 上午11:30:01 gaowei<br>
 * 加入模式设置(enterActiveMode、enterPassiveMode)<br>
 * 加入自定上传文件名功能(putToFile)<br>
 * 开始使用ApacheCommonsNet2.0版本API <br>
 * <p>
 *@deprecated 应当使用{@link com.cplatform.util2.ftp.FTPClientTool} 实现功能
 */
@Deprecated
public class FtpTool {

	/** 超时时间 */
	private static final int TIMEOUT = 10 * 1000;

	/** FTP客户端 */
	private FTPClient ftpClient;

	/** 本地路径 */
	private String localDir;

	/**
	 * 构造器
	 */
	public FtpTool() {
		ftpClient = new FTPClient();
		ftpClient.setDataTimeout(TIMEOUT);
		ftpClient.setDefaultTimeout(TIMEOUT);
	}

	/**
	 * cd至指定ftp目录，必须先登录
	 * 
	 * @param remotePath
	 *            ftp目录
	 * @return cd至指定ftp目录是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean cd(String remotePath) throws Exception {
		return ftpClient.changeWorkingDirectory(remotePath);
	}

	/**
	 * 返回上级ftp目录，必须先登录
	 * 
	 * @return 返回上级ftp目录是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean cdUp() throws Exception {
		return ftpClient.changeToParentDirectory();
	}

	/**
	 * 删除文件
	 * 
	 * @param pathname
	 *            文件路径
	 * @return 是否删除成功
	 * @throws Exception
	 *             异常
	 */
	public boolean deleteFile(String pathname) throws Exception {
		if (!ftpClient.deleteFile(pathname)) {
			return ftpClient.removeDirectory(pathname);
		} else {
			return true;
		}
	}

	/**
	 * 进入主动模式
	 */
	public void enterActiveMode() {
		ftpClient.enterLocalActiveMode();
	}

	/**
	 * 进入被动模式
	 */
	public void enterPassiveMode() {
		ftpClient.enterLocalPassiveMode();
	}

	/**
	 * 退出FTP
	 */
	public void exit() throws Exception {
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
	 * 下载文件 必须先登录
	 * 
	 * @param localDir
	 *            本地路径，若为空则为预设路径，若预设路径为空，则为工作目录
	 * @param remoteFileName
	 *            所要获取的 文件名
	 * @return 是否下载成功
	 * @throws Exception
	 *             异常
	 */
	public boolean get(String localDir, String remoteFileName) throws Exception {
		if (localDir == null || localDir.trim().length() == 0) {
			localDir = this.localDir;
		}
		if (localDir == null || localDir.trim().length() == 0) {
			localDir = ".";
		}
		if (!localDir.equals(".")) {
			File temp = new File(localDir);
			if (!temp.isDirectory()) {
				temp.mkdirs();
			}
		}
		if (!localDir.endsWith("/")) {
			localDir = localDir + "/";
		}

		FileOutputStream fos = new FileOutputStream(localDir + remoteFileName);
		try {
			return ftpClient.retrieveFile(remoteFileName, fos);
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
	 * 直接下载文件 必须先登录
	 * 
	 * @param localFilePath
	 *            本地文件路径
	 * @param remoteFilePath
	 *            远端文件路径
	 * @return 是否下载成功
	 * @throws Exception
	 *             异常
	 */
	public boolean getAbsoluteFile(String localFilePath, String remoteFilePath) throws Exception {

		File localFile = new File(localFilePath).getParentFile();
		if (localFile != null && !localFile.exists()) {
			localFile.mkdirs();
		}

		FileOutputStream fos = new FileOutputStream(localFilePath);
		try {
			return ftpClient.retrieveFile(remoteFilePath, fos);
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
	 *            目标目录，若为空，则为当前ftp目录
	 * @param dirNameRegex
	 *            目标目录名的正则表达式, 若为空，则获得当前ftp目录下所有目录列表
	 * @return 目录名列表
	 * @throws Exception
	 *             异常
	 */
	public String[] getDirList(String remoteDir, String dirNameRegex) throws Exception {
		if (remoteDir != null && remoteDir.length() > 0) {
			cd(remoteDir);
		}
		FTPFile[] fileList = ftpClient.listFiles();
		ArrayList<String> al = new ArrayList<String>();

		if (dirNameRegex != null && dirNameRegex.trim().length() != 0) {
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].getName().equals(".") || fileList[i].getName().endsWith("..")) {
					continue;
				}
				if (fileList[i].isDirectory() && fileList[i].getName().matches(dirNameRegex)) {
					al.add(fileList[i].getName());
				}
			}
		} else {
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory()) {
					if (fileList[i].getName().equals(".") || fileList[i].getName().endsWith("..")) {
						continue;
					}
					al.add(fileList[i].getName());
				}
			}
		}
		String[] nameList = new String[al.size()];
		for (int i = 0; i < al.size(); i++) {
			nameList[i] = al.get(i);
		}
		return nameList;
	}

	/**
	 * 返回服务器的编码格式
	 * 
	 * @return 服务器的编码格式
	 */
	public String getEncoding() {
		return ftpClient.getControlEncoding();
	}

	/**
	 * 获得远端目录下的文件列表，必须先登录
	 * 
	 * @param remoteDir
	 *            目标目录，若为空，则为当前ftp目录
	 * @param fileNameRegex
	 *            目标文件名的正则表达式, 若为空，则获得当前ftp目录下所有文件列表
	 * @return 文件列表
	 * @throws Exception
	 *             异常
	 */
	public String[] getFileList(String remoteDir, String fileNameRegex) throws Exception {
		if (remoteDir != null && remoteDir.length() > 0) {
			cd(remoteDir);
		}
		FTPFile[] fileList = ftpClient.listFiles();

		ArrayList<String> al = new ArrayList<String>();
		if (fileNameRegex != null && fileNameRegex.trim().length() != 0) {
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile() && fileList[i].getName().matches(fileNameRegex)) {
					al.add(fileList[i].getName());
				}
			}
		} else {
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile()) {
					al.add(fileList[i].getName());
				}
			}

		}
		String[] nameList = new String[al.size()];
		for (int i = 0; i < al.size(); i++) {
			nameList[i] = al.get(i);
		}
		return nameList;
	}

	/**
	 * 取得本地路径
	 * 
	 * @return 本地路径
	 */
	public String getLocalDir() {
		return localDir;
	}

	/**
	 * 登录FTP
	 * 
	 * @param url
	 *            目标url，如www.sina.com
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return 是否登陆成功
	 * @throws Exception
	 *             异常
	 */
	public boolean login(String url, int port, String userName, String password) throws Exception {
		try {
			if (ftpClient.isConnected()) {
				exit();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ftpClient.connect(url, port);
		int code = ftpClient.getReplyCode();
		String reply = ftpClient.getReplyString();

		if (!FTPReply.isPositiveCompletion(code)) {
			ftpClient.disconnect();
			throw new Exception(reply);
		}

		ftpClient.setSoTimeout(TIMEOUT);
		ftpClient.setDataTimeout(TIMEOUT);
		setBinary();
		return ftpClient.login(userName, password);
	}

	/**
	 * 登录FTP（默认21端口）
	 * 
	 * @param url
	 *            目标url，如www.sina.com
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return 是否登陆成功
	 * @throws Exception
	 *             异常
	 */
	public boolean login(String url, String userName, String password) throws Exception {

		return login(url, 21, userName, password);
	}

	/**
	 * 建立目录
	 * 
	 * @param name
	 *            名称
	 * @return 建立结果是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean mkdir(String name) throws Exception {
		return ftpClient.makeDirectory(name);
	}

	/**
	 * 批量下载文件，必须先登录
	 * 
	 * @param localDir
	 *            本地路径，若为空则为预设路径，若预设路径为空，则为工作目录
	 * @param remoteFileNames
	 *            下载文件名的正则表达式，若为空则下载当前ftp目录下所有文件(不包括文件夹)
	 * @return 批量下载文件是否成功
	 */
	public boolean multiGet(String localDir, String remoteFileNames) throws Exception {
		String[] files = this.getFileList(null, remoteFileNames);
		if (files == null) {
			return false;
		}
		for (int i = 0; i < files.length; i++) {
			get(localDir, files[i]);
		}
		return true;
	}

	/**
	 * 批量上传文件，必须先登录
	 * 
	 * @param localDir
	 *            本地路径，若为空则为预设路径，若预设路径为空，则为工作目录
	 * @param localFileNames
	 *            上传文件名列表
	 * @return 批量上传文件是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean multiPut(String localDir, String[] localFileNames) throws Exception {
		for (int i = 0; i < localFileNames.length; i++) {
			this.put(localDir, localFileNames[i]);
		}
		return true;
	}

	/**
	 * 上传文件，必须先登录
	 * 
	 * @param localDir
	 *            本地路径，若为空则为预设路径，若预设路径为空，则为工作目录
	 * @param localFileName
	 *            待上传本地文件名
	 * @return 上传文件是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean put(String localDir, String localFileName) throws Exception {
		if (localDir == null || localDir.trim().length() == 0) {
			localDir = this.localDir;
		}
		if (localDir == null || localDir.trim().length() == 0) {
			localDir = ".";
		}
		File file;
		if (localFileName.startsWith("/")) {
			file = new File(localDir + localFileName);
		} else {
			file = new File(localDir + "/" + localFileName);
		}
		FileInputStream fis = new FileInputStream(file);
		try {
			return ftpClient.storeFile(file.getName(), fis);
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
	 * @param localFile
	 *            本地文件
	 * @param remoteFilePath
	 *            远端文件路径（包含文件名），不存在会自动建立
	 * @return 上传文件到远端服务器是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean putToFile(File localFile, String remoteFilePath) throws Exception {
		if (!localFile.isFile()) {
			throw new FileNotFoundException("文件" + localFile.getAbsolutePath() + "不存在或者没有读取权限");
		}
		String[] temp = remoteFilePath.split("/");
		String currentDir = ftpClient.printWorkingDirectory();
		int length = temp.length;
		String fileName = temp[length - 1];
		if (fileName.indexOf(".") == -1) {
			throw new Exception("文件路径不合法");
		}
		if (remoteFilePath.startsWith("/")) {
			cd("/");
		}
		for (String dir : temp) {
			if (dir.trim().length() > 0) {
				if (dir.indexOf(".") == -1) {
					if (!cd(dir)) {
						mkdir(dir);
						cd(dir);
					}
				}
			}
		}
		FileInputStream fis = new FileInputStream(localFile);
		try {
			boolean b = ftpClient.storeFile(fileName, fis);
			cd(currentDir);
			return b;
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
	 * @param localFilePath
	 *            本地文件路径
	 * @param remoteFilePath
	 *            远端文件路径（包含文件名），不存在会自动建立
	 * @return 上传文件到远端服务器是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean putToFile(String localFilePath, String remoteFilePath) throws Exception {
		File file = new File(localFilePath);
		return putToFile(file, remoteFilePath);
	}

	/**
	 * 上传文件到远端目录
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remoteDirPath
	 *            远端目录路径，不存在会自动建立
	 * @return 上传文件到远端目录是否成功
	 * @throws Exception
	 */
	public boolean putToPath(File localFile, String remoteDirPath) throws Exception {
		if (!localFile.isFile()) {
			throw new FileNotFoundException("文件" + localFile.getAbsolutePath() + "不存在或者没有读取权限");
		}
		String[] temp = remoteDirPath.split("/");
		String currentDir = ftpClient.printWorkingDirectory();
		if (remoteDirPath.startsWith("/")) {
			cd("/");
		}
		for (String dir : temp) {
			if (dir.trim().length() > 0) {
				if (!cd(dir)) {
					mkdir(dir);
					cd(dir);
				}
			}
		}
		FileInputStream fis = new FileInputStream(localFile);
		try {
			boolean b = ftpClient.storeFile(localFile.getName(), fis);
			cd(currentDir);
			return b;
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
	 * 上传文件到远端目录
	 * 
	 * @param localFilePath
	 *            本地文件路径
	 * @param remoteDirPath
	 *            远端目录路径，不存在会自动建立
	 * @return 上传文件到远端目录是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean putToPath(String localFilePath, String remoteDirPath) throws Exception {
		File file = new File(localFilePath);
		return putToPath(file, remoteDirPath);
	}

	/**
	 * 修改ftp当前目录下的文件名，必须先登录
	 * 
	 * @param srcFileName
	 *            原文件名
	 * @param destFileName
	 *            目标文件名
	 * @return 修改ftp当前目录下的文件名是否成功
	 * @throws Exception
	 *             异常
	 */
	public boolean rename(String srcFileName, String destFileName) throws Exception {
		return ftpClient.rename(srcFileName, destFileName);
	}

	/**
	 * 设置文件传输模式为文本模式,必须先登录
	 * 
	 * @return 是否设置成功
	 * @throws Exception
	 *             异常
	 */
	public boolean setAscii() throws Exception {

		return ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);

	}

	/**
	 * 设置文件传输模式为二进制模式，本模式为默认模式,必须先登录
	 * 
	 * @return 是否设置成功
	 * @throws Exception
	 *             异常
	 */
	public boolean setBinary() throws Exception {
		return ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	}

	/**
	 * 设置服务器的编码格式
	 * 
	 * @param encoding
	 *            服务器的编码格式
	 */
	public void setEncoding(String encoding) {
		ftpClient.setControlEncoding(encoding);
	}

	/**
	 * 设置本地路径
	 * 
	 * @param path
	 *            本地路径
	 */
	public void setLocalDir(String path) {
		this.localDir = path;
	}
}
