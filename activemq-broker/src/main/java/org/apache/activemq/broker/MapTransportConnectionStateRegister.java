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
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ConnectionId
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
name|ConsumerId
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
name|ProducerId
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
name|SessionId
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|MapTransportConnectionStateRegister
implements|implements
name|TransportConnectionStateRegister
block|{
specifier|private
name|Map
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
name|connectionStates
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|TransportConnectionState
name|registerConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|TransportConnectionState
name|state
parameter_list|)
block|{
name|TransportConnectionState
name|rc
init|=
name|connectionStates
operator|.
name|put
argument_list|(
name|connectionId
argument_list|,
name|state
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|TransportConnectionState
name|unregisterConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|TransportConnectionState
name|rc
init|=
name|connectionStates
operator|.
name|remove
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rc
operator|.
name|getReferenceCounter
argument_list|()
operator|.
name|get
argument_list|()
operator|>
literal|1
condition|)
block|{
name|rc
operator|.
name|decrementReference
argument_list|()
expr_stmt|;
name|connectionStates
operator|.
name|put
argument_list|(
name|connectionId
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|List
argument_list|<
name|TransportConnectionState
argument_list|>
name|listConnectionStates
parameter_list|()
block|{
name|List
argument_list|<
name|TransportConnectionState
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|TransportConnectionState
argument_list|>
argument_list|()
decl_stmt|;
name|rc
operator|.
name|addAll
argument_list|(
name|connectionStates
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|String
name|connectionId
parameter_list|)
block|{
return|return
name|connectionStates
operator|.
name|get
argument_list|(
operator|new
name|ConnectionId
argument_list|(
name|connectionId
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
name|TransportConnectionState
name|cs
init|=
name|lookupConnectionState
argument_list|(
name|id
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot lookup a consumer from a connection that had not been registered: "
operator|+
name|id
operator|.
name|getParentId
argument_list|()
operator|.
name|getParentId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|cs
return|;
block|}
specifier|public
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ProducerId
name|id
parameter_list|)
block|{
name|TransportConnectionState
name|cs
init|=
name|lookupConnectionState
argument_list|(
name|id
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot lookup a producer from a connection that had not been registered: "
operator|+
name|id
operator|.
name|getParentId
argument_list|()
operator|.
name|getParentId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|cs
return|;
block|}
specifier|public
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|SessionId
name|id
parameter_list|)
block|{
name|TransportConnectionState
name|cs
init|=
name|lookupConnectionState
argument_list|(
name|id
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot lookup a session from a connection that had not been registered: "
operator|+
name|id
operator|.
name|getParentId
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|cs
return|;
block|}
specifier|public
name|TransportConnectionState
name|lookupConnectionState
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|TransportConnectionState
name|cs
init|=
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot lookup a connection that had not been registered: "
operator|+
name|connectionId
argument_list|)
throw|;
block|}
return|return
name|cs
return|;
block|}
specifier|public
name|boolean
name|doesHandleMultipleConnectionStates
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|connectionStates
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|connectionStates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|intialize
parameter_list|(
name|TransportConnectionStateRegister
name|other
parameter_list|)
block|{
name|connectionStates
operator|.
name|clear
argument_list|()
expr_stmt|;
name|connectionStates
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|mapStates
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
name|mapStates
parameter_list|()
block|{
name|HashMap
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|ConnectionId
argument_list|,
name|TransportConnectionState
argument_list|>
argument_list|(
name|connectionStates
argument_list|)
decl_stmt|;
return|return
name|map
return|;
block|}
block|}
end_class

end_unit

