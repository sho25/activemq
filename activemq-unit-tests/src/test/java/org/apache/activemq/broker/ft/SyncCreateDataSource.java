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
name|broker
operator|.
name|ft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
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
name|java
operator|.
name|sql
operator|.
name|SQLFeatureNotSupportedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
import|;
end_import

begin_comment
comment|// prevent concurrent calls from attempting to create the db at the same time
end_comment

begin_comment
comment|// can result in "already exists in this jvm" errors
end_comment

begin_class
specifier|public
class|class
name|SyncCreateDataSource
implements|implements
name|DataSource
block|{
specifier|final
name|EmbeddedDataSource
name|delegate
decl_stmt|;
specifier|public
name|SyncCreateDataSource
parameter_list|(
name|EmbeddedDataSource
name|dataSource
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|dataSource
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|delegate
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|SQLException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
return|return
name|delegate
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|SQLException
block|{     }
annotation|@
name|Override
specifier|public
name|int
name|getLoginTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLoginTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SQLException
block|{     }
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|EmbeddedDataSource
name|getDelegate
parameter_list|()
block|{
return|return
name|delegate
return|;
block|}
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
throws|throws
name|SQLFeatureNotSupportedException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

