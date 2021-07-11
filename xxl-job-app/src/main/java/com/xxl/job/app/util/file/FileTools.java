package com.xxl.job.app.util.file;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class FileTools {



	/**
	 * 将字符串追加到文本文件，如果文件已存在，追加到原来的内容后
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 */
	public static void appendTxt(String str, String fileName) throws Exception {
		write(str, fileName, null, true);
	}

	/**
	 * 将字符串追加到文本文件，如果文件已存在，追加到原来的内容后
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @throws Exception
	 */
	public static void appendTxt(String str, String fileName, String encoding)
			throws Exception {
		write(str, fileName, encoding, true);
	}

	/**
	 * 文件复制
	 * 
	 * @param from
	 *            要复制的文件
	 * @param to
	 *            复制到哪个文件
	 * @throws Exception
	 */
	public static final void copy(String from, String to) throws Exception {
		makeParentDir(to);
		FileInputStream fis = new FileInputStream(from);
		try {
			FileOutputStream fos = new FileOutputStream(to, false);
			try {
				byte[] buf = new byte[1024 * 16];
				int size = 0;
				while ((size = fis.read(buf)) != -1) {
					fos.write(buf, 0, size);
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				fos.flush();
				fos.close();
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			fis.close();
		}
	}

	/**
	 * 文件复制目录
	 * 
	 * @param from
	 *            要复制的文件目录
	 * @param to
	 *            复制到哪个文件目录
	 * @param recursive
	 *            是否递归查找
	 * @throws Exception
	 */
	public static final void copyDir(String from, String to, boolean recursive)
			throws Exception {
		if (from.endsWith("/")) {
			from = from.substring(0, from.length() - 1);
		}
		if (to.endsWith("/")) {
			to = to.substring(0, to.length() - 1);
		}
		File file = new File(from);
		if (file.isDirectory()) {
			String[] fileList = file.list();
			for (int i = 0; i < fileList.length; i++) {
				String tmp = from + "/" + fileList[i];
				if (new File(tmp).isFile() || recursive) {
					copyDir(tmp, to + "/" + fileList[i], recursive);
				}
			}
		} else {
			copy(from, to);
		}
	}

	/**
	 * 根据文件获取文件扩展名.<br>
	 * 例如：1.txt的扩展名就是txt
	 * 
	 * @param file
	 *            文件
	 * @return 文件扩展名
	 */
	public static String getExtFilename(File file) {
		return getExtFilename(file.getName());
	}

	/**
	 * 根据文件名获取文件扩展名.<br>
	 * 例如：1.txt的扩展名就是txt
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件扩展名
	 */
	public static String getExtFilename(String fileName) {
		int i = fileName.lastIndexOf(".");
		if (i >= 0) {
			return fileName.substring(i + 1);
		} else {
			return fileName;
		}
	}

	/**
	 * 计算文件的大小(单位为字节).<br>
	 * 默认递归最大为10级，返回所有文件的大小总和。
	 * 
	 * @param file
	 *            文件/目录的路径
	 * @return 文件/目录的大小(单位为字节).
	 */
	public static long getFileSize(File file) throws Exception {
		return getFileSize(file, true, 10);
	}

	/**
	 * 计算文件的大小(单位为字节).<br>
	 * 若需要递归，则递归最多10级，返回所有文件的大小总和。
	 * 
	 * @param file
	 *            文件/目录的路径
	 * @param recursive
	 *            是否递归
	 * @return 文件/目录的大小(单位为字节).
	 * @throws Exception
	 */
	public static long getFileSize(File file, boolean recursive)
			throws Exception {
		return getFileSize(file, recursive, 10);
	}

	/**
	 * 计算文件的大小(单位为字节).<br>
	 * 根据指定的递归级数，返回所有文件的大小总和。
	 * 
	 * @param file
	 *            文件/目录的路径
	 * @param recursive
	 *            是否递归
	 * @param maxDepth
	 *            递归级数
	 * @return 文件/目录的大小(单位为字节).
	 * @throws Exception
	 */
	public static long getFileSize(File file, boolean recursive, int maxDepth)
			throws Exception {
		// 判断文件是否存在，若不存在则抛出异常
		if (!file.exists()) {
			String message = file + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (recursive) {
			return getFileSize(file, maxDepth, 0);// 实现根据设置的递归级数获取文件大小
		} else {
			return getFileSize(file, 0, 0);// 不递归，则递归级数为0
		}
	}

	/**
	 * 根据指定递归级数获取文件大小总和(单位为字节)
	 * 
	 * @param file
	 *            文件路径
	 * @param maxDepth
	 *            最大的递归级数
	 * @param currentDepth
	 *            当前的递归级数
	 * @return 文件大小总和
	 * @throws Exception
	 */
	private static long getFileSize(File file, int maxDepth, int currentDepth)
			throws Exception {
		long size = 0;

		// 如果输入参数File是文件, 则直接返回文件大小
		if (!file.isDirectory()) {
			return file.length();
		}

		// 如果当前级数已达到了最大的级数，则返回0
		if (currentDepth > maxDepth) {
			return 0L;
		}

		// 获取当前路径下所有文件和目录
		File[] files = file.listFiles();
		if (files == null) {// 若目录有安全限制则会返回null，此时认为这个目录大小为0
			return 0L;
		}
		for (int i = 0; i < files.length; i++) {
			File subFile = files[i];
			size += getFileSize(subFile, maxDepth, currentDepth + 1);
		}
		return size;
	}

	/**
	 * 列出指定目录下的文件清单
	 * 
	 * @param dir
	 *            文件目录
	 * @return 文件清单
	 */
	public static List<File> listFile(File dir) {
		return listFile(dir, true);
	}

	/**
	 * 列出指定目录下的文件清单
	 * 
	 * @param dir
	 *            文件目录
	 * @param recursive
	 *            是否递归查找
	 * @return 文件清单
	 */
	public static List<File> listFile(File dir, boolean recursive) {
		List<File> result = new ArrayList<File>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				result.add(file);
			} else if (file.isDirectory() && recursive) {
				result.addAll(listFile(file, true));
			}
		}
		return result;
	}

	/**
	 * 创建指定文件所在的目录
	 * 
	 * @param fileName
	 *            指定文件名
	 * @throws Exception
	 */
	public static final void makeParentDir(String fileName) throws Exception {
		File file = new File(fileName);
		if (!file.exists()) {
			String parent = file.getParent();
			if (parent != null) {
				new File(parent).mkdirs();
			}
		}
	}

	/**
	 * 根据目录名创建目录
	 * 
	 * @param dirName
	 *            目录名（或绝对路径文件名）
	 * @return 是否创建成功
	 */
	public static boolean mkDir(String dirName) {
		File file = new File(dirName);
		if (file.exists() || file.getParentFile() == null) {
			return true;
		}
		if (dirName.endsWith("/") || dirName.endsWith("\\")) {
			return file.mkdirs();
		}
		return file.getParentFile().mkdirs();
	}

	/**
	 * 从文件中读取二进制字节流
	 * 
	 * @param fileName
	 *            要读取的文件名
	 * @return 读取的内容
	 */
	public static byte[] read(String fileName) throws Exception {
		if (fileName == null) {
			return null;
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(fileName);
			byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return bytes;
		} catch (Exception e) {
			throw e;
		} finally {
			in.close();
		}
	}

	/**
	 * 从文件中读取第一行文本
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件第一行内容
	 */
	public static String readLine(String fileName) throws Exception {
		if (fileName == null) {
			return null;
		}

		FileReader fileReader = new FileReader(fileName);
		try {
			BufferedReader in = new BufferedReader(fileReader, 1024 * 64);
			String line = in.readLine();
			if (line != null) {
				line = line.trim();
			}
			return line;
		} catch (Exception ex) {
			throw ex;
		} finally {
			fileReader.close();
		}
	}

	/**
	 * 从文件中读取第一行文本
	 * 
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @return 文件第一行内容
	 * @throws Exception
	 */
	public static String readLine(String fileName, String encoding)
			throws Exception {
		if (fileName == null) {
			return null;
		}
		FileInputStream fileInputStream = new FileInputStream(fileName);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					fileInputStream, encoding), 1024 * 64);
			String line = in.readLine();
			if (line != null) {
				line = line.trim();
			}
			return line;
		} catch (Exception ex) {
			throw ex;
		} finally {
			fileInputStream.close();
		}
	}

	/**
	 * 从文件中读取各行文本
	 * 
	 * @param file
	 *            文件
	 * @return 各行文本的集合
	 */
	public static List<String> readLines(File file) throws Exception {
		List<String> result = new ArrayList<String>();
		FileReader fileReader = new FileReader(file);
		try {
			BufferedReader bufferedReader = new BufferedReader(fileReader,
					1024 * 64);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (Exception ex) {
			throw ex;
		} finally {
			fileReader.close();
		}
	}

	/**
	 * 从文件中读取各行文本
	 * 
	 * @param file
	 *            文件
	 * @param encoding
	 *            字符集
	 * @return 各行文本的集合
	 */
	public static List<String> readLines(File file, String encoding)
			throws Exception {
		List<String> result = new ArrayList<String>();
		FileInputStream fileInputStream = new FileInputStream(file);
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(fileInputStream, encoding), 1024 * 64);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			return result;
		} catch (Exception ex) {
			throw ex;
		} finally {
			fileInputStream.close();
		}
	}

	/**
	 * 从文件中读取各行文本
	 * 
	 * @param fileName
	 *            文件名
	 * @return 每行文本的集合
	 */
	public static List<String> readLines(String fileName) throws Exception {
		return readLines(new File(fileName));
	}

	/**
	 * 从文件中读取各行文本
	 * 
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @return 每行文本的集合
	 */
	public static List<String> readLines(String fileName, String encoding)
			throws Exception {
		return readLines(new File(fileName), encoding);
	}

	/**
	 * 从文件中读取文本到缓冲
	 * 
	 * @param file
	 *            文件
	 * @return 所读文件内容
	 * @throws Exception
	 */
	public static String readTxt(File file) throws Exception {
		return readTxt(file, null);
	}

	/**
	 * 从文件中读取文本到缓冲
	 * 
	 * @param file
	 *            文件
	 * @param encoding
	 *            字符集
	 * @return 所读文件内容
	 * @throws Exception
	 */
	public static String readTxt(File file, String encoding) throws Exception {
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		FileInputStream fis = new FileInputStream(file);
		try {
			char[] cbuf = new char[100];
			StringBuilder buf = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(fis, encoding);
			int n;
			while ((n = isr.read(cbuf)) >= 0) {
				buf.append(cbuf, 0, n);
			}
			return buf.toString();
		} catch (Exception ex) {
			throw ex;
		} finally {
			fis.close();
		}
	}

	/**
	 * 从文件中读取文本到缓冲
	 * 
	 * @param fileName
	 *            文件名
	 * @return 所读文件内容
	 * @throws Exception
	 */
	public static String readTxt(String fileName) throws Exception {
		return readTxt(new File(fileName), null);
	}

	/**
	 * 从文件中读取文本到缓冲
	 * 
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @return 所读文件内容
	 * @throws Exception
	 */
	public static String readTxt(String fileName, String encoding)
			throws Exception {
		return readTxt(new File(fileName), encoding);
	}

	/**
	 * 删除目录
	 * 
	 * @param dir
	 *            文件目录
	 */
	public static void rmDir(File dir) {
		if (!dir.exists()) {
			return;
		}
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					rmDir(file);
				}
				file.delete();
			}
		}
		dir.delete();
	}

	/**
	 * 把二进制字节流写入文件
	 * 
	 * @param bytes
	 *            要写出的字节数组
	 * @param fileName
	 *            文件名
	 */
	public static void write(byte[] bytes, String fileName) throws Exception {
		if (bytes == null || fileName == null) {
			return;
		}
		mkDir(fileName); // 写文件需要检查相关目录是否存在，没有的话需要创建
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(bytes);
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
		}
	}

	/**
	 * 写文件操作
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @param isAppend
	 *            是否追加，如果值为false，则覆盖掉以前的内容
	 * @throws Exception
	 */
	public static void write(String str, String fileName, boolean isAppend)
			throws Exception {
		write(str, fileName, null, isAppend);
	}

	/**
	 * 写文件操作
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @param isAppend
	 *            是否追加，如果值为false，则覆盖掉以前的内容
	 * @throws Exception
	 */
	public static void write(String str, String fileName, String encoding,
			boolean isAppend) throws Exception {
		if (fileName == null || str == null) {
			return;
		}
		File dir = new File(fileName).getParentFile();
		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		if (encoding == null) {
			encoding = Charset.defaultCharset().name();
		}
		FileOutputStream fOutput = new FileOutputStream(fileName, isAppend);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(fOutput, encoding);
			try {
				osw.write(str);
				osw.flush();
			} catch (Exception ex) {
				throw ex;
			} finally {
				osw.close();
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			fOutput.close();
		}
	}

	/**
	 * 将字符串写出到文本文件，如果文件已存在，将覆盖原来的内容
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 */
	public static void writeTxt(String str, String fileName) throws Exception {
		write(str, fileName, null, false);
	}

	/**
	 * 将字符串写出到文本文件，如果文件已存在，将覆盖原来的内容
	 * 
	 * @param str
	 *            要写出的内容
	 * @param fileName
	 *            文件名
	 * @param encoding
	 *            字符集
	 * @throws Exception
	 */
	public static void writeTxt(String str, String fileName, String encoding)
			throws Exception {
		write(str, fileName, encoding, false);
	}
}
