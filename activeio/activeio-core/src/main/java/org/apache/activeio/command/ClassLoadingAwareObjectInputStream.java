begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|command
package|;
end_package

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
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectStreamClass
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_comment
comment|/**  * An input stream which uses the {@link org.apache.activeio.command.ClassLoading} helper class  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ClassLoadingAwareObjectInputStream
extends|extends
name|ObjectInputStream
block|{
specifier|private
specifier|static
specifier|final
name|ClassLoader
name|myClassLoader
init|=
name|DefaultWireFormat
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
specifier|public
name|ClassLoadingAwareObjectInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Class
name|resolveClass
parameter_list|(
name|ObjectStreamClass
name|classDesc
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
return|return
name|loadClass
argument_list|(
name|classDesc
operator|.
name|getName
argument_list|()
argument_list|,
name|classLoader
argument_list|)
return|;
block|}
specifier|protected
name|Class
name|resolveProxyClass
parameter_list|(
name|String
index|[]
name|interfaces
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Class
index|[]
name|interfaceClasses
init|=
operator|new
name|Class
index|[
name|interfaces
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|interfaceClasses
index|[
name|i
index|]
operator|=
name|loadClass
argument_list|(
name|interfaces
index|[
name|i
index|]
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|Proxy
operator|.
name|getProxyClass
argument_list|(
name|interfaceClasses
index|[
literal|0
index|]
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|interfaceClasses
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|null
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|Class
name|loadClass
parameter_list|(
name|String
name|className
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
try|try
block|{
return|return
name|ClassLoading
operator|.
name|loadClass
argument_list|(
name|className
argument_list|,
name|classLoader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
name|ClassLoading
operator|.
name|loadClass
argument_list|(
name|className
argument_list|,
name|myClassLoader
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

