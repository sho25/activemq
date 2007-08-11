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
name|tool
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
name|List
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
name|MessageListener
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
comment|/**  * A simple container of messages for performing testing and rendezvous style  * code. You can use this class a {@link MessageListener} and then make  * assertions about how many messages it has received allowing a certain maximum  * amount of time to ensure that the test does not hang forever.  *<p/>  * Also you can chain these instances together with the  * {@link #setParent(MessageListener)} method so that you can aggregate the  * total number of messages consumed across a number of consumers.  *  * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|MemMessageIdList
implements|implements
name|MessageListener
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MemMessageIdList
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|List
name|messageIds
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|Object
name|semaphore
decl_stmt|;
specifier|private
name|boolean
name|verbose
decl_stmt|;
specifier|private
name|MessageListener
name|parent
decl_stmt|;
specifier|private
name|long
name|maximumDuration
init|=
literal|15000L
decl_stmt|;
specifier|public
name|MemMessageIdList
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemMessageIdList
parameter_list|(
name|Object
name|semaphore
parameter_list|)
block|{
name|this
operator|.
name|semaphore
operator|=
name|semaphore
expr_stmt|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|instanceof
name|MemMessageIdList
condition|)
block|{
name|MemMessageIdList
name|thatListMem
init|=
operator|(
name|MemMessageIdList
operator|)
name|that
decl_stmt|;
return|return
name|getMessageIds
argument_list|()
operator|.
name|equals
argument_list|(
name|thatListMem
operator|.
name|getMessageIds
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
return|return
name|messageIds
operator|.
name|hashCode
argument_list|()
operator|+
literal|1
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
return|return
name|messageIds
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * @return all the messages on the list so far, clearing the buffer      */
specifier|public
name|List
name|flushMessages
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|List
name|answer
init|=
operator|new
name|ArrayList
argument_list|(
name|messageIds
argument_list|)
decl_stmt|;
name|messageIds
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
specifier|public
specifier|synchronized
name|List
name|getMessageIds
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
return|return
operator|new
name|ArrayList
argument_list|(
name|messageIds
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|String
name|id
init|=
literal|null
decl_stmt|;
try|try
block|{
name|id
operator|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|messageIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|semaphore
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|verbose
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Received message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|onMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
return|return
name|messageIds
operator|.
name|size
argument_list|()
return|;
block|}
block|}
specifier|public
name|void
name|waitForMessagesToArrive
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Waiting for "
operator|+
name|messageCount
operator|+
literal|" message(s) to arrive"
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
if|if
condition|(
name|hasReceivedMessages
argument_list|(
name|messageCount
argument_list|)
condition|)
block|{
break|break;
block|}
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|duration
operator|>=
name|maximumDuration
condition|)
block|{
break|break;
block|}
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|semaphore
operator|.
name|wait
argument_list|(
name|maximumDuration
operator|-
name|duration
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"End of wait for "
operator|+
name|end
operator|+
literal|" millis and received: "
operator|+
name|getMessageCount
argument_list|()
operator|+
literal|" messages"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasReceivedMessage
parameter_list|()
block|{
return|return
name|getMessageCount
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasReceivedMessages
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
return|return
name|getMessageCount
argument_list|()
operator|>=
name|messageCount
return|;
block|}
specifier|public
name|boolean
name|isVerbose
parameter_list|()
block|{
return|return
name|verbose
return|;
block|}
specifier|public
name|void
name|setVerbose
parameter_list|(
name|boolean
name|verbose
parameter_list|)
block|{
name|this
operator|.
name|verbose
operator|=
name|verbose
expr_stmt|;
block|}
specifier|public
name|MessageListener
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**      * Allows a parent listener to be specified such as to aggregate messages      * consumed across consumers      */
specifier|public
name|void
name|setParent
parameter_list|(
name|MessageListener
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
block|}
end_class

end_unit

