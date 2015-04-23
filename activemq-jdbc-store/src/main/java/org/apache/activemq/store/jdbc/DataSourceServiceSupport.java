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
name|store
operator|.
name|jdbc
package|;
end_package

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|LockableServiceSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
import|;
end_import

begin_comment
comment|/**  * A helper class which provides a factory method to create a default  * {@link DataSource) if one is not provided.  *   *   */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|DataSourceServiceSupport
extends|extends
name|LockableServiceSupport
block|{
specifier|private
name|String
name|dataDirectory
init|=
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
decl_stmt|;
specifier|private
name|File
name|dataDirectoryFile
decl_stmt|;
specifier|private
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
name|DataSource
name|createdDefaultDataSource
decl_stmt|;
specifier|public
name|DataSourceServiceSupport
parameter_list|()
block|{     }
specifier|public
name|DataSourceServiceSupport
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
block|}
specifier|public
name|File
name|getDataDirectoryFile
parameter_list|()
block|{
if|if
condition|(
name|dataDirectoryFile
operator|==
literal|null
condition|)
block|{
name|dataDirectoryFile
operator|=
operator|new
name|File
argument_list|(
name|getDataDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|dataDirectoryFile
return|;
block|}
specifier|public
name|void
name|setDataDirectoryFile
parameter_list|(
name|File
name|dataDirectory
parameter_list|)
block|{
name|this
operator|.
name|dataDirectoryFile
operator|=
name|dataDirectory
expr_stmt|;
block|}
specifier|public
name|String
name|getDataDirectory
parameter_list|()
block|{
return|return
name|dataDirectory
return|;
block|}
specifier|public
name|void
name|setDataDirectory
parameter_list|(
name|String
name|dataDirectory
parameter_list|)
block|{
name|this
operator|.
name|dataDirectory
operator|=
name|dataDirectory
expr_stmt|;
block|}
specifier|public
name|DataSource
name|getDataSource
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dataSource
operator|==
literal|null
condition|)
block|{
name|dataSource
operator|=
name|createDataSource
argument_list|(
name|getDataDirectoryFile
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataSource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No dataSource property has been configured"
argument_list|)
throw|;
block|}
else|else
block|{
name|createdDefaultDataSource
operator|=
name|dataSource
expr_stmt|;
block|}
block|}
return|return
name|dataSource
return|;
block|}
specifier|public
name|void
name|closeDataSource
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
if|if
condition|(
name|createdDefaultDataSource
operator|!=
literal|null
operator|&&
name|createdDefaultDataSource
operator|.
name|equals
argument_list|(
name|dataSource
argument_list|)
condition|)
block|{
name|shutdownDefaultDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setDataSource
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
block|}
specifier|public
specifier|static
name|DataSource
name|createDataSource
parameter_list|(
name|String
name|homeDir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Setup the Derby datasource.
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.system.home"
argument_list|,
name|homeDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.storage.fileSyncTransactionLog"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"derby.storage.pageCacheSize"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
specifier|final
name|EmbeddedDataSource
name|ds
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|ds
operator|.
name|setDatabaseName
argument_list|(
literal|"derbydb"
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
return|return
name|ds
return|;
block|}
specifier|public
specifier|static
name|void
name|shutdownDefaultDataSource
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
specifier|final
name|EmbeddedDataSource
name|ds
init|=
operator|(
name|EmbeddedDataSource
operator|)
name|dataSource
decl_stmt|;
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
try|try
block|{
name|ds
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|expectedAndIgnored
parameter_list|)
block|{         }
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|dataSource
return|;
block|}
block|}
end_class

end_unit

