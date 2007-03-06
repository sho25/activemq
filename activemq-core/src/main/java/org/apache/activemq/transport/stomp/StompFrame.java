begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
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
name|Endpoint
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
name|Response
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * Represents all the data in a STOMP frame.  *   * @author<a href="http://hiramchirino.com">chirino</a>   */
end_comment

begin_class
specifier|public
class|class
name|StompFrame
implements|implements
name|Command
block|{
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|NO_DATA
init|=
operator|new
name|byte
index|[]
block|{}
decl_stmt|;
specifier|private
name|String
name|action
decl_stmt|;
specifier|private
name|Map
name|headers
init|=
name|Collections
operator|.
name|EMPTY_MAP
decl_stmt|;
specifier|private
name|byte
index|[]
name|content
init|=
name|NO_DATA
decl_stmt|;
specifier|public
name|StompFrame
parameter_list|(
name|String
name|command
parameter_list|,
name|HashMap
name|headers
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|command
expr_stmt|;
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|StompFrame
parameter_list|()
block|{ 	}
specifier|public
name|String
name|getAction
parameter_list|()
block|{
return|return
name|action
return|;
block|}
specifier|public
name|void
name|setAction
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|command
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|getContent
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|Map
name|getHeaders
parameter_list|()
block|{
return|return
name|headers
return|;
block|}
specifier|public
name|void
name|setHeaders
parameter_list|(
name|Map
name|headers
parameter_list|)
block|{
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
block|}
comment|//
comment|// Methods in the Command interface
comment|//
specifier|public
name|int
name|getCommandId
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|Endpoint
name|getFrom
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Endpoint
name|getTo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isBrokerInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageAck
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatch
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isMessageDispatchNotification
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isResponse
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isResponseRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isShutdownInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isWireFormatInfo
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|setCommandId
parameter_list|(
name|int
name|value
parameter_list|)
block|{ 	}
specifier|public
name|void
name|setFrom
parameter_list|(
name|Endpoint
name|from
parameter_list|)
block|{ 	}
specifier|public
name|void
name|setResponseRequired
parameter_list|(
name|boolean
name|responseRequired
parameter_list|)
block|{ 	}
specifier|public
name|void
name|setTo
parameter_list|(
name|Endpoint
name|to
parameter_list|)
block|{ 	}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|Map
name|headers
init|=
name|getHeaders
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|headers
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|buffer
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|getContent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|getContent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

