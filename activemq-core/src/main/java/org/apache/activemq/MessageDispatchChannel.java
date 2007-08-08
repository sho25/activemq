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
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageDispatch
import|;
end_import

begin_class
specifier|public
class|class
name|MessageDispatchChannel
block|{
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|MessageDispatch
argument_list|>
name|list
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|public
name|MessageDispatchChannel
parameter_list|()
block|{
name|this
operator|.
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|MessageDispatch
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|enqueue
parameter_list|(
name|MessageDispatch
name|message
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|list
operator|.
name|addLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|mutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|enqueueFirst
parameter_list|(
name|MessageDispatch
name|message
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|list
operator|.
name|addFirst
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|mutex
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|list
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
comment|/**      * Used to get an enqueued message. The amount of time this method blocks is      * based on the timeout value. - if timeout==-1 then it blocks until a      * message is received. - if timeout==0 then it it tries to not block at      * all, it returns a message if it is available - if timeout>0 then it      * blocks up to timeout amount of time. Expired messages will consumed by      * this method.      *       * @throws JMSException      * @return null if we timeout or if the consumer is closed.      * @throws InterruptedException      */
specifier|public
name|MessageDispatch
name|dequeue
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
comment|// Wait until the consumer is ready to deliver messages.
while|while
condition|(
name|timeout
operator|!=
literal|0
operator|&&
operator|!
name|closed
operator|&&
operator|(
name|list
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|running
operator|)
condition|)
block|{
if|if
condition|(
name|timeout
operator|==
operator|-
literal|1
condition|)
block|{
name|mutex
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|mutex
operator|.
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|closed
operator|||
operator|!
name|running
operator|||
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|list
operator|.
name|removeFirst
argument_list|()
return|;
block|}
block|}
specifier|public
name|MessageDispatch
name|dequeueNoWait
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|closed
operator|||
operator|!
name|running
operator|||
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|list
operator|.
name|removeFirst
argument_list|()
return|;
block|}
block|}
specifier|public
name|MessageDispatch
name|peek
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|closed
operator|||
operator|!
name|running
operator|||
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|list
operator|.
name|getFirst
argument_list|()
return|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
name|mutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
block|}
specifier|public
name|Object
name|getMutex
parameter_list|()
block|{
return|return
name|mutex
return|;
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
name|List
argument_list|<
name|MessageDispatch
argument_list|>
name|removeAll
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|ArrayList
argument_list|<
name|MessageDispatch
argument_list|>
name|rc
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageDispatch
argument_list|>
argument_list|(
name|list
argument_list|)
decl_stmt|;
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|rc
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
name|mutex
init|)
block|{
return|return
name|list
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

