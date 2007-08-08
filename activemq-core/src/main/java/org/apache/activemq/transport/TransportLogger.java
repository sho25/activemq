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
name|transport
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TransportLogger
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|static
name|int
name|lastId
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|Log
name|log
decl_stmt|;
specifier|public
name|TransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|)
block|{
name|this
argument_list|(
name|next
argument_list|,
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransportLogger
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".Connection:"
operator|+
name|getNextId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|synchronized
specifier|private
specifier|static
name|int
name|getNextId
parameter_list|()
block|{
return|return
operator|++
name|lastId
return|;
block|}
specifier|public
name|TransportLogger
parameter_list|(
name|Transport
name|next
parameter_list|,
name|Log
name|log
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
name|Object
name|rc
init|=
name|super
operator|.
name|request
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"GOT RESPONSE: "
operator|+
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|command
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
name|Object
name|rc
init|=
name|super
operator|.
name|request
argument_list|(
name|command
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"GOT RESPONSE: "
operator|+
name|rc
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Object
name|command
parameter_list|,
name|ResponseCallback
name|responseCallback
parameter_list|)
throws|throws
name|IOException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING ASNYC REQUEST: "
operator|+
name|command
argument_list|)
expr_stmt|;
name|FutureResponse
name|rc
init|=
name|next
operator|.
name|asyncRequest
argument_list|(
name|command
argument_list|,
name|responseCallback
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"SENDING: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"RECEIVED: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|getTransportListener
argument_list|()
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"RECEIVED Exception: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

