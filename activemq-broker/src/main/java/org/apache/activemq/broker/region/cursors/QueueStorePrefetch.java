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
name|region
operator|.
name|cursors
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
name|activemq
operator|.
name|broker
operator|.
name|Broker
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
name|BrokerService
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
name|region
operator|.
name|Queue
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
name|Message
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
name|MessageId
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
name|store
operator|.
name|MessageStore
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
name|store
operator|.
name|memory
operator|.
name|MemoryMessageStore
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
name|store
operator|.
name|memory
operator|.
name|MemoryTransactionStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * persist pending messages pending message (messages awaiting dispatch to a  * consumer) cursor  *  *  */
end_comment

begin_class
class|class
name|QueueStorePrefetch
extends|extends
name|AbstractStoreCursor
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueueStorePrefetch
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MessageStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Broker
name|broker
decl_stmt|;
comment|/**      * Construct it      * @param queue      */
specifier|public
name|QueueStorePrefetch
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|Broker
name|broker
parameter_list|)
block|{
name|super
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|queue
operator|.
name|getMessageStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|messageReference
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|msg
init|=
name|this
operator|.
name|store
operator|.
name|getMessage
argument_list|(
name|messageReference
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
return|return
name|recoverMessage
argument_list|(
name|msg
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|err
init|=
literal|"Failed to retrieve message for id: "
operator|+
name|messageReference
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|err
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|int
name|getStoreSize
parameter_list|()
block|{
try|try
block|{
name|int
name|result
init|=
name|this
operator|.
name|store
operator|.
name|getMessageCount
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get message count"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|long
name|getStoreMessageSize
parameter_list|()
block|{
try|try
block|{
return|return
name|this
operator|.
name|store
operator|.
name|getMessageSize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get message size"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|boolean
name|isStoreEmpty
parameter_list|()
block|{
try|try
block|{
return|return
name|this
operator|.
name|store
operator|.
name|isEmpty
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get message count"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|resetBatch
parameter_list|()
block|{
name|this
operator|.
name|store
operator|.
name|resetBatching
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setBatch
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"{}  setBatch {} seq: {}, loc: {}"
argument_list|,
name|this
argument_list|,
name|messageId
argument_list|,
name|messageId
operator|.
name|getFutureOrSequenceLong
argument_list|()
argument_list|,
name|messageId
operator|.
name|getEntryLocator
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|setBatch
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
name|batchResetNeeded
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doFillBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|hadSpace
operator|=
name|this
operator|.
name|hasSpace
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isPersistent
argument_list|()
operator|||
name|hadSpace
condition|)
block|{
name|this
operator|.
name|store
operator|.
name|recoverNextMessages
argument_list|(
name|this
operator|.
name|maxBatchSize
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|dealWithDuplicates
argument_list|()
expr_stmt|;
comment|// without the index lock
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
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|",store="
operator|+
name|store
return|;
block|}
block|}
end_class

end_unit

