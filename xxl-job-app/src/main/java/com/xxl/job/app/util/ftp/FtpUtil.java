package com.xxl.job.app.util.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * FTP处理公共类
 * @author wuyq
 *
 */
public class FtpUtil {

	/** 日志 */
	private static final Logger logger = Logger.getLogger(FtpUtil.class);

	/** 创建FTP客户端对象 */
	FTPClientTool ftp = new FTPClientTool();

	/** 编码 */
	static final String ENCODING = "GB2312";

	public FtpUtil(int timeOut,String encoding) {
		ftp.setTimeOut(timeOut);
		ftp.setEncoding(encoding);
	}

	public FtpUtil(int timeOut) {
		ftp.setTimeOut(timeOut);
		ftp.setEncoding(ENCODING);
	}
	
	/**
	 * 根据ftp路径获取文件夹列表
	 * 
	 * @param ftpDirPath
	 *            路径 须是绝对路径
	 * @return
	 * @throws Exception
	 *             ftp获取异常
	 */
	public List<String> getFtpDirList(String ftpDirPath) throws Exception {
		return ftp.getDirList(ftpDirPath);
	}

	/**
	 * 根据ftp路径获取文件列表
	 * 
	 * @param ftpDirPath
	 *            路径 须是绝对路径
	 * @return
	 * @throws Exception
	 *             ftp获取异常
	 */
	public List<String> getFtpFileList(String ftpDirPath) throws Exception {
		return ftp.getFileList(ftpDirPath);
	}

	/**
	 * 根据ftp路径获取文件列表 <br>
	 * 文件的修改时间必须在指定时间之前
	 * 
	 * @param ftpDirPath
	 *            路径 须是绝对路径
	 * @param regex
	 *            文件名称所匹配的正则
	 * @return
	 * @throws Exception
	 *             ftp获取异常
	 */
	public List<String> getFtpFileList(String ftpDirPath, String regex) throws Exception {
		List<String> nameList = new ArrayList<String>();
		List<FTPFile> fileList = null;
		try {
			fileList = ftp.getFTPFileList(ftpDirPath, regex);
		}
		catch (Exception e) {
			logger.error("文件夹:" + ftpDirPath + "  获取列表异常:", e);
		}
		if (fileList == null || fileList.isEmpty()) {
			return nameList;
		}
		StringBuilder debugStr = new StringBuilder();
		debugStr.setLength(0);
		String name = "";
		// ftp文件大小大于这个值时进行拷贝
		for (FTPFile file : fileList) {
			name = file.getName();
			nameList.add(name);
			debugStr.append(name);
		}
		logger.debug("文件夹:" + ftpDirPath + " 下的文件列表为:" + debugStr);
		return nameList;
	}

	/**
	 * 给文件改名 <br>
	 * 使得拷贝文件的线程只需要拷贝特定名称的文件
	 * 
	 * @param recordMap
	 *            记录map
	 * @param ftpDirPath
	 *            文件路径
	 * @param ext
	 *            不能拷贝文件的后缀名
	 */
	public void renameFtpFile(Map<String, Long> recordMap, String ftpDirPath, String ext) {
		List<FTPFile> fileList = null;
		try {
			fileList = ftp.getFTPFileList(ftpDirPath);
		}
		catch (Exception e) {
			logger.error("文件夹:" + ftpDirPath + "  获取列表异常:", e);
		}
		String name = "";
		// ftp文件大小大于这个值时进行拷贝
		String tmpPath = "";
		if (fileList != null && !fileList.isEmpty()) {
			for (FTPFile file : fileList) {
				name = file.getName();
				tmpPath = ftpDirPath + File.separator + name;
				long size = file.getSize();
				if (recordMap.isEmpty() || recordMap.containsKey(tmpPath)) {
					recordMap.put(tmpPath, size);
					continue;
				}
				long oldSize = recordMap.get(tmpPath);
				int index = name.lastIndexOf(ext);
				logger.debug("文件:" + tmpPath + " 原始大小:" + oldSize + " ,最新大小:" + size);
				if (oldSize == size) {
					name = index > 0 ? name.substring(0, index) : name;
					recordMap.remove(tmpPath);
				} else {
					name = index > 0 ? name : name + ext;
					recordMap.put(tmpPath, size);
				}
				// 改名
				file.setName(name);
			}
		}

		List<String> dirList = null;
		try {
			dirList = ftp.getDirList(ftpDirPath);
		}
		catch (Exception e) {
			logger.error("文件夹:" + ftpDirPath + "  获取列表异常:", e);
		}
		if (dirList == null || dirList.isEmpty()) {
			return;
		}
		for (String fileName : dirList) {
			String absPath = ftpDirPath + File.separator + fileName;
			// 轮询的检查 各个子目录
			renameFtpFile(recordMap, absPath, ext);
		}
	}

	/**
	 * 获取ftp文件内容
	 * 
	 * @param ftpFilePath
	 *            文件内容
	 * @return
	 */
	public String getFtpContent(String ftpFilePath) {
		String content = "";
		try {
			byte[] data = ftp.get(ftpFilePath);
			content = new String(data, ENCODING);
		}
		catch (Exception e) {
			logger.error("ftp远程获取文件内容异常:" + ftpFilePath, e);
		}

		return content;
	}

	/**
	 * 删除ftp文件
	 * 
	 * @param ftpFilePath
	 *            ftp文件路径
	 * @return
	 */
	public boolean delFtpFile(String ftpFilePath) {
		boolean result = false;
		try {
			result = ftp.deleteFile(ftpFilePath);
		}
		catch (Exception e) {
			try {
				result = ftp.deleteFile(ftpFilePath);
			}
			catch (Exception ex) {
				logger.error("删除ftp文件异常:", e);
			}
		}
		return result;
	}

	/**
	 * 删除ftp目录
	 * 
	 * @param ftpDirPath
	 *            ftp文件夹路径
	 */
	public boolean delFtpDir(String ftpDirPath) {
		boolean result = false;
		try {
			ftp.delDirFiles(ftpDirPath);
			result = true;
		}
		catch (Exception e) {
			try {
				ftp.delDirFiles(ftpDirPath);
				result = true;
			}
			catch (Exception ex) {
				logger.error("删除ftp文件夹异常:", e);
			}
		}
		return result;
	}

	/**
	 * ftp链接打开
	 * 
	 * @param ip
	 *            ip地址
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return 打开结果
	 */
	public boolean openFtp(String ip, int port, String userName, String password) {
		boolean result = false;
		try {
			// 连接服务器
			logger.info("打开ftp服务器:" + ip + ",port:" + port + ",userName:" + userName + ",password:" + password);
			ftp.connect(ip, port, userName, password);
			logger.info("打开ftp服务器成功!");
			result = true;
		}
		catch (Exception e) {
			logger.error("打开ftp服务器异常:" + ip + ",port:" + port + ",userName:" + userName + ",password:" + password, e);
		}

		return result;
	}

	/**
	 * 关闭ftp链接
	 * 
	 * @return
	 */
	public boolean closeFtp() {
		boolean result = false;
		try {
			// 关闭链接
			ftp.close();
			result = true;
		}
		catch (Exception e) {
			try {
				// 关闭链接
				ftp.close();
				result = true;
			}
			catch (Exception ex) {
				logger.error("关闭服务器异常:", e);
			}
		}
		return result;
	}

	/**
	 * 从ftp处获取文件到本地文件夹
	 * 
	 * @param remotePath
	 *            远程文件夹
	 * @param localPath
	 *            本地文件夹
	 * @return
	 */
	public boolean getFtpFile2Local(String remotePath, String localPath) {
		boolean result = false;

		try {
			logger.debug("拷贝文件:" + remotePath + " 到本地:" + localPath);
			ftp.get(remotePath, localPath);
			result = true;
		}
		catch (Exception e) {
			logger.error("拷贝文件:" + remotePath + "异常:", e);
		}

		return result;
	}

	/**
	 * 从ftp处获取文件到本地文件夹
	 * 
	 * @param remotePath
	 *            远程文件夹
	 * @param localPath
	 *            本地文件夹
	 * @param localName
	 *            本地文件名称
	 * @return
	 */
	public boolean getFtpFile2Local(String remotePath, String localPath, String localName) {
		boolean result = false;

		try {
			logger.info("拷贝文件:" + remotePath + " 到本地:" + localPath);
			ftp.get(remotePath, localPath, localName);
			result = true;
		}
		catch (Exception e) {
			logger.error("拷贝文件:" + remotePath + "异常:", e);
		}

		return result;
	}

	/**
	 * 根据路径获取ftp文件对象
	 * 
	 * @param ftpFilePath
	 *            绝对路径
	 * @return
	 * @throws Exception
	 */
	public List<FTPFile> getFTPFileList(String ftpFilePath) throws Exception {
		return ftp.getFTPFileList(ftpFilePath);
	}

	/**
	 * 根据路径获取ftp文件夹对象
	 * 
	 * @param ftpFilePath
	 *            绝对路径
	 * @return
	 * @throws Exception
	 */
	public List<FTPFile> getFTPDirList(String ftpFilePath) throws Exception {
		return ftp.getFTPDirList(ftpFilePath);
	}

	/**
	 * 上文件到ftp目录
	 * 
	 * @param fileName
	 *            文件名称 文件全路径
	 * @param ftpPath
	 *            ftp路径
	 * @return
	 * @throws Exception
	 */
	public boolean putFileOnFtp(String fileName, String ftpPath) throws Exception {
		boolean result = false;
		try {
			ftp.put(fileName, ftpPath);
			result = true;
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 *            路径
	 * @return
	 * @throws Exception
	 */
	public boolean mkdir(String path) throws Exception {
		boolean result = false;
		try {
			ftp.mkdir(path);
			result = true;
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

}
