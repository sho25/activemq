begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|ClassLoaderSPIConnectionFactory
implements|implements
name|SPIConnectionFactory
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ClassLoaderSPIConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|KEY_EXT_DIR
init|=
literal|"extDir"
decl_stmt|;
specifier|public
specifier|final
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Load new context class loader
name|ClassLoader
name|newClassLoader
init|=
name|getContextClassLoader
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|newClassLoader
argument_list|)
expr_stmt|;
return|return
name|instantiateConnectionFactory
argument_list|(
name|settings
argument_list|)
return|;
block|}
specifier|protected
name|ClassLoader
name|getContextClassLoader
parameter_list|(
name|Properties
name|settings
parameter_list|)
block|{
name|String
name|extDir
init|=
operator|(
name|String
operator|)
name|settings
operator|.
name|remove
argument_list|(
name|KEY_EXT_DIR
argument_list|)
decl_stmt|;
if|if
condition|(
name|extDir
operator|!=
literal|null
condition|)
block|{
name|StringTokenizer
name|tokens
init|=
operator|new
name|StringTokenizer
argument_list|(
name|extDir
argument_list|,
literal|";,"
argument_list|)
decl_stmt|;
name|List
name|urls
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|tokens
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|dir
init|=
name|tokens
operator|.
name|nextToken
argument_list|()
decl_stmt|;
try|try
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot find extension dir: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding extension dir: "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|f
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
operator|||
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Adding extension dir: "
operator|+
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load ext dir: "
operator|+
name|dir
operator|+
literal|". Reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|URL
name|u
index|[]
init|=
operator|new
name|URL
index|[
name|urls
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|urls
operator|.
name|toArray
argument_list|(
name|u
argument_list|)
expr_stmt|;
return|return
operator|new
name|URLClassLoader
argument_list|(
name|u
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
return|;
block|}
return|return
name|ClassLoaderSPIConnectionFactory
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
return|;
block|}
specifier|protected
specifier|abstract
name|ConnectionFactory
name|instantiateConnectionFactory
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_class

end_unit

