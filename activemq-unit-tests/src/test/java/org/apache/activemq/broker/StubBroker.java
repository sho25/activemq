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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidClientIDException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|command
operator|.
name|ConnectionInfo
import|;
end_import

begin_class
specifier|public
class|class
name|StubBroker
extends|extends
name|EmptyBroker
block|{
specifier|public
name|LinkedList
argument_list|<
name|AddConnectionData
argument_list|>
name|addConnectionData
init|=
operator|new
name|LinkedList
argument_list|<
name|AddConnectionData
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|LinkedList
argument_list|<
name|RemoveConnectionData
argument_list|>
name|removeConnectionData
init|=
operator|new
name|LinkedList
argument_list|<
name|RemoveConnectionData
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
class|class
name|AddConnectionData
block|{
specifier|public
specifier|final
name|ConnectionContext
name|connectionContext
decl_stmt|;
specifier|public
specifier|final
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
specifier|public
name|AddConnectionData
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
block|{
name|connectionContext
operator|=
name|context
expr_stmt|;
name|connectionInfo
operator|=
name|info
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|RemoveConnectionData
block|{
specifier|public
specifier|final
name|ConnectionContext
name|connectionContext
decl_stmt|;
specifier|public
specifier|final
name|ConnectionInfo
name|connectionInfo
decl_stmt|;
specifier|public
specifier|final
name|Throwable
name|error
decl_stmt|;
specifier|public
name|RemoveConnectionData
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
name|connectionContext
operator|=
name|context
expr_stmt|;
name|connectionInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|error
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|AddConnectionData
name|data
range|:
name|addConnectionData
control|)
block|{
if|if
condition|(
name|data
operator|.
name|connectionInfo
operator|.
name|getClientId
argument_list|()
operator|!=
literal|null
operator|&&
name|data
operator|.
name|connectionInfo
operator|.
name|getClientId
argument_list|()
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getClientId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidClientIDException
argument_list|(
literal|"ClientID already exists"
argument_list|)
throw|;
block|}
block|}
name|addConnectionData
operator|.
name|add
argument_list|(
operator|new
name|AddConnectionData
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Exception
block|{
name|removeConnectionData
operator|.
name|add
argument_list|(
operator|new
name|RemoveConnectionData
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

