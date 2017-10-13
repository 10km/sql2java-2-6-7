package gu.rpc.thrift;

import java.io.PrintStream;
import java.net.URLClassLoader;

import com.facebook.swift.codec.metadata.ThriftCatalog;
import com.facebook.swift.codec.metadata.ThriftFieldMetadata;
import com.facebook.swift.service.metadata.ThriftMethodMetadata;
import com.facebook.swift.service.metadata.ThriftServiceMetadata;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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
		if(Strings.isNullOrEmpty(thriftServiceClassName))
			throw new IllegalArgumentException("thriftServiceClass is null");
		Class<?> swiftServiceType;
		if(null == classLoader)
			classLoader =Thread.currentThread().getContextClassLoader();
		swiftServiceType = Class.forName(thriftServiceClassName,false,classLoader);
		return getServiceMetadata(swiftServiceType);
	}
	
	public static ThriftServiceMetadata getServiceMetadata(String thriftServiceClassName,String... classpath) throws ClassNotFoundException{
		return getServiceMetadata(thriftServiceClassName,ClassLoaderUtils.makeURLClassLoader(classpath));
	}
	/**
	 * @param thriftServiceClassName
	 * @param recursive
	 * @param path
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static ThriftServiceMetadata getServiceMetadata(String thriftServiceClassName,boolean recursive,String path) throws ClassNotFoundException{
		return getServiceMetadata(thriftServiceClassName,ClassLoaderUtils.makeURLClassLoader(recursive,path));
	}
	public static final void output(ThriftServiceMetadata metadata, PrintStream stream){
		int mcount=0;
		stream.println(metadata.getName());
		for( ThriftMethodMetadata method: metadata.getDeclaredMethods().values()){
			stream.printf("%d name: %s ", mcount++, method.getName());
			if(!method.getMethod().getName().equals(method.getName()))
				stream.printf("original name: %s ", method.getMethod().getName());
			stream.println();
			int pcount = 0;
			for(ThriftFieldMetadata parameter:method.getParameters()){
				stream.printf("\tparam %d: %s %s\n",pcount++, parameter.getName(),parameter.getThriftType().getJavaType());
			}
		}
	}
	private SwiftServiceUtils() {}

}
