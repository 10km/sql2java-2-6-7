package net.sourceforge.sql2java;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ClassLoaderUtils {
	/**
	 * {@code parent}为{@code null}时策略<br>
	 * {@code defaultParentLoader} 使用default parent class loader,参见{@link URLClassLoader#newInstance(URL[])}<br>
	 * {@code threadContextLoader} 使用当前线程的Thread Context ClassLoader作为parent,参见{@link Thread#getContextClassLoader()} <br>
	 * {@code currentClassLoader} 使用当前类({@link ClassLoaderUtils})的class loader<br>
	 * @author guyadong
	 *
	 */
	public static enum ParentStrategy{
		defaultParentLoader,	threadContextLoader,currentClassLoader
	}
	/**
	 * 默认值 {@link ParentStrategy#currentClassLoader }
	 * @see {@link ParentStrategy}
	 */
	private static final ThreadLocal<ParentStrategy> parentLoaderStrategy = new ThreadLocal<ParentStrategy>(){
		@Override
		protected ParentStrategy initialValue() {
			return ParentStrategy.currentClassLoader;
		}};

	private static final boolean addIfJar(Collection<URL> out, File file) {
		if (file.isFile() && file.getName().endsWith(".jar")) {
			out.add(toURL(file));
			return true;
		}
		return false;
	}

	private static final URL toURL(File file) {
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回文件{@link URL}集合<br>
	 * 如果 {@code file} 是文件夹，则文件夹所有jar包文件生成{@link URL} 集合<br>
	 * 如果{@code file}是jar,则将返回包含该jar的{@link URL} 集合<br>
	 * 如果不是jar抛出异常
	 * 
	 * @param file
	 *            不存在时抛出异常
	 * @param recursive
	 *            为 {@code true}时递归搜索子目录,{@code file}为文件夹时有效
	 * @return
	 */
	private static final Set<URL> toJarURLs(File file, final boolean recursive) {
		final HashSet<URL> out = new HashSet<URL>();
		if (!file.exists())
			throw new IllegalArgumentException(" not exists:" + file.toString());
		if (file.isFile()) {
			if (!addIfJar(out, file))
				throw new IllegalArgumentException(" not jar:" + file.toString());
		}
		if (file.isDirectory()) {
			file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (!addIfJar(out, pathname) && pathname.isDirectory() && recursive) {
						out.addAll(toJarURLs(pathname, recursive));
					}
					return false;
				}
			});
			return out;
		}
		throw new IllegalArgumentException(" invalid file:" + file.toString());
	}

	private final static Set<URL> toJarURLs(String path, final boolean recursive) {
		if (null == path || 0 == path.length())
			throw new IllegalArgumentException("path must not be  null or empty ");
		return toJarURLs(new File(path), recursive);
	}

	private final static Set<URL> toJarURLs(final boolean recursive, String... paths) {
		if (null == paths)
			throw new NullPointerException("paths is null");
		HashSet<URL> out = new HashSet<URL>();
		for (String path : paths) {
			out.addAll(toJarURLs(path, recursive));
		}
		return out;
	}

	private final static Set<URL> toJarURLs(String... classpath) {
		if (null == classpath)
			throw new NullPointerException("classpaths is null");
		final HashSet<URL> out = new HashSet<URL>();
		for (String path : classpath) {
			if (null == path || 0 == path.length())
				throw new IllegalArgumentException("classPaths have null or empty element");
			File file = new File(path);
			if (!file.exists())
				throw new IllegalArgumentException("no exists : " + file);
			if (file.isFile()) {
				if (!addIfJar(out, file))
					throw new IllegalArgumentException("not jar:" + file);
			} else if (file.isDirectory())
				out.add(toURL(file));
		}
		return out;
	}

	private final static Set<URL> toJarURLs(boolean recursive, String[] libdirs, String[] classpath) {
		HashSet<URL> out = new HashSet<URL>();
		if (null != libdirs) {
			out.addAll(toJarURLs(recursive, libdirs));
		}
		if (null != classpath)
			out.addAll(toJarURLs(classpath));
		return out;
	}

	/** @see #makeURLClassLoader(ClassLoader, boolean, String[], String[]) */
	public final static URLClassLoader makeURLClassLoader(ClassLoader parent, String... classpath) {
		return makeURLClassLoader(parent, false, null, classpath);
	}

	/** @see #makeURLClassLoader(ClassLoader, boolean, String[], String[]) */
	public final static URLClassLoader makeURLClassLoader(ClassLoader parent, boolean recursive, String... libdirs) {
		return makeURLClassLoader(parent, recursive, libdirs, null);
	}

	/**
	 * 根据{@code libdirs}提供的lib路径和{@code classpath}创建{@link URLClassLoader}实例<br>
	 * 如果所有的参数中都没有找URL(jar或class 文件夹),则抛出异常
	 * @param parent 指定父类加载器,为null时根据{@link #parentLoaderStrategy}决定parent
	 * @param recursive
	 *            指示是否递归搜索文件夹,对 {@code libdirs}有效,see also
	 *            {@link #toJarURLs(File, boolean)}
	 * @param libdirs
	 *            path列表,path为jar包或jar所在文件夹(such as 'lib')
	 * @param classpath
	 *            jar包或class文件夹路径
	 * 
	 * @return
	 * @see {@link URLClassLoader#newInstance(URL[],ClassLoader)}
	 * @see {@linkplain Thread#getContextClassLoader()}
	 */
	public final static URLClassLoader makeURLClassLoader(ClassLoader parent, boolean recursive, String[] libdirs, String[] classpath) {
		Set<URL> out = toJarURLs(recursive, libdirs, classpath);
		if (out.isEmpty())
			throw new IllegalArgumentException("empty libdirs and classpath");
		if(null == parent){
			switch(parentLoaderStrategy.get()){
			case defaultParentLoader:
				return URLClassLoader.newInstance(out.toArray(new URL[out.size()]));
			case threadContextLoader:
				parent = Thread.currentThread().getContextClassLoader();
				break;
			case currentClassLoader:	
				parent = ClassLoaderUtils.class.getClassLoader();
				break;
			}
		}
		return URLClassLoader.newInstance(out.toArray(new URL[out.size()]),parent);
	}
	/** @see #makeURLClassLoader(ClassLoader, boolean, String[], String[]) */
	public final static URLClassLoader makeURLClassLoader(ClassLoader parent, boolean recursive,
			Collection<String> libdirs, Collection<String> classpath) {
		return makeURLClassLoader(parent, recursive,
				null == libdirs ? null : libdirs.toArray(new String[0]), null == classpath ? null : classpath.toArray(new String[0]));
	}
	/**
	 * @param parentLoaderStrategy if null,{@link ParentStrategy#currentClassLoader} instead
	 * @see #makeURLClassLoader(ClassLoader, boolean, String[], String[])
	 * @see #parentLoaderStrategy 
	 */
	public final static void setParentLoaderStrategy(ParentStrategy parentLoaderStrategy){
			ClassLoaderUtils.parentLoaderStrategy.set(null == parentLoaderStrategy
					?ParentStrategy.currentClassLoader
					:parentLoaderStrategy);
	}
	/** @see #setParentLoaderStrategy(ParentStrategy) */
	public final static void setParentLoaderStrategy(String parentLoaderStrategy){
		setParentLoaderStrategy(ParentStrategy.valueOf(parentLoaderStrategy));
	}
}
