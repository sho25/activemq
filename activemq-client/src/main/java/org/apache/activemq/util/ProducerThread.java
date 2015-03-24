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
name|io
operator|.
name|*
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
name|ProducerThread
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
name|ProducerThread
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|messageCount
init|=
literal|1000
decl_stmt|;
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
name|int
name|sleep
init|=
literal|0
decl_stmt|;
name|boolean
name|persistent
init|=
literal|true
decl_stmt|;
name|int
name|messageSize
init|=
literal|0
decl_stmt|;
name|int
name|textMessageSize
decl_stmt|;
name|long
name|msgTTL
init|=
literal|0L
decl_stmt|;
name|String
name|msgGroupID
init|=
literal|null
decl_stmt|;
name|int
name|transactionBatchSize
decl_stmt|;
name|int
name|transactions
init|=
literal|0
decl_stmt|;
name|int
name|sentCount
init|=
literal|0
decl_stmt|;
name|String
name|message
decl_stmt|;
name|String
name|messageText
init|=
literal|null
decl_stmt|;
name|String
name|payloadUrl
init|=
literal|null
decl_stmt|;
name|byte
index|[]
name|payload
init|=
literal|null
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
name|ProducerThread
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
specifier|public
name|void
name|run
parameter_list|()
block|{
name|MessageProducer
name|producer
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
try|try
block|{
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|persistent
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|msgTTL
argument_list|)
expr_stmt|;
name|initPayLoad
argument_list|()
expr_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Started to calculate elapsed time ...\n"
argument_list|)
expr_stmt|;
name|long
name|tStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|sentCount
operator|=
literal|0
init|;
name|sentCount
operator|<
name|messageCount
operator|&&
name|running
condition|;
name|sentCount
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|sentCount
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Sent: "
operator|+
operator|(
name|message
operator|instanceof
name|TextMessage
condition|?
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
else|:
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transactionBatchSize
operator|>
literal|0
operator|&&
name|sentCount
operator|>
literal|0
operator|&&
name|sentCount
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
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Produced: "
operator|+
name|this
operator|.
name|getSentCount
argument_list|()
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
name|long
name|tEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
operator|(
name|tEnd
operator|-
name|tStart
operator|)
operator|/
literal|1000
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Elapsed time in second : "
operator|+
name|elapsed
operator|+
literal|" s"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|threadName
operator|+
literal|" Elapsed time in milli second : "
operator|+
operator|(
name|tEnd
operator|-
name|tStart
operator|)
operator|+
literal|" milli seconds"
argument_list|)
expr_stmt|;
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
name|producer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|producer
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
block|}
specifier|private
name|void
name|initPayLoad
parameter_list|()
block|{
if|if
condition|(
name|messageSize
operator|>
literal|0
condition|)
block|{
name|payload
operator|=
operator|new
name|byte
index|[
name|messageSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|payload
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|payload
index|[
name|i
index|]
operator|=
literal|'.'
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|answer
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
name|answer
operator|=
name|session
operator|.
name|createBytesMessage
argument_list|()
expr_stmt|;
operator|(
operator|(
name|BytesMessage
operator|)
name|answer
operator|)
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|textMessageSize
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|messageText
operator|==
literal|null
condition|)
block|{
name|messageText
operator|=
name|readInputStream
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"demo.txt"
argument_list|)
argument_list|,
name|textMessageSize
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|payloadUrl
operator|!=
literal|null
condition|)
block|{
name|messageText
operator|=
name|readInputStream
argument_list|(
operator|new
name|URL
argument_list|(
name|payloadUrl
argument_list|)
operator|.
name|openStream
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|messageText
operator|=
name|message
expr_stmt|;
block|}
else|else
block|{
name|messageText
operator|=
name|createDefaultMessage
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|answer
operator|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|messageText
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|msgGroupID
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|msgGroupID
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|answer
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|msgGroupID
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|private
name|String
name|readInputStream
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|messageNumber
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
decl_stmt|;
try|try
block|{
name|char
index|[]
name|buffer
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|buffer
operator|=
operator|new
name|char
index|[
name|size
index|]
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|=
operator|new
name|char
index|[
literal|1024
index|]
expr_stmt|;
block|}
name|int
name|count
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
break|break;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
return|return
name|createDefaultMessage
argument_list|(
name|messageNumber
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|createDefaultMessage
parameter_list|(
name|int
name|messageNumber
parameter_list|)
block|{
return|return
literal|"test message: "
operator|+
name|messageNumber
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
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageCount
return|;
block|}
specifier|public
name|int
name|getSentCount
parameter_list|()
block|{
return|return
name|sentCount
return|;
block|}
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|persistent
return|;
block|}
specifier|public
name|void
name|setPersistent
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
name|this
operator|.
name|persistent
operator|=
name|persistent
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
name|long
name|getMsgTTL
parameter_list|()
block|{
return|return
name|msgTTL
return|;
block|}
specifier|public
name|void
name|setMsgTTL
parameter_list|(
name|long
name|msgTTL
parameter_list|)
block|{
name|this
operator|.
name|msgTTL
operator|=
name|msgTTL
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
name|String
name|getMsgGroupID
parameter_list|()
block|{
return|return
name|msgGroupID
return|;
block|}
specifier|public
name|void
name|setMsgGroupID
parameter_list|(
name|String
name|msgGroupID
parameter_list|)
block|{
name|this
operator|.
name|msgGroupID
operator|=
name|msgGroupID
expr_stmt|;
block|}
specifier|public
name|int
name|getTextMessageSize
parameter_list|()
block|{
return|return
name|textMessageSize
return|;
block|}
specifier|public
name|void
name|setTextMessageSize
parameter_list|(
name|int
name|textMessageSize
parameter_list|)
block|{
name|this
operator|.
name|textMessageSize
operator|=
name|textMessageSize
expr_stmt|;
block|}
specifier|public
name|int
name|getMessageSize
parameter_list|()
block|{
return|return
name|messageSize
return|;
block|}
specifier|public
name|void
name|setMessageSize
parameter_list|(
name|int
name|messageSize
parameter_list|)
block|{
name|this
operator|.
name|messageSize
operator|=
name|messageSize
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
specifier|public
name|String
name|getPayloadUrl
parameter_list|()
block|{
return|return
name|payloadUrl
return|;
block|}
specifier|public
name|void
name|setPayloadUrl
parameter_list|(
name|String
name|payloadUrl
parameter_list|)
block|{
name|this
operator|.
name|payloadUrl
operator|=
name|payloadUrl
expr_stmt|;
block|}
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
block|}
end_class

end_unit

