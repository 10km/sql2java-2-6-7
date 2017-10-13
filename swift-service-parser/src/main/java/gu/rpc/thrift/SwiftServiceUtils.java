package gu.rpc.thrift;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.service.metadata.ThriftServiceMetadata;
import com.google.common.base.Preconditions;

/**
 * 解析swift service类型,返回 {@link ThriftServiceMetadata}实例
 * @author guyadong
 *
 */
public class SwiftServiceUtils {
	public static ThriftServiceMetadata getServiceMetadata(Class<?>swiftServiceType){
		Preconditions.checkNotNull(swiftServiceType, "swiftServiceType is null");
		return  new ThriftServiceMetadata(swiftServiceType,new ThriftCatalog());		
	}
	public static ThriftServiceMetadata getServiceMetadata(String thriftServiceClassName) throws ClassNotFoundException{
		return getServiceMetadata(thriftServiceClassName,(URLClassLoader)null);
	}
	public static ThriftServiceMetadata getServiceMetadata(String thriftServiceClassName,ClassLoader classLoader) throws ClassNotFoundException{
		Preconditions.checkNotNull(thriftServiceClassName,"thriftServiceClass is null");
		Class<?> swiftServiceType;
		if(null == classLoader)
			classLoader =Thread.currentThread().getContextClassLoader();
		swiftServiceType = Class.forName(thriftServiceClassName,true,classLoader);
		return getServiceMetadata(swiftServiceType);
	}
	
	public static ThriftServiceMetadata getServiceMetadata(String thriftServiceClassName,String... classPath) throws ClassNotFoundException{
		if(null == classPath || 0 == classPath.length)
			throw new IllegalArgumentException("classPath must not be null or empty");
		return getServiceMetadata(thriftServiceClassName,makeURLClassLoader(classPath));
	}
	
	private static URLClassLoader makeURLClassLoader(String[] classPath){
		URL[] urls = new URL[classPath.length];
		for(int i=0;i<urls.length;++i){
			String path = classPath[i];
			Preconditions.checkNotNull(path,"classPath have null or empty element");
			File file = new File(path);
			if(file.exists())
				throw new IllegalArgumentException("no exists : "+ file.toString());
			try {
				urls[i]= file.getAbsoluteFile().toURI().toURL();					
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return URLClassLoader.newInstance(urls);
	}
	private SwiftServiceUtils() {}

}
