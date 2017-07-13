# sql2java-2-6-7

sql2java是一款轻量级的历史悠久的 java ORM 工具.

>官网地址:[https://sourceforge.net/projects/sql2java](https://sourceforge.net/projects/sql2java)

可惜早已经不再维护，最后一个版本是[sql2java-2-6-7.zip][1]

从2010年起我就一直在用这个工具生成的数据库访问代码。它体积很小，结构却非常好，支持很多主流的数据库，生成代码非常方便，而且可以自由定制生成的代码，重要的是很稳定。

然而一年前，我发现这个工具无法支持BLOB,对CLOB类型的支持也不好，当时绕过了这个问题。现在因为项目需要必须要用到BLOB类型，就下决心解决这个问题。

sql2java的官网上虽然有源码,但svn库中因为没有tag,已经无法溯源找到2.6.7版本对应的源码，于是我用CFR反编译器对核心库sql2java.jar反编译得到了源码。

通过研究反编译的源码，找到了sql2java不支持BLOB的原因,并在此基础上做了较少的修改，就实现了对BLOB,CLOB类型的支持。

## 更新范围

整个修复只涉及两个文件：

	lib/sql2java.jar中的net/sourceforge/sql2java/Column.class(另加入了对应的源码Column.java,基于反编译的源码修复后的代码)
	src/templates/velocity/java5/perschema/manager.java.vm(velocity模板文件)

对sql2java的接口没有做任何修改。

### 注意

因为只对java5下的perschema/manager.java.vm模板文件进行了修改。所以此版本只保证
`sql2java.properties`文件中`template.folder.include`使用 `java5` 而不是 `java` 时能生成正确的代码。


## 代码说明

为了便于使用，同时也为了隐藏数据库访问相关的细节，在生成的Java Bean类中Blob对象被解析为byte[],Clob对象被解析为java.lang.String

示例代码如下

    /**
     * Getter method for colorImage.
     * <br>
     * Meta Data Information (in progress):
     * <ul>
     * <li>full name: TEST_USER.COLOR_IMAGE</li>
     * <li>column size: 4000</li>
     * <li>jdbc type returned by the driver: Types.CLOB</li>
     * </ul>
     *
     * @return the value of colorImage
     */
    public String getColorImage()
    {
        return colorImage;
    }

    /**
     * Setter method for colorImage.
     * <br>
     * The new value is set only if compareTo() says it is different,
     * or if one of either the new value or the current value is null.
     * In case the new value is different, it is set and the field is marked as 'modified'.
     *
     * @param newVal the new value to be assigned to colorImage
     */
    public void setColorImage(String newVal)
    {
        if ((newVal != null && colorImage != null && (newVal.compareTo(colorImage) == 0)) ||
            (newVal == null && colorImage == null && colorImageIsInitialized)) {
            return;
        }
        colorImage = newVal;
        colorImageIsModified = true;
        colorImageIsInitialized = true;
    }
    /**
     * Getter method for grayImage.
     * <br>
     * Meta Data Information (in progress):
     * <ul>
     * <li>full name: TEST_USER.GRAY_IMAGE</li>
     * <li>column size: 4000</li>
     * <li>jdbc type returned by the driver: Types.BLOB</li>
     * </ul>
     *
     * @return the value of grayImage
     */
    public byte[] getGrayImage()
    {
        return grayImage;
    }

    /**
     * Setter method for grayImage.
     * <br>
     * Attention, there will be no comparison with current value which
     * means calling this method will mark the field as 'modified' in all cases.
     *
     * @param newVal the new value to be assigned to grayImage
     */
    public void setGrayImage(byte[] newVal)
    {
        if ((newVal != null && grayImage != null && newVal.equals(grayImage)) ||
            (newVal == null && grayImage == null && grayImageIsInitialized)) {
            return;
        }
        grayImage = newVal;
        grayImageIsModified = true;
        grayImageIsInitialized = true;
    }

## author
	GuYaDong 10km0811@sohu.com



[1]:https://nchc.dl.sourceforge.net/project/sql2java/sql2java-distribution/sql2java%202.6.7/sql2java-2-6-7.zip