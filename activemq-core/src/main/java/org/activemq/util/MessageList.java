begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|util
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
name|javax
operator|.
name|jms
operator|.
name|TextMessage
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
comment|/**  * A simple container for performing testing and rendezvous style code.  *   * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|MessageList
extends|extends
name|Assert
implements|implements
name|MessageListener
block|{
specifier|private
name|List
name|messages
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
specifier|public
name|MessageList
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
name|MessageList
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
name|messages
argument_list|)
decl_stmt|;
name|messages
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
name|getMessages
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
name|messages
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|synchronized
name|List
name|getTextMessages
parameter_list|()
block|{
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|ArrayList
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|messages
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
try|try
block|{
name|TextMessage
name|m
init|=
operator|(
name|TextMessage
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|l
operator|.
name|add
argument_list|(
literal|""
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|l
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
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|messages
operator|.
name|add
argument_list|(
name|message
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"###�received message: "
operator|+
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
name|messages
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
name|System
operator|.
name|out
operator|.
name|println
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
synchronized|synchronized
init|(
name|semaphore
init|)
block|{
name|semaphore
operator|.
name|wait
argument_list|(
literal|4000
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
name|System
operator|.
name|out
operator|.
name|println
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"End of wait for "
operator|+
name|end
operator|+
literal|" millis"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Performs a testing assertion that the correct number of messages have      * been received      *       * @param messageCount      */
specifier|public
name|void
name|assertMessagesReceived
parameter_list|(
name|int
name|messageCount
parameter_list|)
block|{
name|waitForMessagesToArrive
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected number of messages when received: "
operator|+
name|getMessages
argument_list|()
argument_list|,
name|messageCount
argument_list|,
name|getMessageCount
argument_list|()
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
block|}
end_class

end_unit

