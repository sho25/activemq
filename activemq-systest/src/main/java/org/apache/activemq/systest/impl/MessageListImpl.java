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
name|systest
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|AgentStopper
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
name|systest
operator|.
name|MessageList
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
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

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
name|Date
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
name|List
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * A simple in-JVM implementation of a {@link MessageList}  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|MessageListImpl
extends|extends
name|Assert
implements|implements
name|MessageList
block|{
specifier|private
name|int
name|numberOfMessages
decl_stmt|;
specifier|private
name|int
name|charactersPerMessage
decl_stmt|;
specifier|private
name|List
name|payloads
decl_stmt|;
specifier|private
name|String
name|customHeader
init|=
literal|"testHeader"
decl_stmt|;
specifier|private
name|boolean
name|useTextMessage
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|sentCounter
decl_stmt|;
specifier|public
name|MessageListImpl
parameter_list|(
name|int
name|numberOfMessages
parameter_list|,
name|int
name|charactersPerMessage
parameter_list|)
block|{
name|this
operator|.
name|numberOfMessages
operator|=
name|numberOfMessages
expr_stmt|;
name|this
operator|.
name|charactersPerMessage
operator|=
name|charactersPerMessage
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|payloads
operator|=
operator|new
name|ArrayList
argument_list|(
name|numberOfMessages
argument_list|)
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
name|numberOfMessages
condition|;
name|i
operator|++
control|)
block|{
name|payloads
operator|.
name|add
argument_list|(
name|createTextPayload
argument_list|(
name|i
argument_list|,
name|charactersPerMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|payloads
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|(
name|AgentStopper
name|stopper
parameter_list|)
block|{
name|payloads
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|assertMessagesCorrect
parameter_list|(
name|List
name|actual
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|actualSize
init|=
name|actual
operator|.
name|size
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Consumed so far: "
operator|+
name|actualSize
operator|+
literal|" message(s)"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not enough messages received: "
operator|+
name|actualSize
operator|+
literal|" when expected: "
operator|+
name|getSize
argument_list|()
argument_list|,
name|actualSize
operator|>=
name|getSize
argument_list|()
argument_list|)
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
name|actualSize
condition|;
name|i
operator|++
control|)
block|{
name|assertMessageCorrect
argument_list|(
name|i
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Number of messages received"
argument_list|,
name|getSize
argument_list|()
argument_list|,
name|actualSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertMessageCorrect
parameter_list|(
name|int
name|index
parameter_list|,
name|List
name|actualMessages
parameter_list|)
throws|throws
name|JMSException
block|{
name|Object
name|expected
init|=
name|getPayloads
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|actualMessages
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertHeadersCorrect
argument_list|(
name|message
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|Object
name|actual
init|=
name|getMessagePayload
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message payload for message: "
operator|+
name|index
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|getPayloads
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
name|counter
operator|++
control|)
block|{
name|Object
name|element
init|=
operator|(
name|Object
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|counter
operator|==
name|sentCounter
condition|)
block|{
name|sentCounter
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|session
argument_list|,
name|element
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|appendHeaders
argument_list|(
name|message
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|sendMessages
parameter_list|(
name|Session
name|session
parameter_list|,
name|MessageProducer
name|producer
parameter_list|,
name|int
name|percent
parameter_list|)
throws|throws
name|JMSException
block|{
name|int
name|numberOfMessages
init|=
operator|(
name|getPayloads
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|percent
operator|)
operator|/
literal|100
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|int
name|lastMessageId
init|=
name|sentCounter
operator|+
name|numberOfMessages
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|getPayloads
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
name|counter
operator|<
name|lastMessageId
condition|;
name|counter
operator|++
control|)
block|{
name|Object
name|element
init|=
operator|(
name|Object
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|counter
operator|==
name|sentCounter
condition|)
block|{
name|sentCounter
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|session
argument_list|,
name|element
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|appendHeaders
argument_list|(
name|message
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|getPayloads
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getCustomHeader
parameter_list|()
block|{
return|return
name|customHeader
return|;
block|}
specifier|public
name|void
name|setCustomHeader
parameter_list|(
name|String
name|customHeader
parameter_list|)
block|{
name|this
operator|.
name|customHeader
operator|=
name|customHeader
expr_stmt|;
block|}
specifier|public
name|List
name|getPayloads
parameter_list|()
block|{
return|return
name|payloads
return|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|appendHeaders
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|counter
parameter_list|)
throws|throws
name|JMSException
block|{
name|message
operator|.
name|setIntProperty
argument_list|(
name|getCustomHeader
argument_list|()
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"cheese"
argument_list|,
literal|"Edam"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertHeadersCorrect
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertEquals
argument_list|(
literal|"String property 'cheese' for message: "
operator|+
name|index
argument_list|,
literal|"Edam"
argument_list|,
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"cheese"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"int property '"
operator|+
name|getCustomHeader
argument_list|()
operator|+
literal|"' for message: "
operator|+
name|index
argument_list|,
name|index
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
name|getCustomHeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Object
name|getMessagePayload
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
return|return
name|textMessage
operator|.
name|getText
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|ObjectMessage
condition|)
block|{
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
return|return
name|objectMessage
operator|.
name|getObject
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|Object
name|element
parameter_list|,
name|int
name|counter
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|element
operator|instanceof
name|String
operator|&&
name|useTextMessage
condition|)
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
operator|(
name|String
operator|)
name|element
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|element
operator|==
literal|null
condition|)
block|{
return|return
name|session
operator|.
name|createMessage
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|session
operator|.
name|createObjectMessage
argument_list|(
operator|(
name|Serializable
operator|)
name|element
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Object
name|createTextPayload
parameter_list|(
name|int
name|messageCounter
parameter_list|,
name|int
name|charactersPerMessage
parameter_list|)
block|{
return|return
literal|"Body of message: "
operator|+
name|messageCounter
operator|+
literal|" sent at: "
operator|+
operator|new
name|Date
argument_list|()
return|;
block|}
block|}
end_class

end_unit

