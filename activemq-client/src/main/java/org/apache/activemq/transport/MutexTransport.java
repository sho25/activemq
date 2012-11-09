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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * Thread safe Transport Filter that serializes calls to and from the Transport Stack.  */
end_comment

begin_class
specifier|public
class|class
name|MutexTransport
extends|extends
name|TransportFilter
block|{
specifier|private
specifier|final
name|ReentrantLock
name|writeLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|syncOnCommand
decl_stmt|;
specifier|public
name|MutexTransport
parameter_list|(
name|Transport
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|syncOnCommand
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|MutexTransport
parameter_list|(
name|Transport
name|next
parameter_list|,
name|boolean
name|syncOnCommand
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|syncOnCommand
operator|=
name|syncOnCommand
expr_stmt|;
block|}
annotation|@
name|Override
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
name|syncOnCommand
condition|)
block|{
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|transportListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|next
operator|.
name|asyncRequest
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|next
operator|.
name|request
argument_list|(
name|command
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|writeLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|next
operator|.
name|request
argument_list|(
name|command
argument_list|,
name|timeout
argument_list|)
return|;
block|}
finally|finally
block|{
name|writeLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
specifier|public
name|boolean
name|isSyncOnCommand
parameter_list|()
block|{
return|return
name|syncOnCommand
return|;
block|}
specifier|public
name|void
name|setSyncOnCommand
parameter_list|(
name|boolean
name|syncOnCommand
parameter_list|)
block|{
name|this
operator|.
name|syncOnCommand
operator|=
name|syncOnCommand
expr_stmt|;
block|}
block|}
end_class

end_unit
