package gu.rpc.thrift;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassLoaderUtils {

	private ClassLoaderUtils() {
	}

	private static final boolean addIfJar(Collection<URL>out,File file){
		if (file.isFile() && file.getName().endsWith(".jar")){
			out.add(toURL(file));
			return true;
		}
		return false;
	}

	private static final URL toURL(File file){
		try{
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 如果 {@code file} 是文件夹，则文件夹所有jar包文件生成{@link URL} 集合<br>
	 * 如果{@code file}是jar,则将返回包含该jar的{@link URL} 集合<br>
	 * 如果不是jar抛出异常
	 * @param out
	 * @param file 不存在时抛出异常
	 * @param recursive 为 {@code true}时递归搜索子目录,{@code file}为文件夹时有效
	 * @return 
	 */
	private static final Set<URL> toJarURLs(File file,final boolean recursive){
		final HashSet<URL> out = new HashSet<URL>();
		if(!file.exists())
			throw new IllegalArgumentException(" not exists:" + file.toString());
		if(file.isFile()){
			if(!addIfJar(out,file))
				throw new IllegalArgumentException(" not jar:" + file.toString());
		}
		if(file.isDirectory()){
			file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (!addIfJar(out,pathname) && pathname.isDirectory() && recursive) {
						out.addAll(toJarURLs(pathname, recursive));
					}
					return false;
				}
			});
			return out;
		}
		throw new IllegalArgumentException(" invalid file:" + file.toString());
	}

	private final static Set<URL> toJarURLs(String path,final boolean recursive){
		if(null == path || 0 == path.length())
			throw new IllegalArgumentException("path must not be  null or empty ");
		return toJarURLs(new File(path),recursive);
	}

	private final  static Set<URL> toJarURLs(final boolean recursive,String... paths){
		if(null == paths)
			throw new NullPointerException("paths is null");
		HashSet<URL> out = new HashSet<URL>();
		for(String path:paths){
			out.addAll(toJarURLs(path,recursive));
		}
		return out;
	}

	private final  static Set<URL> toJarURLs(String[] classpath){
		if(null == classpath)
			throw new NullPointerException("classpaths is null");
		final HashSet<URL> out = new HashSet<URL>();
		for(String path:classpath){
			if(null == path || 0 == path.length())
				throw new IllegalArgumentException("classPaths have null or empty element");
			File file = new File(path);
			if(!file.exists())
				throw new IllegalArgumentException("no exists : "+ file);
			if(file.isFile()){
				if(!addIfJar(out,file))
					throw new IllegalArgumentException("not jar:"+ file);
			}else if(file.isDirectory())
				out.add(toURL(file));
		}
		return out;
	}

	/**
	 * 根据{@code classpath}提供的路径创建{@link URLClassLoader}实例
	 * @param classpath jar包或class文件夹路径
	 * @return
	 */
	public final  static URLClassLoader makeURLClassLoader(String[] classpath){
		final Set<URL> out = toJarURLs(classpath);
		if(out.isEmpty())
			throw new IllegalArgumentException("empty classpath");
		return URLClassLoader.newInstance(out.toArray(new URL[out.size()]));
	}

	/**
	 * 根据 {@code paths}提供的lib路径创建{@link URLClassLoader}实例
	 * @param recursive  指示是否递归搜索文件夹,path是文件夹时有效
	 * @param paths path列表,path为jar包或jar所在文件夹(such as 'lib'),不可为空
	 * @return
	 */
	public final  static URLClassLoader makeURLClassLoader(boolean recursive,String... paths){
		Set<URL> out = toJarURLs(recursive,paths);
		if(out.isEmpty())
			throw new IllegalArgumentException("empty paths" );
		return URLClassLoader.newInstance(out.toArray(new URL[out.size()]));
	}

}
