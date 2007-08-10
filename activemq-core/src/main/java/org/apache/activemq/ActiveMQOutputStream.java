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
name|io
operator|.
name|OutputStream
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
name|javax
operator|.
name|jms
operator|.
name|InvalidDestinationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|ActiveMQBytesMessage
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
name|ActiveMQDestination
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
name|ActiveMQMessage
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
name|ProducerInfo
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
name|IOExceptionSupport
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQOutputStream
extends|extends
name|OutputStream
implements|implements
name|Disposable
block|{
comment|// Send down 64k messages.
specifier|protected
name|int
name|count
decl_stmt|;
specifier|final
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|64
operator|*
literal|1024
index|]
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
decl_stmt|;
specifier|private
specifier|final
name|ProducerInfo
name|info
decl_stmt|;
specifier|private
name|long
name|messageSequence
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
specifier|final
name|int
name|deliveryMode
decl_stmt|;
specifier|private
specifier|final
name|int
name|priority
decl_stmt|;
specifier|private
specifier|final
name|long
name|timeToLive
decl_stmt|;
specifier|public
name|ActiveMQOutputStream
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|ProducerId
name|producerId
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeToLive
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|deliveryMode
operator|=
name|deliveryMode
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|properties
argument_list|)
expr_stmt|;
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidDestinationException
argument_list|(
literal|"Don't understand null destinations"
argument_list|)
throw|;
block|}
name|this
operator|.
name|info
operator|=
operator|new
name|ProducerInfo
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
name|this
operator|.
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|addOutputStream
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|asyncSendPacket
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Send an EOS style empty message to signal EOS.
name|send
argument_list|(
operator|new
name|ActiveMQMessage
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|dispose
argument_list|()
expr_stmt|;
name|this
operator|.
name|connection
operator|.
name|asyncSendPacket
argument_list|(
name|info
operator|.
name|createRemoveCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|this
operator|.
name|connection
operator|.
name|removeOutputStream
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
index|[
name|count
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|int
name|max
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|count
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|count
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|len
operator|-=
name|max
expr_stmt|;
name|count
operator|+=
name|max
expr_stmt|;
name|off
operator|+=
name|max
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|writeBytes
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|send
argument_list|(
name|msg
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|count
operator|=
literal|0
expr_stmt|;
block|}
comment|/**      * @param msg      * @throws JMSException      */
specifier|private
name|void
name|send
parameter_list|(
name|ActiveMQMessage
name|msg
parameter_list|,
name|boolean
name|eosMessage
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|properties
operator|.
name|keySet
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
name|String
name|key
init|=
operator|(
name|String
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|properties
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setObjectProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|msg
operator|.
name|setType
argument_list|(
literal|"org.apache.activemq.Stream"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setGroupID
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|eosMessage
condition|)
block|{
name|msg
operator|.
name|setGroupSequence
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|setGroupSequence
argument_list|(
operator|(
name|int
operator|)
name|messageSequence
argument_list|)
expr_stmt|;
block|}
name|MessageId
name|id
init|=
operator|new
name|MessageId
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|,
name|messageSequence
operator|++
argument_list|)
decl_stmt|;
name|connection
operator|.
name|send
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|,
name|msg
argument_list|,
name|id
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|,
operator|!
name|eosMessage
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ActiveMQOutputStream { producerId="
operator|+
name|info
operator|.
name|getProducerId
argument_list|()
operator|+
literal|" }"
return|;
block|}
block|}
end_class

end_unit

