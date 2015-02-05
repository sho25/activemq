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
name|util
package|;
end_package

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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|CountDownLatch
import|;
end_import

begin_class
specifier|public
class|class
name|ConsumerThread
extends|extends
name|Thread
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
name|ConsumerThread
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
name|int
name|receiveTimeOut
init|=
literal|3000
decl_stmt|;
name|Destination
name|destination
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|boolean
name|breakOnNull
init|=
literal|true
decl_stmt|;
name|int
name|sleep
decl_stmt|;
name|int
name|transactionBatchSize
decl_stmt|;
name|int
name|received
init|=
literal|0
decl_stmt|;
name|int
name|transactions
init|=
literal|0
decl_stmt|;
name|boolean
name|running
init|=
literal|false
decl_stmt|;
name|CountDownLatch
name|finished
decl_stmt|;
specifier|public
name|ConsumerThread
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
name|String
name|threadName
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" wait until "
operator|+
name|messageCount
operator|+
literal|" messages are consumed"
argument_list|)
expr_stmt|;
try|try
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
while|while
condition|(
name|running
operator|&&
name|received
operator|<
name|messageCount
condition|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|receiveTimeOut
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Received "
operator|+
operator|(
name|msg
operator|instanceof
name|TextMessage
condition|?
operator|(
operator|(
name|TextMessage
operator|)
name|msg
operator|)
operator|.
name|getText
argument_list|()
else|:
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|received
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|breakOnNull
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|transactionBatchSize
operator|>
literal|0
operator|&&
name|received
operator|>
literal|0
operator|&&
name|received
operator|%
name|transactionBatchSize
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Committing transaction: "
operator|+
name|transactions
operator|++
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sleep
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleep
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|finished
operator|!=
literal|null
condition|)
block|{
name|finished
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Consumed: "
operator|+
name|this
operator|.
name|getReceived
argument_list|()
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
try|try
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Consumer thread finished"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getReceived
parameter_list|()
block|{
return|return
name|received
return|;
block|}
specifier|public
name|void
name|setMessageCount
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|this
operator|.
name|messageCount
operator|=
name|messageCount
expr_stmt|;
block|}
specifier|public
name|void
name|setBreakOnNull
parameter_list|(
name|boolean
name|breakOnNull
parameter_list|)
block|{
name|this
operator|.
name|breakOnNull
operator|=
name|breakOnNull
expr_stmt|;
block|}
specifier|public
name|int
name|getTransactionBatchSize
parameter_list|()
block|{
return|return
name|transactionBatchSize
return|;
block|}
specifier|public
name|void
name|setTransactionBatchSize
parameter_list|(
name|int
name|transactionBatchSize
parameter_list|)
block|{
name|this
operator|.
name|transactionBatchSize
operator|=
name|transactionBatchSize
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
specifier|public
name|boolean
name|isBreakOnNull
parameter_list|()
block|{
return|return
name|breakOnNull
return|;
block|}
specifier|public
name|int
name|getReceiveTimeOut
parameter_list|()
block|{
return|return
name|receiveTimeOut
return|;
block|}
specifier|public
name|void
name|setReceiveTimeOut
parameter_list|(
name|int
name|receiveTimeOut
parameter_list|)
block|{
name|this
operator|.
name|receiveTimeOut
operator|=
name|receiveTimeOut
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
specifier|public
name|void
name|setRunning
parameter_list|(
name|boolean
name|running
parameter_list|)
block|{
name|this
operator|.
name|running
operator|=
name|running
expr_stmt|;
block|}
specifier|public
name|int
name|getSleep
parameter_list|()
block|{
return|return
name|sleep
return|;
block|}
specifier|public
name|void
name|setSleep
parameter_list|(
name|int
name|sleep
parameter_list|)
block|{
name|this
operator|.
name|sleep
operator|=
name|sleep
expr_stmt|;
block|}
specifier|public
name|CountDownLatch
name|getFinished
parameter_list|()
block|{
return|return
name|finished
return|;
block|}
specifier|public
name|void
name|setFinished
parameter_list|(
name|CountDownLatch
name|finished
parameter_list|)
block|{
name|this
operator|.
name|finished
operator|=
name|finished
expr_stmt|;
block|}
block|}
end_class

end_unit

