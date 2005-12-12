begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_class
specifier|public
class|class
name|FactoryFinder
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentHashMap
name|classMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|FactoryFinder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/**      * Creates a new instance of the given key      *      * @param key is the key to add to the path to find a text file      *            containing the factory name      * @return a newly created instance      */
specifier|public
name|Object
name|newInstance
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|IOException
throws|,
name|ClassNotFoundException
block|{
return|return
name|newInstance
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Object
name|newInstance
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|propertyPrefix
parameter_list|)
throws|throws
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|IOException
throws|,
name|ClassNotFoundException
block|{
if|if
condition|(
name|propertyPrefix
operator|==
literal|null
condition|)
name|propertyPrefix
operator|=
literal|""
expr_stmt|;
name|Class
name|clazz
init|=
operator|(
name|Class
operator|)
name|classMap
operator|.
name|get
argument_list|(
name|propertyPrefix
operator|+
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
name|clazz
operator|=
name|newInstance
argument_list|(
name|doFindFactoryProperies
argument_list|(
name|key
argument_list|)
argument_list|,
name|propertyPrefix
argument_list|)
expr_stmt|;
name|classMap
operator|.
name|put
argument_list|(
name|propertyPrefix
operator|+
name|key
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
return|return
name|clazz
operator|.
name|newInstance
argument_list|()
return|;
block|}
specifier|private
name|Class
name|newInstance
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|propertyPrefix
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|IOException
block|{
name|String
name|className
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|propertyPrefix
operator|+
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expected property is missing: "
operator|+
name|propertyPrefix
operator|+
literal|"class"
argument_list|)
throw|;
block|}
name|Class
name|clazz
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|clazz
operator|=
name|FactoryFinder
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
return|return
name|clazz
return|;
block|}
specifier|private
name|Properties
name|doFindFactoryProperies
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|uri
init|=
name|path
operator|+
name|key
decl_stmt|;
comment|// lets try the thread context class loader first
name|InputStream
name|in
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
name|in
operator|=
name|FactoryFinder
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not find factory class for resource: "
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
comment|// lets load the file
name|BufferedInputStream
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|properties
return|;
block|}
finally|finally
block|{
try|try
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
block|}
block|}
end_class

end_unit

